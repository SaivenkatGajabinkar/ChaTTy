package com.chaty.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Bean;
import javax.sql.DataSource;
import org.springframework.boot.jdbc.DataSourceBuilder;
import java.net.URI;
import java.net.URISyntaxException;

@Configuration
public class DatabaseConfig {

    @Bean
    public DataSource dataSource() throws URISyntaxException {
        String databaseUrl = System.getenv("DATABASE_URL");
        if (databaseUrl == null) {
            databaseUrl = System.getenv("MYSQL_URL");
        }
        
        if (databaseUrl != null && (databaseUrl.startsWith("postgres://") || databaseUrl.startsWith("postgresql://"))) {
            // Convert postgres://user:password@host:port/database to jdbc:postgresql://host:port/database
            URI dbUri = new URI(databaseUrl);
            String username = dbUri.getUserInfo().split(":")[0];
            String password = dbUri.getUserInfo().split(":")[1];
            
            // Get port or default to 5432
            int port = dbUri.getPort();
            String portStr = port == -1 ? "5432" : String.valueOf(port);
            
            String dbUrl = "jdbc:postgresql://" + dbUri.getHost() + ":" + portStr + dbUri.getPath();
            
            return DataSourceBuilder.create()
                    .url(dbUrl)
                    .username(username)
                    .password(password)
                    .driverClassName("org.postgresql.Driver")
                    .build();
        }
        
        // Fallback to default spring datasource configuration (MySQL / Local)
        String url = System.getenv("MYSQL_URL");
        if (url == null) {
            url = "jdbc:mysql://localhost:3306/chaty_db?createDatabaseIfNotExist=true&useSSL=false&allowPublicKeyRetrieval=true";
        }
        String username = System.getenv("MYSQL_USER");
        if (username == null) {
            username = "root";
        }
        String password = System.getenv("MYSQL_PASSWORD");
        if (password == null) {
            password = "SaiVenkat@123";
        }
        
        return DataSourceBuilder.create()
                .url(url)
                .username(username)
                .password(password)
                .driverClassName("com.mysql.cj.jdbc.Driver")
                .build();
    }
}
