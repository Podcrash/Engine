package com.podcrash.api.db;

public interface ITable {
    String getName();

    void createTable(String name);
    void createTable();

    void dropTable(String name);
    void dropTable();

    DataTableType getDataTableType();


}
