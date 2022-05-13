package org.richard.config;

import com.zaxxer.hikari.HikariDataSource;
import javax.sql.DataSource;
import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.conf.Settings;
import org.jooq.impl.DSL;
import org.jooq.impl.DefaultConfiguration;
import org.richard.infra.jooq.RecordMapperProviderImpl;
import org.sqlite.SQLiteConfig;
import org.sqlite.SQLiteConfig.LockingMode;
import org.sqlite.SQLiteConfig.SynchronousMode;
import org.sqlite.SQLiteConfig.TempStore;
import org.sqlite.SQLiteConfig.TransactionMode;
import org.sqlite.SQLiteOpenMode;

public class DatabaseConfig {

    public DataSource dataSource(String url) {
        HikariDataSource ds = new HikariDataSource();
        ds.setJdbcUrl(url);
//        ds.setMaximumPoolSize(1);
        return ds;
    }

    public static SQLiteConfig sqLiteConfig(/*SQLiteConfigProperties properties*/) {
        SQLiteConfig config = new SQLiteConfig();

        final int cacheSize = 1000;
        final int pageSize = 8192;

//        config.setReadOnly( readOnly );
        config.setTempStore(TempStore.MEMORY);  // Hold indices in memory

        config.setCacheSize(cacheSize);
        config.setPageSize(pageSize);
        config.setJounalSizeLimit(cacheSize * pageSize);

        config.setOpenMode(SQLiteOpenMode.NOMUTEX);
        config.setLockingMode(LockingMode.NORMAL);
        config.setTransactionMode(TransactionMode.IMMEDIATE);
        config.setSynchronous(SynchronousMode.NORMAL);


// If read-only, then use the existing journal, if any
//        if ( ! readOnly )
//        {
//            config.setJournalMode( JournalMode.WAL );
//        }
        return config;
    }

    public DSLContext dslContext(String url) {
        return DSL.using(new DefaultConfiguration()
            .set(dataSource(url))
            .set(SQLDialect.SQLITE)
            .set(new Settings().withExecuteLogging(false))
            .set(new RecordMapperProviderImpl(ObjectMapperFactory.buildObjectMapper()))
        );
    }
}
