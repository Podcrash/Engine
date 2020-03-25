package com.podcrash.api.db;

import com.podcrash.api.plugin.Pluginizer;

public final class DBUtils {
    public static void handleThrowables(Throwable throwable) {
        if(throwable == null) return;
        Pluginizer.getLogger().info(throwable.getLocalizedMessage());
        throwable.printStackTrace();
    }
}
