package com.podcrash.api.db;

import com.podcrash.api.db.jooq.Tables;
import com.podcrash.api.db.jooq.tables.Locations;
import org.jooq.DSLContext;
import org.jooq.impl.DSL;
import org.jooq.impl.SQLDataType;

import java.math.BigInteger;

public class LocationTable extends BaseTable {
    private final Locations LOCATIONS;
    public LocationTable(boolean test) {
        super("locations", test);
        this.LOCATIONS = Tables.LOCATIONS.rename(getName());
    }

    @Override
    public DataTableType getDataTableType() {
        return DataTableType.LOCATIONS;
    }

    @Override
    public void createTable() {
        DSLContext create = getContext();

        create.createSequenceIfNotExists(getConstraintPrefix() + "primary_id_sequence").execute();

        create.createTableIfNotExists(getName())
            .column("_id", SQLDataType.BIGINT)
            .column("name", SQLDataType.CHAR(12).nullable(true))
            .column("otype", SQLDataType.INTEGER)
            .column("map_id", SQLDataType.INTEGER)
            .column("x", SQLDataType.FLOAT)
            .column("y", SQLDataType.FLOAT)
            .column("z", SQLDataType.FLOAT)
        .constraints(
            DSL.constraint(getConstraintPrefix() + "primary_id").primaryKey("_id"),
            DSL.constraint(getConstraintPrefix() + "unique_name").unique("name")
        ).execute();

    }

    public void set(String name, int otype, int mapId, double[] xyz) {
        DSLContext set = getContext();
        BigInteger next = set.nextval(getConstraintPrefix() + "primary_id_sequence");

        set.insertInto(
                LOCATIONS,
                LOCATIONS._ID, LOCATIONS.NAME, LOCATIONS.OTYPE, LOCATIONS.MAP_ID,
                LOCATIONS.X, LOCATIONS.Y, LOCATIONS.Z)
            .values(next.longValue(), name, otype, mapId,
                    xyz[0], xyz[1], xyz[2])
            .execute();
    }

    @Override
    public void dropTable() {
        getContext().dropSequenceIfExists(getConstraintPrefix() + "primary_id_sequence").execute();
        super.dropTable();
    }


}
