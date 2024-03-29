package com.revolut.database;

import com.revolut.model.RevolutDbException;
import com.zaxxer.hikari.HikariDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.SQLException;

public class InMemDataSource {
    private static final Logger log = LoggerFactory.getLogger(InMemDataSource.class);

    private static final HikariDataSource ds;

    static {
        ds = new HikariDataSource();
        //initializing the in-memry H2 database and initialize it by the schema and some initial data
        ds.setJdbcUrl("jdbc:h2:mem:test;" +
                "INIT=RUNSCRIPT FROM 'classpath:db_schema.sql'\\;RUNSCRIPT FROM 'classpath:sql_init_data.sql';" +
                "TRACE_LEVEL_FILE=4");
        //TODO login and password should be provided trough system variables
        ds.setUsername("sa");
        ds.setPassword("sa");
        //We are using frequently manual transaction management in the app. So we don't want to have transaction
        //commit for each request
        ds.setAutoCommit(false);

        log.info("The database has been initialized");
    }

    private InMemDataSource() {}

    public static Connection getConnection() throws RevolutDbException {
        try {
            return ds.getConnection();
        } catch (SQLException e) {
            throw new RevolutDbException(e);
        }

    }
}
