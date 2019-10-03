package com.podcrash.test;

import com.podcrash.api.db.TableOrganizer;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;

public class CreateDatabaseTest {

    @Test
    @DisplayName("Create Databases")
    @Order(1)
    public void create() {
        TableOrganizer.createTables(false);
    }
}
