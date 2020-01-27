package com.podcrash.api.db;

import javax.security.auth.Destroyable;

public enum DataTableType {
    KITS(ChampionsKitTable.class),
    PLAYERS(PlayerTable.class),
    PERMISSIONS(PlayerPermissionsTable.class),
    DESCRIPTIONS(DescriptorTable.class),
    SEQUENCES(SequenceTable.class),
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
