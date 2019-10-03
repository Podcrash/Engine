package com.podcrash.test;

import com.podcrash.api.db.*;
import com.podcrash.api.permissions.Perm;
import org.jooq.Table;
import org.jooq.impl.DSL;
import org.junit.jupiter.api.*;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;


public class DatabaseLoginTest {
    private static UUID random1, random2, random3;

    @BeforeAll
    public static void createTestDatabases() {
        TableOrganizer.deleteTables(true);
        TableOrganizer.createTables(true);
        random1 = UUID.randomUUID();
        random2 = UUID.randomUUID();
        random3 = UUID.randomUUID();
    }

    @Test
    @Order(1)
    @DisplayName("Players Database Login")
    public void players() {
        PlayerTable table = TableOrganizer.getTable(DataTableType.PLAYERS, true);

        List<Table<?>> tables = table.getContext().meta().getTables();
        assertTrue(
                tables.stream().anyMatch(
                        t -> t.getName().equals(table.getName())),
                "Table doesn't exist");
        assertNotEquals(null, DSL.table(table.getName()), "Table doesn't exist");
        table.insert(random1);
        long id1 = table.getID(random1);
        assertEquals(1L, id1, "Reading after an insert");

        table.insert(random2);
        long id2 = table.getID(random2);
        assertEquals(2L, id2, "Reading after 2 inserts");

        table.insert(random3);

    }
    @Test
    @Order(2)
    @DisplayName("Champions Database Login")
    public void champions() {
        ChampionsKitTable table = TableOrganizer.getTable(DataTableType.KITS, true);

        List<Table<?>> tables = table.getContext().meta().getTables();
        assertTrue(
                tables.stream().anyMatch(
                        t -> t.getName().equals(table.getName())),
                "Table doesn't exist");

        table.set(random1, "assassin",1, "BIG DATA");
        assertEquals(1, table.size(), "Insertion Test failed, Data was not inserted into the data!");

        String data = table.getJSONData(random1, "assassin", 1);
        assertEquals("BIG DATA", data, "Reading Test failed, expected BIG DATA");

        assertNotEquals("BIG DATA", table.getJSONData(random2, "assassin1", 1), "A different UUID should have not queried");

        table.delete(random1, "assassin", 1);
        assertEquals(0, table.size(), "Deletion Test failed.");


    }

    @Test
    @Order(3)
    @DisplayName("Locations Database Login")
    public void locations() {
        /*
        LocationTable table = (ChampionsKitTable) TableOrganizer.getTable("locationsTEST");
        table.createTable();

        String randomUUID = UUID.randomUUID().toString();

        table.set(randomUUID, "assassin",1, "BIG DATA");
        assertEquals(1, table.size(), "Insertion Test failed, Data was not inserted into the data!");

        String data = table.get(randomUUID, "assassin", 1);
        assertEquals("BIG DATA", data, "Reading Test failed, expected BIG DATA");

        table.delete(randomUUID, "assassin", 1);
        assertEquals(0, table.size(), "Deletion Test failed.");

        table.dropTable();
        */
    }

    @Test
    @Order(4)
    @DisplayName("Permissions Database Login")
    public void permissions() {
        PlayerPermissionsTable table = TableOrganizer.getTable(DataTableType.PERMISSIONS, true);
        table.addRole(random3, Perm.ANTICHEAT);
        table.addRole(random3, Perm.BUILD);

        List<Perm> perms = table.getRoles(random3);
        boolean contains2Roles = perms.contains(Perm.ANTICHEAT) && perms.contains(Perm.BUILD);
        assertTrue(contains2Roles, "The uuid doesn't have both roles that were added");

        table.removeRole(random3, Perm.BUILD);
        List<Perm> perms2 = table.getRoles(random3);
        boolean lost1Role = perms2.contains(Perm.ANTICHEAT) && !perms2.contains(Perm.BUILD);
        assertTrue(lost1Role, "The uuid didn't have a role that were deleted");

        assertTrue(table.hasRole(random3, Perm.ANTICHEAT), "The uuid somehow lost its anticheat role");
    }

    @AfterAll
    @Order(9999)
    public static void deleteAllTables() {
        TableOrganizer.deleteTables(true);
    }
}
