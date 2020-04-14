package com.podcrash.test;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class SimpleTest {

    @Test
    @DisplayName("Rounding Test")
    public void round() {
        Assertions.assertEquals(100.12, 100.12);
    }
}
