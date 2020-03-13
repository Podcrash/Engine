package com.podcrash.api.db.tables;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.grinderwolf.swm.api.exceptions.UnknownWorldException;
import com.grinderwolf.swm.api.exceptions.WorldInUseException;
import com.grinderwolf.swm.api.loaders.SlimeLoader;
import com.mongodb.Block;
import com.mongodb.MongoException;
import com.mongodb.async.client.MongoDatabase;
import com.mongodb.async.client.gridfs.AsyncInputStream;
import com.mongodb.async.client.gridfs.AsyncOutputStream;
import com.mongodb.async.client.gridfs.GridFSBucket;
import com.mongodb.async.client.gridfs.GridFSBuckets;
import com.mongodb.async.client.gridfs.helpers.AsyncStreamHelper;
import com.mongodb.client.gridfs.model.GridFSDownloadOptions;
import com.mongodb.client.gridfs.model.GridFSFile;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.IndexOptions;
import com.mongodb.client.model.Indexes;
import com.mongodb.client.model.Updates;
import com.mongodb.client.result.UpdateResult;
import com.podcrash.api.db.BaseTable;
import com.podcrash.api.db.DBUtils;
import com.podcrash.api.db.MongoBaseTable;
import org.bson.Document;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.channels.AsynchronousFileChannel;
import java.rmi.activation.UnknownObjectException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

/**
 * Uses the exact same things as the default:
 * https://github.com/Grinderwolf/Slime-World-Manager/blob/develop/slimeworldmanager-plugin/src/main/java/com/grinderwolf/swm/plugin/loaders/mongo/MongoLoader.java
 * This is just to avoid using the config
 */
public class WorldLoader extends MongoBaseTable implements SlimeLoader {// World locking executor service
    private static final ScheduledExecutorService SERVICE = Executors.newScheduledThreadPool(2, new ThreadFactoryBuilder()
            .setNameFormat("SWM MongoDB Lock Pool Thread #%1$d").build());
    private final long MAX_LOCK_TIME = 300000L;
    private final long LOCK_INTERVAL = 60000L;
    private final Map<String, ScheduledFuture> lockedWorlds;

    public WorldLoader() {
        super("worlds");
        lockedWorlds = new HashMap<>();
    }

    @SuppressWarnings("deprecation")
    @Override
    public byte[] loadWorld(final String worldName, boolean readOnly) throws UnknownWorldException, WorldInUseException, IOException {
        CompletableFuture<ByteArrayOutputStream> future = new CompletableFuture<>();
        getCollection().find(Filters.eq("name", worldName)).first((res, t) -> {
            DBUtils.handleThrowables(t);
            if(res == null) {
                future.completeExceptionally(new UnknownWorldException(worldName));
                return;
            }
            if(!readOnly) {
                long lockedMillis = res.getLong("locked");
                if(System.currentTimeMillis() - lockedMillis <= MAX_LOCK_TIME)
                    future.completeExceptionally(new WorldInUseException(worldName));

                updateLock(worldName, true);
            }

            GridFSBucket bucket = GridFSBuckets.create(getDatabase(), getName());
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            AsyncOutputStream asyncStream = AsyncStreamHelper.toAsyncOutputStream(stream);
            bucket.downloadToStream(worldName, asyncStream, (result, t1) -> {
                DBUtils.handleThrowables(t1);
                future.complete(stream);
            });
        });

        return futureGuaranteeGet(future).toByteArray();
    }

    @SuppressWarnings("deprecation")
    private void updateLock(String worldName, boolean forceSchedule) {
        getCollection().updateOne(Filters.eq("name", worldName), Updates.set("locked", System.currentTimeMillis()), (res,t) ->
            DBUtils.handleThrowables(t)
        );
        if (forceSchedule || lockedWorlds.containsKey(worldName)) { // Only schedule another update if the world is still on the map
            lockedWorlds.put(worldName, SERVICE.schedule(() -> updateLock(worldName, false), LOCK_INTERVAL, TimeUnit.MILLISECONDS));
        }
    }

    @SuppressWarnings("deprecation")
    @Override
    public boolean worldExists(String worldName) {
        CompletableFuture<Boolean> found = new CompletableFuture<>();
        getCollection().find(Filters.eq("name", worldName)).first((res, t) -> {
            DBUtils.handleThrowables(t);
            found.complete(res != null);
        });
        return futureGuaranteeGet(found);
    }

    @SuppressWarnings("deprecation")
    @Override
    public List<String> listWorlds() {
        List<String> list = new ArrayList<>();
        Block<Document> listBlock = (doc) -> {
            String name = doc.getString("name");
            list.add(name);
        };
        CompletableFuture<Void> done = new CompletableFuture<>();
        getCollection().find().forEach(listBlock, (result, t) -> {
            DBUtils.handleThrowables(t);
            done.complete(result);
        });

        futureGuaranteeGet(done);
        return list;
    }

    @SuppressWarnings("deprecation")
    @Override
    public void saveWorld(String worldName, byte[] serializedWorld, boolean lock) throws IOException {
        GridFSBucket bucket = GridFSBuckets.create(getDatabase(), getName());
        CountDownLatch latch = new CountDownLatch(1);
        bucket.find(Filters.eq("filename", worldName)).first((result, t) -> {
            DBUtils.handleThrowables(t);
            if(result != null)
                bucket.rename(result.getObjectId(), worldName + "_backup", ((result1, t1) -> {
                    DBUtils.handleThrowables(t);
                    latch.countDown();
                }));
            else latch.countDown();
        });

        try {
            latch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        CountDownLatch latch2 = new CountDownLatch(1);
        AsyncInputStream stream = AsyncStreamHelper.toAsyncInputStream(serializedWorld);
        bucket.uploadFromStream(worldName, stream, (result, t) -> latch2.countDown());

        getCollection().find(Filters.eq("name")).first((result, t) -> {
            DBUtils.handleThrowables(t);
            long lockMillis = lock ? System.currentTimeMillis() : 0L;
            if(result == null) {
                CountDownLatch latch3 = new CountDownLatch(1);
                Document doc = new Document("name", worldName).append("locked", lockMillis);
                getCollection().insertOne(doc, (result1, t1) -> latch3.countDown());
                try {
                    latch3.await();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }else if (System.currentTimeMillis() - result.getLong("locked") > MAX_LOCK_TIME && lock) {
                updateLock(worldName, true);
            }
        });
    }

    @SuppressWarnings("deprecation")
    @Override
    public void unlockWorld(String worldName) throws UnknownWorldException, IOException {
        ScheduledFuture future = lockedWorlds.remove(worldName);

        if (future != null) {
            future.cancel(false);
        }

        CompletableFuture<UpdateResult> updateResultF = new CompletableFuture<>();
        getCollection().updateOne(Filters.eq("name", worldName), Updates.set("locked", 0L), (result, t) -> {
            DBUtils.handleThrowables(t);
            updateResultF.complete(result);
        });
        if(futureGuaranteeGet(updateResultF).getMatchedCount() == 0)
            throw new UnknownWorldException(worldName);
    }

    @SuppressWarnings("deprecation")
    @Override
    public boolean isWorldLocked(String worldName) throws UnknownWorldException, IOException {
        if(lockedWorlds.containsKey(worldName)) return true;

        CompletableFuture<Boolean> truth = new CompletableFuture<>();
        getCollection().find(Filters.eq("name", worldName)).first((result, t) -> {
            DBUtils.handleThrowables(t);
            if(result == null) truth.completeExceptionally(new UnknownWorldException(worldName));
            else truth.complete(System.currentTimeMillis() - result.getLong("locked") <= MAX_LOCK_TIME);
        });

        return futureGuaranteeGet(truth);
    }

    @SuppressWarnings("deprecation")
    @Override
    public void deleteWorld(String worldName) throws UnknownWorldException, IOException {
        ScheduledFuture future = lockedWorlds.remove(worldName);
        if(future != null) future.cancel(false);

        CompletableFuture<GridFSFile> fileFuture = new CompletableFuture<>();
        GridFSBucket bucket = GridFSBuckets.create(getDatabase(), getName());
        bucket.find(Filters.eq("filename", worldName)).first((result, t) -> {
            DBUtils.handleThrowables(t);
            if(result == null) fileFuture.completeExceptionally(new UnknownWorldException(worldName));
            else {
                bucket.delete(result.getObjectId(), (result1, t1) -> DBUtils.handleThrowables(t1));
                getCollection().find(Filters.eq("filename", worldName + "_backup")).first((result1, t1) -> {
                    DBUtils.handleThrowables(t1);
                    if(result1 != null)
                        bucket.delete(result.getObjectId(), (result2, t2) -> DBUtils.handleThrowables(t1));
                });
                fileFuture.complete(result);
            }
        });

        CountDownLatch latch = new CountDownLatch(1);
        getCollection().deleteOne(Filters.eq("name", worldName), (result, t) -> latch.countDown());
        futureGuaranteeGet(fileFuture);
        try {
            latch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public DataTableType getDataTableType() {
        return DataTableType.WORLDS;
    }

    @SuppressWarnings("deprecation")
    @Override
    public void createTable() {
        CountDownLatch latch = new CountDownLatch(1);
        getCollection().createIndex(Indexes.ascending("name"), new IndexOptions().unique(true), (res, t) -> {
            DBUtils.handleThrowables(t);
            latch.countDown();
        });
        try {
            latch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
