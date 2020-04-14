package com.podcrash.test.math;

import com.podcrash.api.mc.util.MathUtil;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
public class FloorFactor {


    @Order(1)
    @Test
    @DisplayName("MathUtil.floor")
    public void floor() {
        assertEquals(9, MathUtil.floor(9, 10));
        assertEquals(9, MathUtil.floor(9, 11));
        assertEquals(9, MathUtil.floor(9, 12));
        assertEquals(9, MathUtil.floor(9, 13));
        assertEquals(9, MathUtil.floor(9, 14));
        assertEquals(9, MathUtil.floor(9, 15));
        assertEquals(9, MathUtil.floor(9, 16));
        assertEquals(9, MathUtil.floor(9, 17));
    }

    @Order(2)
    @Test
    @DisplayName("MathUtil.ceil")
    public void ceil() {
        assertEquals(18, MathUtil.ceil(9, 10));
        assertEquals(18, MathUtil.ceil(9, 11));
        assertEquals(18, MathUtil.ceil(9, 12));
        assertEquals(18, MathUtil.ceil(9, 13));
        assertEquals(18, MathUtil.ceil(9, 14));
        assertEquals(18, MathUtil.ceil(9, 15));
        assertEquals(18, MathUtil.ceil(9, 16));
        assertEquals(18, MathUtil.ceil(9, 17));
    }
}
