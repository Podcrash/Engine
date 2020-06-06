package com.podcrash.api.db.tables;

import com.mongodb.Block;
import com.mongodb.client.model.Filters;
import com.mongodb.client.result.DeleteResult;
import com.podcrash.api.db.DBUtils;
import com.podcrash.api.db.MongoBaseTable;
import com.podcrash.api.db.pojos.FriendLink;
import com.podcrash.api.db.pojos.InvictaPlayer;
import com.podcrash.api.db.pojos.PojoHelper;
import com.podcrash.api.db.pojos.Rank;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class FriendsTable extends MongoBaseTable {
    public FriendsTable() { super("friends"); }

    @Override
    public DataTableType getDataTableType() { return DataTableType.FRIENDS; }

    @Override
    public void createTable() {
        // maybe add an index here in the future, no clue how they work rn though
    }

    public void addFriendLink(UUID playerOne, UUID playerTwo, boolean pending) {
        FriendLink link = PojoHelper.createFriendLink(playerOne, playerTwo, pending);

        CompletableFuture<Void> future = new CompletableFuture<>();
        getCollection(FriendLink.class).insertOne(link, (res, t) -> {
            DBUtils.handleDuplicateKeyException(t);
            future.complete(res);
        });
        futureGuaranteeGet(future);
    }

    public void removeFriendLink(UUID playerOne, UUID playerTwo) {
        CompletableFuture<DeleteResult> delete = new CompletableFuture<>();
        getCollection(FriendLink.class).deleteOne(Filters.or(
            Filters.and(Filters.eq("playerOne", playerOne), Filters.eq("playerTwo", playerTwo)),
            Filters.and(Filters.eq("playerTwo", playerOne), Filters.eq("playerOne", playerTwo))),
            ((result, t) -> {
                DBUtils.handleThrowables(t);
                delete.complete(result);
            }));
        futureGuaranteeGet(delete);
    }

    public CompletableFuture<Set<UUID>> getFriendLinksAsync(UUID sender, boolean includePending) {
        Set<UUID> result = new HashSet<>();

        Block<FriendLink> addLink = (friendLink) -> {
            if (friendLink.getPlayerOne().equals(sender)) {
                result.add(friendLink.getPlayerTwo());
            } else {
                result.add(friendLink.getPlayerOne());
            }
        };
        CompletableFuture<Set<UUID>> future = new CompletableFuture<>();
        getCollection(FriendLink.class).find().filter(Filters.or(
            Filters.eq("playerOne", sender),
            Filters.eq("playerTwo", sender)))
            .forEach(addLink, (res, t) -> {
                DBUtils.handleThrowables(t);
                future.complete(result);
            });
        futureGuaranteeGet(future);

        return future;
    }

    public CompletableFuture<Boolean> hasFriendLinkAsync(UUID playerOne, UUID playerTwo) {
        CompletableFuture<Boolean> result = new CompletableFuture<>();
        getCollection(FriendLink.class).find().filter(Filters.or(
                Filters.and(Filters.eq("playerOne", playerOne), Filters.eq("playerTwo", playerTwo)),
                Filters.and(Filters.eq("playerTwo", playerOne), Filters.eq("playerOne", playerTwo))))
                .first((friendLink, t) -> {
                    DBUtils.handleThrowables(t);
                    result.complete(friendLink != null);
                });
        return result;
    }
}
