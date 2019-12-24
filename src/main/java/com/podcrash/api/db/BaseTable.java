package com.podcrash.api.db;

import com.podcrash.api.db.connection.DatabaseConnection;
import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.impl.DSL;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.jooq.impl.DSL.using;

public abstract class BaseTable implements ITable {
    protected ExecutorService EXECUTOR = Executors.newFixedThreadPool(5);
    private String name;
    protected boolean test;
    private Connection connection;

    public BaseTable(String tableName, boolean test) {
        this.test = test;
        this.name = test ? tableName + "TEST" : tableName;
        System.out.println("Creating the SQLTable: " + name);

        try {
            this.connection = DatabaseConnection.makeConnection();
        }catch (SQLException ex) {
            ex.printStackTrace();
        }
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
    @Deprecated
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

    /**
     * Use this instead
     * @param dslConnection
     */
    public void useContext(DSLConnection dslConnection) {
        try(Connection conn = DatabaseConnection.makeConnection()) {
            dslConnection.useConnection(using(conn, SQLDialect.POSTGRES_10));
        } catch (SQLException e) {
            e.printStackTrace();
        }
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

    public interface DSLConnection {
        void useConnection(DSLContext context);
    }
}
