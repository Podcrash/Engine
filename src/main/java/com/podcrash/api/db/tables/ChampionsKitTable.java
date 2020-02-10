package com.podcrash.api.db.tables;

//static imports are recommended to make the code look cleaner

import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Updates.set;

import com.mongodb.client.model.Updates;
import com.mongodb.client.result.UpdateResult;
import com.podcrash.api.db.DBUtils;
import com.podcrash.api.db.IPlayerDB;
import com.podcrash.api.db.MongoBaseTable;
import com.podcrash.api.db.pojos.ConquestGameData;
import com.podcrash.api.db.pojos.GameData;
import com.podcrash.api.db.pojos.InvictaPlayer;
import com.podcrash.api.db.pojos.PojoHelper;
import com.podcrash.api.plugin.Pluginizer;
import org.bson.conversions.Bson;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Logger;

/**
 * clasz, build_id, jsondata --> clasz + build_id, jsondata
 */
public class ChampionsKitTable extends MongoBaseTable implements IPlayerDB {
    public ChampionsKitTable() {
        super("championskits");
    }

    @Override
    public DataTableType getDataTableType() {
        return DataTableType.KITS;
    }

    @Override
    public void createTable() {

    }


    /**
     * Adds the championskits column to the players table if they don't already have it
     * @param uuid
     */
    private void evaluate(UUID uuid) {
        InvictaPlayer playerDoc = getPlayerDocumentSync(uuid);

        if(playerDoc.getGameData().containsKey("conquest")) return;

        ConquestGameData conquestData = PojoHelper.createConquestGameData();

        CompletableFuture<UpdateResult> future = new CompletableFuture<>();
        getPlayerTable().getCollection(InvictaPlayer.class).updateOne(eq("uuid", uuid), Updates.set("gameData.conquest", conquestData), (res, t) -> {
            DBUtils.handleThrowables(t);
            future.complete(res);
        });
        futureGuaranteeGet(future);
    }
    private CompletableFuture<ConquestGameData> getKitDocumentAsync(UUID uuid) {
        evaluate(uuid);
        return getPlayerDocumentAsync(uuid).thenApplyAsync(player -> (ConquestGameData) player.getGameData().get("conquest"));
    }
    private ConquestGameData getKitDocumentSync(UUID uuid) {
        return futureGuaranteeGet(getKitDocumentAsync(uuid));
    }

    /**
     * TODO: Optimize queries
     *
     * @param uuid
     * @param clasz
     * @param build_id
     * @return
     */
    public CompletableFuture<String> getJSONDataAsync(UUID uuid, String clasz, int build_id) {
        //TODO: Find out if this works
        CompletableFuture<ConquestGameData> kitDocument = getKitDocumentAsync(uuid);
        return kitDocument.thenApplyAsync((kits -> (String) kits.getBuilds().get(clasz + build_id)), SERVICE);
    }
    public String getJSONData(UUID uuid, String clasz, int build_id) {
        CompletableFuture<String> data = getJSONDataAsync(uuid, clasz, build_id);
        return futureGuaranteeGet(data);
    }

    private void updateSync(Bson query, Bson update) {
        CompletableFuture<UpdateResult> updateResult = new CompletableFuture<>();
        getPlayerTable().getCollection(InvictaPlayer.class).updateOne(query, update, (res, t) -> {
            DBUtils.handleThrowables(t);
            updateResult.complete(res);
        });
        futureGuaranteeGet(updateResult);
    }
    public void set(UUID uuid, String clasz, int build_id, String data) {
        String key = "gameData.conquest.builds." + clasz + build_id;
        //data = value
        Bson update = Updates.set(key, data);
        updateSync(eq("uuid", uuid), update);
    }

    public void alter(UUID uuid, String clasz, int build_id, String data) {
        String key = "gameData.conquest.builds." + clasz + build_id;
        //data = value
        Bson update = Updates.set(key, data);
        updateSync(eq("uuid", uuid), update);
    }

    public void delete(UUID uuid, String clasz, int build_id) {
        String key = "gameData.conquest.builds." + clasz + build_id;
        //data = value
        Bson update = Updates.unset(key);
        updateSync(eq("uuid", uuid), update);
    }

}
