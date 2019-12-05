/*
 * This file is generated by jOOQ.
 */
package com.podcrash.api.db.jooq.tables.records;


import javax.annotation.Generated;

import com.podcrash.api.db.jooq.tables.Permissions;

import org.jooq.Field;
import org.jooq.Record2;
import org.jooq.Row2;
import org.jooq.impl.TableRecordImpl;


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
public class PermissionsRecord extends TableRecordImpl<PermissionsRecord> implements Record2<Long, Integer> {

    private static final long serialVersionUID = 111973403;

    /**
     * Setter for <code>public.permissions.player_id</code>.
     */
    public PermissionsRecord setPlayerId(Long value) {
        set(0, value);
        return this;
    }

    /**
     * Getter for <code>public.permissions.player_id</code>.
     */
    public Long getPlayerId() {
        return (Long) get(0);
    }

    /**
     * Setter for <code>public.permissions.permission_id</code>.
     */
    public PermissionsRecord setPermissionId(Integer value) {
        set(1, value);
        return this;
    }

    /**
     * Getter for <code>public.permissions.permission_id</code>.
     */
    public Integer getPermissionId() {
        return (Integer) get(1);
    }

    // -------------------------------------------------------------------------
    // Record2 type implementation
    // -------------------------------------------------------------------------

    /**
     * {@inheritDoc}
     */
    @Override
    public Row2<Long, Integer> fieldsRow() {
        return (Row2) super.fieldsRow();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Row2<Long, Integer> valuesRow() {
        return (Row2) super.valuesRow();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<Long> field1() {
        return Permissions.PERMISSIONS.PLAYER_ID;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<Integer> field2() {
        return Permissions.PERMISSIONS.PERMISSION_ID;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Long component1() {
        return getPlayerId();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Integer component2() {
        return getPermissionId();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Long value1() {
        return getPlayerId();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Integer value2() {
        return getPermissionId();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public PermissionsRecord value1(Long value) {
        setPlayerId(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public PermissionsRecord value2(Integer value) {
        setPermissionId(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public PermissionsRecord values(Long value1, Integer value2) {
        value1(value1);
        value2(value2);
        return this;
    }

    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------

    /**
     * Create a detached PermissionsRecord
     */
    public PermissionsRecord() {
        super(Permissions.PERMISSIONS);
    }

    /**
     * Create a detached, initialised PermissionsRecord
     */
    public PermissionsRecord(Long playerId, Integer permissionId) {
        super(Permissions.PERMISSIONS);

        set(0, playerId);
        set(1, permissionId);
    }
}
