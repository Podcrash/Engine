package com.podcrash.api.db.slime;

import com.grinderwolf.swm.api.exceptions.UnknownWorldException;
import com.grinderwolf.swm.api.exceptions.WorldInUseException;
import com.grinderwolf.swm.api.loaders.SlimeLoader;
import com.podcrash.api.db.BaseTable;
import com.podcrash.api.db.tables.DataTableType;
import org.jooq.impl.DSL;
import org.jooq.impl.SQLDataType;

import java.io.IOException;
import java.util.List;

/**
 * Unused for now
 */
public class PostgresLoader extends BaseTable implements SlimeLoader {
    public PostgresLoader() {
        super("worldmaps", false);
        createTable();
    }

    @Override
    public void createTable() {
        useContext((context) -> {
            String sequenceName = getConstraintPrefix() + "world_sequence";
            context.createSequenceIfNotExists(sequenceName).execute();
            context.createTableIfNotExists(getName())
                .column("world_id", SQLDataType.BIGINT.defaultValue(DSL.sequence(DSL.name(sequenceName), SQLDataType.BIGINT).nextval()));

        });
        getContext().createTableIfNotExists(getName())
                .column("world_id", SQLDataType.BIGINT);
    }

    @Override
    public DataTableType getDataTableType() {
        return null;
    }

    @Override
    public byte[] loadWorld(String s, boolean b) throws UnknownWorldException, WorldInUseException, IOException {
        return new byte[0];
    }

    @Override
    public boolean worldExists(String s) throws IOException {
        return false;
    }

    @Override
    public List<String> listWorlds() throws IOException {
        return null;
    }

    @Override
    public void saveWorld(String s, byte[] bytes, boolean b) throws IOException {

    }

    @Override
    public void unlockWorld(String s) throws UnknownWorldException, IOException {

    }

    @Override
    public boolean isWorldLocked(String s) throws UnknownWorldException, IOException {
        return false;
    }

    @Override
    public void deleteWorld(String s) throws UnknownWorldException, IOException {

    }
}
