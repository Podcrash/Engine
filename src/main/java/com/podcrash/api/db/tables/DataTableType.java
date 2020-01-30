package com.podcrash.api.db.tables;

import com.podcrash.api.db.ITable;
import com.podcrash.api.db.MapTable;

public enum DataTableType {
    KITS(ChampionsKitTable.class),
    PLAYERS(PlayerTable.class),
    PERMISSIONS(RanksTable.class),
    DESCRIPTIONS(DescriptorTable.class),
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
