package com.podcrash.api.db;

import com.podcrash.api.db.connection.DatabaseConnection;
import com.podcrash.api.db.connection.IConnection;
import com.podcrash.api.db.connection.MongoConnection;
import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.Sequence;
import org.jooq.Table;

import java.sql.SQLException;
import java.util.*;

import static org.jooq.impl.DSL.using;

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
    public static void createTables(boolean test) {
        ITable[] tables = new ITable[]{
            new LocationTable(test),
            new PlayerTable(test),
            new ChampionsKitTable(test),
            new PlayerPermissionsTable(test),
            new DescriptorTable(test),
            new MapTable(test),
        };
        process(tables);
    }

    /**
     * For testing only
     * @param test
     */
    public static void createMongoTables(boolean test) {
        MongoBaseTable[] tables = new MongoBaseTable[] {
            new MapTable(test)
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
    public static void deleteTables(boolean test) {
        if(!test) return;
        try {
            System.out.println("Dropping test tables");
            Collection<? extends ITable> tables = typeTables.values();
            if(tables.size() > 0) {
                System.out.println("Code-generated tables found!");
                tables.forEach(table -> {
                    if(table.getName().toLowerCase().contains("test")) {
                        System.out.println("Dropping " + table.getName());
                        table.dropTable();
                    }
                });
                return;
            }
            DSLContext context = using(DatabaseConnection.makeConnection(), SQLDialect.POSTGRES_10);
            for(Table table : context.meta().getTables()) {
                String name = table.getName();
                if(name.toLowerCase().contains("test")) {
                    System.out.println("Dropping the " + name + " table!");
                    context.dropTable(name).execute();
                }
            }

            for(Sequence sequence : context.meta().getSequences()) {
                String name = sequence.getName();
                if(name.toLowerCase().contains("test")) {
                    System.out.println("Dropping the " + name + " sequence!");
                    context.dropTable(name).execute();
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
