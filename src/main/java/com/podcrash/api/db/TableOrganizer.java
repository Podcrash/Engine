package com.podcrash.api.db;

import com.podcrash.api.db.connection.IConnection;
import com.podcrash.api.db.connection.MongoConnection;
import com.podcrash.api.db.tables.*;

import java.util.*;

public final class TableOrganizer {
    private static Map<DataTableType, ITable> typeTables = new HashMap<>();
    private static List<IConnection<?>> connections = new ArrayList<>();

    public static <F extends ITable> F getTable(DataTableType type) {
        return getTable(type, false);
    }
    public static <F extends ITable> F getTable(DataTableType type, boolean test) {
        final F table = (F) typeTables.get(type);
        return table;
    }

    public static void initConnections() {
        connections.add(new MongoConnection());
        for(IConnection<?> connection : connections)
            connection.setUp();
    }
    public static void deleteConnections() {
        for(IConnection<?> connection : connections)
            connection.close();
        connections.clear();
    }
    public static <T extends IConnection<?>> T getConnection(Class<T> clasz) {
        System.out.println("conn size: " + connections.size());
        for(IConnection<?> connection : connections)
            if(connection.getClass() == clasz) return (T) connection;
        return null;
    }

    /**
     *
     *
     * */
    public static void createTables() {
        ITable[] tables = new ITable[]{
                new PlayerTable(),
                new ChampionsKitTable(),
                new RanksTable(),
                new MapTable(),
        };
        process(tables);
    }
    private static void process(ITable[] tables) {
        for(ITable t : tables) {
            System.out.println("Processing the " + t.getName() + " Table!");
            t.createTable();
            typeTables.put(t.getDataTableType(), t);
        }
    }
}
