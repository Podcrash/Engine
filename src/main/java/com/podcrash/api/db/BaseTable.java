package com.podcrash.api.db;

import org.jooq.DSLContext;
import org.jooq.SQLDialect;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import static org.jooq.impl.DSL.*;
import static org.jooq.impl.DSL.using;

public abstract class BaseTable implements ITable {
    private String name;
    protected boolean test;
    private static Map<String, BaseTable> map = new HashMap<>();
    private Connection connection;

    public static BaseTable getDatabase(String dbName) {
        return map.getOrDefault(dbName, null);
    }
    public static String keys() {
        return map.keySet().toString();
    }
    public static Collection<BaseTable> values() {
        return map.values();
    }
    public BaseTable(String tableName, boolean test) {
        this.test = test;
        this.name = test ? tableName + "TEST" : tableName;
        System.out.println("Creating the SQLTable: " + name);

        try {
            this.connection = DatabaseConnection.makeConnection();
        }catch (SQLException ex) {
            ex.printStackTrace();
        }
        map.put(name, this);
    }
    public BaseTable(String tableName) {
        this(tableName, false);
    }

    public String getName() {
        return name;
    }
    public String getTableName() {
        return "\"public\".\"" + name + "\"";
    }
    public String getConstraintPrefix() {
        return name.toUpperCase(Locale.ENGLISH) + "_";
    }

    protected Connection getConnection() {
        return this.connection;
    }
    public DSLContext getContext() {
        try {
            if (connection.isClosed())
                connection = DatabaseConnection.makeConnection();
        }catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("basetable:55");
        }
        return using(connection, SQLDialect.POSTGRES_10);
    }

    public void createTable(String name) {
        String temp = this.name;
        this.name = name;
        createTable();
        this.name = temp;
    }

    public void dropTable(String name) {
        getContext().dropTable(name).execute();
    }
    public void dropTable() {
        this.dropTable(name);
    }
}
