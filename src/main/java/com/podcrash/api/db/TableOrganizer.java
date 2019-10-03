package com.podcrash.api.db;

import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.Sequence;
import org.jooq.Table;

import java.sql.SQLException;
import java.util.Collection;

import static org.jooq.impl.DSL.using;

public final class TableOrganizer {
    public static BaseTable getTable(String name) {
        return BaseTable.getDatabase(name);
    }
    public static <F extends BaseTable> F getTable(DataTableType type) {
        return getTable(type, false);
    }
    public static <F extends BaseTable> F getTable(DataTableType type, boolean test) {
        final F table = (F) getTable(test ? type.getName() + "TEST" : type.getName());
        return table;
    }

    public static void createTables(boolean test) {
        BaseTable[] tables = new BaseTable[]{
            new PlayerTable(test),
            new ChampionsKitTable(test),
            new PlayerPermissionsTable(test),
            new LocationTable(test),
        };
        for(BaseTable t : tables) {
            System.out.println("Processing the " + t.getName() + " Table!");
            t.createTable();
        }
    }
    public static void deleteTables(boolean test) {
        if(test) {
            try {
                System.out.println("Dropping test tables");
                Collection<BaseTable> tables = BaseTable.values();
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
}
