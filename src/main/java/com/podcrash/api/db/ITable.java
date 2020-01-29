package com.podcrash.api.db;

import com.podcrash.api.db.tables.DataTableType;

public interface ITable {
    String getName();

    void createTable(String name);
    void createTable();

    void dropTable(String name);
    void dropTable();

    DataTableType getDataTableType();


}
