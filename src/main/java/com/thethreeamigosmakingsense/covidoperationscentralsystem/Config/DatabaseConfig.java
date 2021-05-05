package com.thethreeamigosmakingsense.covidoperationscentralsystem.Config;

import com.thethreeamigosmakingsense.covidoperationscentralsystem.Model.Authority;
import com.thethreeamigosmakingsense.covidoperationscentralsystem.Model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@DependsOn("securityConfig")
public class DatabaseConfig {

    @Autowired
    JdbcTemplate jdbcTemplate;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Bean
    public void initializeDatabase() {

        jdbcTemplate.batchUpdate(
                "create table if not exists users(" +
                        "username varchar(50) not null primary key, " +
                        "password varchar(60) not null, " +
                        "enabled boolean not null);",

                "create table if not exists authorities (" +
                        "username varchar(50) not null, " +
                        "authority varchar(50) not null, " +
                        "constraint fk_authorities_users foreign key (username) references users(username));");

        int admins = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM authorities WHERE authority = 'ROLE_ADMIN'", Integer.class);

        if (admins == 0) {
            User user = new User("Admin", "0000");
            user.setPassword(passwordEncoder.encode(user.getPassword()));
            Authority authority = new Authority(user, "ROLE_ADMIN");
            jdbcTemplate.update("insert into users (username, password, enabled) values (?, ?, ?);",
                    user.getUsername(), user.getPassword(), true);
            jdbcTemplate.update("insert into authorities (username, authority) values (?, ?);",
                    user.getUsername(), authority.getAuthority());
        }

    }
}
