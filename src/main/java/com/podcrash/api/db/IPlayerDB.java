package com.podcrash.api.db;

import com.podcrash.api.db.pojos.InvictaPlayer;
import com.podcrash.api.db.tables.DataTableType;
import com.podcrash.api.db.tables.PlayerTable;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public interface IPlayerDB {
    default PlayerTable getPlayerTable() {
        return TableOrganizer.getTable(DataTableType.PLAYERS);
    }

    default InvictaPlayer getPlayerDocumentSync(UUID uuid, String... fields) {
        return getPlayerTable().getPlayerDocumentSync(uuid, fields);
    }
    default CompletableFuture<InvictaPlayer> getPlayerDocumentAsync(UUID uuid, String... fields) {
        return getPlayerTable().getPlayerDocumentAsync(uuid, fields);
    }
}
