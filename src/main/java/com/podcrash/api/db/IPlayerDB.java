package com.podcrash.api.db;

import org.bson.Document;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public interface IPlayerDB {
    default PlayerTable getPlayerTable() {
        return TableOrganizer.getTable(DataTableType.PLAYERS);
    }

    default Document getPlayerDocumentSync(UUID uuid) {
        return getPlayerTable().getPlayerDocumentSync(uuid);
    }
    default CompletableFuture<Document> getPlayerDocumentAsync(UUID uuid) {
        return getPlayerTable().getPlayerDocumentAsync(uuid);
    }

    @Deprecated
    default long getID(UUID uuid) {
        return 0;
    }
}
