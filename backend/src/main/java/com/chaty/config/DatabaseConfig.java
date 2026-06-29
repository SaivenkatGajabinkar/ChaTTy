package com.chaty.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Bean;
import javax.sql.DataSource;
import org.springframework.boot.jdbc.DataSourceBuilder;
import java.net.URI;
import java.net.URISyntaxException;

@Configuration
public class DatabaseConfig {

    @Value("${spring.datasource.url}")
    private String defaultUrl;

    @Value("${spring.datasource.username}")
    private String defaultUsername;

    @Value("${spring.datasource.password}")
    private String defaultPassword;

    @Bean
    public DataSource dataSource() throws URISyntaxException {
        String databaseUrl = System.getenv("DATABASE_URL");
        if (databaseUrl == null) {
            databaseUrl = System.getenv("MYSQL_URL");
        }
        
        if (databaseUrl != null && (databaseUrl.startsWith("postgres://") || databaseUrl.startsWith("postgresql://") || databaseUrl.startsWith("mysql://"))) {
            URI dbUri = new URI(databaseUrl);
            String username = "";
            String password = "";
            if (dbUri.getUserInfo() != null) {
                String[] userInfo = dbUri.getUserInfo().split(":");
                username = userInfo[0];
                if (userInfo.length > 1) {
                    password = userInfo[1];
                }
            }
            
            if (databaseUrl.startsWith("mysql://")) {
                int port = dbUri.getPort();
                String portStr = port == -1 ? "3306" : String.valueOf(port);
                String query = dbUri.getQuery();
                String extraParams = "?createDatabaseIfNotExist=true&useSSL=false&allowPublicKeyRetrieval=true";
                if (query != null && !query.isEmpty()) {
                    extraParams = "?" + query;
                }
                String dbUrl = "jdbc:mysql://" + dbUri.getHost() + ":" + portStr + dbUri.getPath() + extraParams;
                return DataSourceBuilder.create()
                        .url(dbUrl)
                        .username(username)
                        .password(password)
                        .driverClassName("com.mysql.cj.jdbc.Driver")
                        .build();
            } else {
                int port = dbUri.getPort();
                String portStr = port == -1 ? "5432" : String.valueOf(port);
                // Append SSL and SNI options for Neon PostgreSQL routing
                String dbUrl = "jdbc:postgresql://" + dbUri.getHost() + ":" + portStr + dbUri.getPath() + "?sslmode=require&options=endpoint%3Dep-round-wind-adzvb2hz";
                return DataSourceBuilder.create()
                        .url(dbUrl)
                        .username(username)
                        .password(password)
                        .driverClassName("org.postgresql.Driver")
                        .build();
            }
        }
        
        // Fallback to the settings configured in application.properties!
        return DataSourceBuilder.create()
                .url(defaultUrl)
                .username(defaultUsername)
                .password(defaultPassword)
                .driverClassName("org.postgresql.Driver")
                .build();
    }
}
