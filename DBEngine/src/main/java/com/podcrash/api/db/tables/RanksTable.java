package com.podcrash.api.db.tables;

import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Filters.and;

import com.mongodb.Block;
import com.mongodb.client.model.*;
import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.UpdateResult;
import com.podcrash.api.db.DBUtils;
import com.podcrash.api.db.IPlayerDB;
import com.podcrash.api.db.MongoBaseTable;
import com.podcrash.api.db.pojos.InvictaPlayer;
import com.podcrash.api.db.pojos.Rank;
import org.bukkit.Bukkit;

import java.util.*;
import java.util.concurrent.CompletableFuture;

/**
 * roles: name:string, permissions:list[string]
 */
public class RanksTable extends MongoBaseTable implements IPlayerDB {
    public RanksTable() {
        super("ranks");
    }

    @Override
    public DataTableType getDataTableType() {
        return DataTableType.PERMISSIONS;
    }

    @Override
    public void createTable() {
        List<IndexModel> models = new ArrayList<>();
        models.add(new IndexModel(Indexes.ascending("name"), new IndexOptions().unique(true)));
        models.add(new IndexModel(Indexes.ascending("permissions"),
                new IndexOptions().collation(Collation.builder().caseLevel(false).locale("en_US").build())));
        CompletableFuture<List<String>> future = new CompletableFuture<>();
        getCollection().createIndexes(models, (res, t) -> {
            DBUtils.handleThrowables(t);
            future.complete(res);
        });
        futureGuaranteeGet(future);
    }


    /**
     *
     * @param roleName
     * @param color MUST BE THE BUKKIT CHATCOLOR
     * @param position
     */
    public void addRank(String roleName, String color, int position) {
        Rank rank = new Rank();
        rank.setName(roleName);
        rank.setColor(color);
        rank.setPermissions(new HashSet<>());
        rank.setPosition(position);
        CompletableFuture<Void> future = new CompletableFuture<>();
        getCollection(Rank.class).insertOne(rank, (res, t)  -> {
            DBUtils.handleThrowables(t);
            future.complete(res);
        });
        futureGuaranteeGet(future);
    }
    public CompletableFuture<Rank> getRankAsync(String name) {
        CompletableFuture<Rank> futureRank = new CompletableFuture<>();
        getCollection(Rank.class).find(Filters.eq("name", name)).first(((result, t) -> {
            DBUtils.handleThrowables(t);
            futureRank.complete(result);
        }));
        return futureRank;
    }
    public Rank getRankSync(String name) {
        return futureGuaranteeGet(getRankAsync(name));
    }
    public void removeRole(String role) {
        CompletableFuture<DeleteResult> delete = new CompletableFuture<>();
        getCollection(Rank.class).deleteOne(Filters.eq("name", role), ((result, t) -> {
            DBUtils.handleThrowables(t);
            delete.complete(result);
        }));
        futureGuaranteeGet(delete);
    }
    public void addPermission(String role, String permission) {
        CompletableFuture<UpdateResult> future = new CompletableFuture<>();
        getCollection(Rank.class).updateOne(Filters.eq("name", role), Updates.push("permissions", permission), (res, t) -> {
            DBUtils.handleThrowables(t);
            future.complete(res);
        });
        futureGuaranteeGet(future);
    }
    public void removePermission(String role, String permission) {
        CompletableFuture<UpdateResult> future = new CompletableFuture<>();
        getCollection(Rank.class).updateOne(Filters.eq("name", role), Updates.push("permissions", permission), (res, t) -> {
            DBUtils.handleThrowables(t);
            future.complete(res);
        });
        futureGuaranteeGet(future);
    }

    public CompletableFuture<Set<String>> getRolePermissionsAsync(String role) {
        CompletableFuture<Set<String>> future = new CompletableFuture<>();
        getCollection(Rank.class).find(Filters.eq("name", role)).first((result, t) -> {
            DBUtils.handleThrowables(t);
            future.complete(result.getPermissions());
        });
        return future;
    }
    public Set<String> getPermissionsSync(String role) {
        return futureGuaranteeGet(getRolePermissionsAsync(role));
    }
    //
    // PLAYER
    //
    public CompletableFuture<List<String>> getAllPermissionsAsync(UUID uuid) {
        return getPlayerDocumentAsync(uuid).thenApplyAsync(player -> {
            Set<String> perms = player.getExtraPerms();

            List<String> collected = new ArrayList<>(perms);

            Set<String> ranks = player.getRanks();
            Block<Rank> block = (rank) -> collected.addAll(rank.getPermissions());
            CompletableFuture<Void> future = new CompletableFuture<>();
            getCollection(Rank.class).find(Filters.in("ranks", ranks)).forEach(block, (res, t) -> {
                DBUtils.handleThrowables(t);
                future.complete(res);
            });

            futureGuaranteeGet(future);
            return collected;
        }, SERVICE);
    }

    public CompletableFuture<Set<Rank>> getRanksAsync(UUID uuid) {
        return getPlayerDocumentAsync(uuid, "ranks").thenApplyAsync(player -> {
            Set<Rank> output = new HashSet<>();

            if (player == null) return output;
            Set<String> ranks = player.getRanks();
            if (ranks == null) return output;
            Block<Rank> addRank = (rank) -> {
                if (ranks.contains(rank.getName())) output.add(rank);
            };
            CompletableFuture<Void> future = new CompletableFuture<>();
            getCollection(Rank.class).find().forEach(addRank, (res, t) -> {
                DBUtils.handleThrowables(t);
                future.complete(res);
            });
            futureGuaranteeGet(future);

            return output;
        });
    }
    public Set<Rank> getRanksSync(UUID uuid) {
        return futureGuaranteeGet(getRanksAsync(uuid));
    }
    public void addRole(UUID uuid, String role) {
        CompletableFuture<UpdateResult> future = new CompletableFuture<>();
        getCollection("players").updateOne(Filters.eq("uuid", uuid), Updates.push("ranks", role), (res, t) -> {
            DBUtils.handleThrowables(t);
            future.complete(res);
        });
        futureGuaranteeGet(future);
    }

    public CompletableFuture<Boolean> hasRoleAsync(UUID uuid, final String role) {
        return getRanksAsync(uuid).thenApplyAsync(ranks -> {
            for(Rank r : ranks) {
                if (r.getName().equals(role)) return true;
            }
            return false;
        });
    }
    public boolean hasRoleSync(UUID uuid, String role) {
        return futureGuaranteeGet(hasRoleAsync(uuid, role));
    }

    public void removeRole(UUID uuid, String role) {
        CompletableFuture<UpdateResult> future = new CompletableFuture<>();
        getCollection("players").updateOne(Filters.eq("uuid", uuid), Updates.pull("ranks", role), (res, t) -> {
            DBUtils.handleThrowables(t);
            future.complete(res);
        });
        futureGuaranteeGet(future);
    }
}
