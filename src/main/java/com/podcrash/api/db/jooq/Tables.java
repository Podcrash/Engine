/*
 * This file is generated by jOOQ.
 */
package com.podcrash.api.db.jooq;


import javax.annotation.Generated;

import com.podcrash.api.db.jooq.tables.Descriptions;
import com.podcrash.api.db.jooq.tables.Kits;
import com.podcrash.api.db.jooq.tables.Locations;
import com.podcrash.api.db.jooq.tables.Permissions;
import com.podcrash.api.db.jooq.tables.Players;


/**
 * Convenience access to all tables in public
 */
@Generated(
    value = {
        "http://www.jooq.org",
        "jOOQ version:3.11.11"
    },
    comments = "This class is generated by jOOQ"
)
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class Tables {

    /**
     * The table <code>public.descriptions</code>.
     */
    public static final Descriptions DESCRIPTIONS = com.podcrash.api.db.jooq.tables.Descriptions.DESCRIPTIONS;

    /**
     * The table <code>public.kits</code>.
     */
    public static final Kits KITS = com.podcrash.api.db.jooq.tables.Kits.KITS;

    /**
     * The table <code>public.locations</code>.
     */
    public static final Locations LOCATIONS = com.podcrash.api.db.jooq.tables.Locations.LOCATIONS;

    /**
     * The table <code>public.permissions</code>.
     */
    public static final Permissions PERMISSIONS = com.podcrash.api.db.jooq.tables.Permissions.PERMISSIONS;

    /**
     * The table <code>public.players</code>.
     */
    public static final Players PLAYERS = com.podcrash.api.db.jooq.tables.Players.PLAYERS;
}
