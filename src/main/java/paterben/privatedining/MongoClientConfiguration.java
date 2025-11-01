package paterben.privatedining;

import java.util.Collections;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.MongoDatabaseFactory;
import org.springframework.data.mongodb.MongoTransactionManager;
import org.springframework.data.mongodb.config.AbstractMongoClientConfiguration;

import com.mongodb.MongoClientSettings.Builder;
import com.mongodb.ServerAddress;

/**
 * Configuration class for MongoDB.
 */
@Configuration
public class MongoClientConfiguration extends AbstractMongoClientConfiguration {

    @Value("${spring.data.mongodb.host}")
    private String host;

    @Value("${spring.data.mongodb.port}")
    private int port;

    @Value("${spring.data.mongodb.database}")
    private String databaseName;

    @Value("${spring.data.mongodb.auto-index-creation}")
    private boolean autoIndexCreation;

    // This is necessary in order to enable MongoDB transaction support.
    @Bean
    MongoTransactionManager transactionManager(MongoDatabaseFactory mongoDatabaseFactory) {
        return new MongoTransactionManager(mongoDatabaseFactory);
    }

    @Override
    protected String getDatabaseName() {
        return databaseName;
    }

    @Override
    public boolean autoIndexCreation() {
        return autoIndexCreation;
    }

    @Override
    protected void configureClientSettings(Builder builder) {

        builder
                .applyToClusterSettings(settings -> {
                    settings.hosts(Collections.singletonList(new ServerAddress(host, port)));
                });
    }

}
