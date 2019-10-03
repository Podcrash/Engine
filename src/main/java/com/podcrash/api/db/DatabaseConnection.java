package com.podcrash.api.db;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.sql.Connection;
import java.sql.SQLException;

public class DatabaseConnection {
    private static final String HOST = System.getenv("PSQL_HOST");
    private static final String USER = System.getenv("PSQL_USER");
    private static final String PASSWORD = System.getenv("PSQL_PASS");
    private static final String DATABASE = System.getenv("PSQL_DBNAME");
    private static final int PORT = Integer.parseInt(System.getenv("PSQL_PORT"));

    private static final HikariDataSource source;
    static {
        try {
            Class.forName("org.postgresql.Driver");
            Class.forName("org.postgresql.ds.PGSimpleDataSource");
        }catch (ClassNotFoundException e) {
            System.out.println("PG DRIVER = NOT FOUND!");
            e.printStackTrace();
        }
        HikariConfig config = new HikariConfig();
        String url = String.format("jdbc:postgresql://%s:%d/%s?user=%s&password=%s",
                HOST, PORT, DATABASE, USER, PASSWORD);
        config.setJdbcUrl(url);
        System.out.println(url);
        config.setUsername(USER);
        config.setPassword(PASSWORD);
        config.addDataSourceProperty("cachePrepStmts", "true");
        config.addDataSourceProperty("prepStmtCacheSize", "250");
        config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
        config.setDriverClassName("org.postgresql.Driver");

        source = new HikariDataSource(config);
    }
    public static Connection makeConnection() throws SQLException {
        return source.getConnection();
    }

    private DatabaseConnection() {

    }
}
