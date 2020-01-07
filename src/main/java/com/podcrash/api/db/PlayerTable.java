package com.podcrash.api.db;


import nu.studer.sample.Tables;
import nu.studer.sample.tables.Players;
import org.jooq.*;
import org.jooq.impl.DSL;
import org.jooq.impl.SQLDataType;

import java.math.BigInteger;
import java.util.UUID;

public class PlayerTable extends BaseTable {
    private final Players PLAYERS;
    public PlayerTable(boolean test) {
        super("players", test);
        this.PLAYERS = Tables.PLAYERS.rename(getName());
    }

    @Override
    public DataTableType getDataTableType() {
        return DataTableType.PLAYERS;
    }

    @Override
    public void createTable() {
        DSLContext create = getContext();

        create.createSequenceIfNotExists(getConstraintPrefix() + "player_id_sequence").execute();

        create.createTableIfNotExists(getName())
            .column("_id", SQLDataType.BIGINT)
            .column("uuid", SQLDataType.UUID)
            .constraints(
                DSL.constraint(getConstraintPrefix() + "_primary_player_id").primaryKey("_id"),
                DSL.constraint(getConstraintPrefix() + "_unique_player_uuid").unique("uuid")
            ).execute();
    }

    public void insert(UUID uuid) {
        DSLContext insert = getContext();

        BigInteger nextVal = insert.nextval(getConstraintPrefix() + "player_id_sequence");

        insert.insertInto(PLAYERS,
            PLAYERS._ID, PLAYERS.UUID)
            .values(nextVal.longValue(), uuid)
            .onConflictDoNothing()
            .execute();
    }

    public long getID(UUID uuid) {
        DSLContext select = getContext();

        return select.select(PLAYERS._ID)
            .from(PLAYERS)
            .where(PLAYERS.UUID.eq(uuid))
            .fetchOneInto(Long.class);
    }

    public SelectConditionStep<Record1<Long>> joinTable(UUID uuid, TableLike<? extends Record> tableLike) {
        DSLContext join = getContext();
        return null; //join.select(PLAYERS).;
    }
    @Override
    public void dropTable() {
        getContext().dropSequenceIfExists(getConstraintPrefix() + "player_id_sequence").execute();
        super.dropTable();
    }


}
