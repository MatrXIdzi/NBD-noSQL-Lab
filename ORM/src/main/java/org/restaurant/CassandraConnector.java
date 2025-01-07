package org.restaurant;

import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.oss.driver.api.core.config.DriverConfigLoader;

import java.io.File;
import java.net.InetSocketAddress;

public class CassandraConnector {
    private CqlSession session;

    public void initSession() {
        //DriverConfigLoader loader = DriverConfigLoader.fromFile(new File("application.conf"));

        session = CqlSession.builder()
                //.withConfigLoader(loader)
                .addContactPoint(new InetSocketAddress("cassandra1", 9042))
                .addContactPoint(new InetSocketAddress("cassandra2", 9043))
                .withLocalDatacenter("dc1")
                .withAuthCredentials("cassandra", "cassandrapassword")
                .build();
    }

    public CqlSession getSession() {
        return this.session;
    }

    public void close() {
        session.close();
    }
}
