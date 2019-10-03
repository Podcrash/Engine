package com.podcrash.api.db;

public enum DataTableType {
    KITS(ChampionsKitTable.class),
    LOCATIONS(LocationTable.class),
    PLAYERS(PlayerTable.class),
    PERMISSIONS(PlayerPermissionsTable.class);

    private Class<? extends BaseTable> table;
    DataTableType(Class<? extends BaseTable> table) {
        this.table = table;
    }

    public String getName(){
        return name().toLowerCase();
    }

    public Class<? extends BaseTable> getTable() {
        return table;
    }
}
