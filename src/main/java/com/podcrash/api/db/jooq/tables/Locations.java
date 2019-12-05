/*
 * This file is generated by jOOQ.
 */
package com.podcrash.api.db.jooq.tables;


import java.util.Arrays;
import java.util.List;

import javax.annotation.Generated;

import com.podcrash.api.db.jooq.Indexes;
import com.podcrash.api.db.jooq.Keys;
import com.podcrash.api.db.jooq.Public;
import com.podcrash.api.db.jooq.tables.records.LocationsRecord;

import org.jooq.Field;
import org.jooq.ForeignKey;
import org.jooq.Index;
import org.jooq.Name;
import org.jooq.Record;
import org.jooq.Schema;
import org.jooq.Table;
import org.jooq.TableField;
import org.jooq.UniqueKey;
import org.jooq.impl.DSL;
import org.jooq.impl.TableImpl;


/**
 * This class is generated by jOOQ.
 */
@Generated(
    value = {
        "http://www.jooq.org",
        "jOOQ version:3.11.11"
    },
    comments = "This class is generated by jOOQ"
)
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class Locations extends TableImpl<LocationsRecord> {

    private static final long serialVersionUID = 1857675575;

    /**
     * The reference instance of <code>public.locations</code>
     */
    public static final Locations LOCATIONS = new Locations();

    /**
     * The class holding records for this type
     */
    @Override
    public Class<LocationsRecord> getRecordType() {
        return LocationsRecord.class;
    }

    /**
     * The column <code>public.locations._id</code>.
     */
    public final TableField<LocationsRecord, Long> _ID = createField("_id", org.jooq.impl.SQLDataType.BIGINT.nullable(false), this, "");

    /**
     * The column <code>public.locations.name</code>.
     */
    public final TableField<LocationsRecord, String> NAME = createField("name", org.jooq.impl.SQLDataType.CHAR(12), this, "");

    /**
     * The column <code>public.locations.otype</code>.
     */
    public final TableField<LocationsRecord, Integer> OTYPE = createField("otype", org.jooq.impl.SQLDataType.INTEGER, this, "");

    /**
     * The column <code>public.locations.map_id</code>.
     */
    public final TableField<LocationsRecord, Integer> MAP_ID = createField("map_id", org.jooq.impl.SQLDataType.INTEGER, this, "");

    /**
     * The column <code>public.locations.x</code>.
     */
    public final TableField<LocationsRecord, Double> X = createField("x", org.jooq.impl.SQLDataType.DOUBLE, this, "");

    /**
     * The column <code>public.locations.y</code>.
     */
    public final TableField<LocationsRecord, Double> Y = createField("y", org.jooq.impl.SQLDataType.DOUBLE, this, "");

    /**
     * The column <code>public.locations.z</code>.
     */
    public final TableField<LocationsRecord, Double> Z = createField("z", org.jooq.impl.SQLDataType.DOUBLE, this, "");

    /**
     * Create a <code>public.locations</code> table reference
     */
    public Locations() {
        this(DSL.name("locations"), null);
    }

    /**
     * Create an aliased <code>public.locations</code> table reference
     */
    public Locations(String alias) {
        this(DSL.name(alias), LOCATIONS);
    }

    /**
     * Create an aliased <code>public.locations</code> table reference
     */
    public Locations(Name alias) {
        this(alias, LOCATIONS);
    }

    private Locations(Name alias, Table<LocationsRecord> aliased) {
        this(alias, aliased, null);
    }

    private Locations(Name alias, Table<LocationsRecord> aliased, Field<?>[] parameters) {
        super(alias, null, aliased, parameters, DSL.comment(""));
    }

    public <O extends Record> Locations(Table<O> child, ForeignKey<O, LocationsRecord> key) {
        super(child, key, LOCATIONS);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Schema getSchema() {
        return Public.PUBLIC;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Index> getIndexes() {
        return Arrays.<Index>asList(Indexes.LOCATIONS_PRIMARY_ID, Indexes.LOCATIONS_UNIQUE_NAME);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public UniqueKey<LocationsRecord> getPrimaryKey() {
        return Keys.LOCATIONS_PRIMARY_ID;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<UniqueKey<LocationsRecord>> getKeys() {
        return Arrays.<UniqueKey<LocationsRecord>>asList(Keys.LOCATIONS_PRIMARY_ID, Keys.LOCATIONS_UNIQUE_NAME);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Locations as(String alias) {
        return new Locations(DSL.name(alias), this);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Locations as(Name alias) {
        return new Locations(alias, this);
    }

    /**
     * Rename this table
     */
    @Override
    public Locations rename(String name) {
        return new Locations(DSL.name(name), null);
    }

    /**
     * Rename this table
     */
    @Override
    public Locations rename(Name name) {
        return new Locations(name, null);
    }
}
