package com.podcrash.test;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.podcrash.api.mc.util.ChatUtil;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class StringTest {
    @Test
    @DisplayName("Purging Game Chat Test")
    public void chat() {
        String test = ChatUtil.chat("&5HELLO!");
        assertEquals("HELLO!", ChatUtil.purge(test));


        String test2 = ChatUtil.chat("&5HELLO! &e2131219031290");
        assertEquals("HELLO! 2131219031290", ChatUtil.purge(test2));

    }
}
