package com.podcrash.api.db;

import com.mongodb.DuplicateKeyException;
import com.mongodb.ErrorCategory;
import com.mongodb.MongoWriteException;
import com.mongodb.WriteError;
import com.podcrash.api.plugin.Pluginizer;

public final class DBUtils {
    /**
     * This is to check and null duplicate key errors from the console.
     * If it's not a duplicate key, just handle it regularly
     * @param throwable
     */
    public static void handleDuplicateKeyException(Throwable throwable) {
        if(throwable == null) return;
        if(!(throwable instanceof MongoWriteException)) return;

        MongoWriteException exception = (MongoWriteException) throwable;
        WriteError writeError = exception.getError();
        boolean isDuplicateKey = writeError.getCategory() == ErrorCategory.DUPLICATE_KEY;
        if(isDuplicateKey) return;
        handleThrowables(throwable);
    }
    public static void handleThrowables(Throwable throwable) {
        if(throwable == null) return;
        Pluginizer.getLogger().info(throwable.getLocalizedMessage());
        throwable.printStackTrace();
    }
}
