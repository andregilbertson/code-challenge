package com.mindex.challenge.config;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import de.bwaldvogel.mongo.MongoServer;
import de.bwaldvogel.mongo.backend.memory.MemoryBackend;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.config.AbstractMongoClientConfiguration;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.lang.NonNull;

import java.net.InetSocketAddress;

@EnableMongoRepositories(basePackages = "com.mindex.challenge.dao")
@Configuration
public class MongoConfig extends AbstractMongoClientConfiguration {

    @Override
    @NonNull
    protected String getDatabaseName() {
        return "test";
    }

    @Override
    @NonNull
    public MongoClient mongoClient() {
        MongoServer server = new MongoServer(new MemoryBackend());
        InetSocketAddress serverAddress = server.bind();
        String mongoConnectionString = String.format("mongodb://%s:%d", serverAddress.getHostName(), serverAddress.getPort());
        return MongoClients.create(mongoConnectionString);
    }
}


