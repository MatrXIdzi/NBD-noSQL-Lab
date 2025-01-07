package org.restaurant;

import com.datastax.oss.driver.api.core.CqlSession;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;

//@Disabled
public class CassandraTest {
    @Test
    public void connectionTest() {
        CassandraConnector cassandraConnector = new CassandraConnector();
        cassandraConnector.initSession();
        CqlSession session = cassandraConnector.getSession();
        assertFalse(session.isClosed());
        cassandraConnector.close();
    }
}
