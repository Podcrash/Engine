package com.podcrash.api.db;

import com.podcrash.api.db.jooq.Tables;
import com.podcrash.api.db.jooq.tables.Descriptions;
import com.podcrash.api.redis.Communicator;
import com.podcrash.api.db.jooq.Sequences;
import org.jooq.*;
import org.jooq.impl.DSL;
import org.jooq.impl.SQLDataType;
import org.jooq.util.postgres.PostgresDataType;

import java.util.concurrent.CompletableFuture;

public class DescriptorTable extends BaseTable {
    private final Descriptions DESCRIPTIONS;
    public DescriptorTable(boolean test) {
        super("descriptions", test);

        this.DESCRIPTIONS = Tables.DESCRIPTIONS.rename(getName());
    }

    @Override
    public DataTableType getDataTableType() {
        return DataTableType.DESCRIPTIONS;
    }

    @Override
    public void createTable() {
        DSLContext create = getContext();

        create.createSequenceIfNotExists(getConstraintPrefix() + "id_sequence").execute();

        create.createTableIfNotExists(getName())
            .column("id", PostgresDataType.INT
                .defaultValue(Sequences.DESCRIPTIONS_ID_SEQUENCE.nextval().cast(PostgresDataType.INT)))
            .column("key", SQLDataType.VARCHAR(32))
            .column("value", SQLDataType.VARCHAR(1024))
            .constraints(
                DSL.constraint(getConstraintPrefix() + "_unique_key").unique("key"),
                DSL.constraint(getConstraintPrefix() + "_primary_id").primaryKey("id")
            ).execute();

        create.createIndexIfNotExists(getConstraintPrefix() + "id")
                .on(getName(), "id")
                .execute();
    }

    public void insert(String key, Object value) {
        //am I dumb or are they both the same thing
        if(value instanceof String) insert(key, (String) value);
        else insert(key, value.toString());
    }
    private void insert(String key, String value) {
        DSLContext insert = getContext();
        insert.insertInto(DESCRIPTIONS,
                DESCRIPTIONS.KEY, DESCRIPTIONS.VALUE)
                .values(key.toLowerCase(), value);


    }

    public CompletableFuture<Void> requestCache(final String key) {
        if(!Communicator.isReady()) {
            System.out.println("Communicator must be ready!");
            return CompletableFuture.completedFuture(null);
        }
        return CompletableFuture.runAsync(() -> {
            String value = getValue(key);
            if(value == null || value.isEmpty()) {
                System.out.println("Caching nothing, from key: " + key);
                return;
            }
            System.out.println("Caching " + key + ": " + value);
            Communicator.cache(key, value);
        }, EXECUTOR);
    }

    public String getValue(String key) {
        DSLContext select = getContext();
        String value = select.select(DESCRIPTIONS.VALUE)
                .from(DESCRIPTIONS)
                .where(DESCRIPTIONS.KEY.eq(key))
                .fetchOneInto(String.class);
        return value;
    }
}
