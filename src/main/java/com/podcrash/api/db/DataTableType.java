package com.podcrash.api.db;

public enum DataTableType {
    KITS(ChampionsKitTable.class),
    LOCATIONS(LocationTable.class),
    PLAYERS(PlayerTable.class),
    PERMISSIONS(PlayerPermissionsTable.class),
    MAPS(MapTable.class);

    private Class<? extends ITable> table;
    DataTableType(Class<? extends ITable> table) {
        this.table = table;
    }

    public String getName(){
        return name().toLowerCase();
    }

    public Class<? extends ITable> getTable() {
        return table;
    }
}
