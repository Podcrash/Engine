package com.podcrash.api.db;
import java.util.Locale;


public abstract class BaseTable implements ITable {
    private String name;

    public BaseTable(String tableName) {
        this.name = tableName;
        System.out.println("Creating the Table: " + name);
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
}
