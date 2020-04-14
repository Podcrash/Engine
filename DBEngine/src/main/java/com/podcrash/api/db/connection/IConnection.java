package com.podcrash.api.db.connection;

public interface IConnection<T> {
    /**
     * Set up all of the database stuff.
     * This usually includes logging in with credentials.
     */
    void setUp();

    /**
     * The equivalent of getting a connection.
     * @return the connection pool
     */
    T getConnection();

    /**
     * Closes the database.
     * This is handled automatically, but it is used to close any db resources.
     */
    void close();
}
