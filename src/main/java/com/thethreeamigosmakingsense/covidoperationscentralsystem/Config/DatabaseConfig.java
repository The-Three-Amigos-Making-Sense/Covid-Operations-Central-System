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

        /*jdbcTemplate.batchUpdate(
                "create table if not exists users(" +
                        "username varchar(50) not null primary key, " +
                        "password varchar(60) not null, " +
                        "enabled boolean not null);",

                "create table if not exists authorities (" +
                        "username varchar(50) not null, " +
                        "authority varchar(50) not null, " +
                        "constraint fk_authorities_users foreign key (username) references users(username));");*/

        jdbcTemplate.batchUpdate(
                "CREATE TABLE IF NOT EXISTS Users (" +
                        "username   varchar(11) NOT NULL, " +
                        "email      varchar(320) NOT NULL UNIQUE, " +
                        "firstname  varchar(25) NOT NULL, " +
                        "lastname   varchar(25) NOT NULL, " +
                        "phone_no   varchar(8) NOT NULL UNIQUE, " +
                        "password   varchar(60) NOT NULL, " +
                        "enabled    boolean NOT NULL, " +
                        "PRIMARY KEY (username));",

                "CREATE TABLE IF NOT EXISTS Authorities (" +
                        "username  varchar(11) NOT NULL, " +
                        "authority varchar(14) NOT NULL, " +
                        "PRIMARY KEY (username), " +
                        "CONSTRAINT Authorities FOREIGN KEY (username) REFERENCES Users (username) ON DELETE CASCADE);",

                "CREATE TABLE IF NOT EXISTS Bookings (" +
                        "booking_id int AUTO_INCREMENT NOT NULL, " +
                        "username   varchar(11) NOT NULL, " +
                        "`date`     varchar(10) NOT NULL, " +
                        "time       varchar(5) NOT NULL, " +
                        "type       varchar(7) NOT NULL, " +
                        "PRIMARY KEY (booking_id), " +
                        "CONSTRAINT Bookings FOREIGN KEY (username) REFERENCES Users (username) ON DELETE CASCADE);",

                "CREATE TABLE IF NOT EXISTS TestResult (" +
                        "booking_id int(10) NOT NULL, " +
                        "status     varchar(14) NOT NULL, " +
                        "PRIMARY KEY (booking_id)," +
                        "CONSTRAINT TestResult FOREIGN KEY (booking_id) REFERENCES Bookings (booking_id) ON DELETE CASCADE);",

                "CREATE TABLE IF NOT EXISTS Vaccine (" +
                        "booking_id int(10) NOT NULL, " +
                        "type       varchar(11) NOT NULL, " +
                        "PRIMARY KEY (booking_id), " +
                        "CONSTRAINT Vaccine FOREIGN KEY (booking_id) REFERENCES Bookings (booking_id) ON DELETE CASCADE);"
        );


        int admins = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM authorities WHERE authority = 'ROLE_ADMIN'", Integer.class);


        if (admins == 0) {
            User user = new User("admin", "admin@cocs.com", "Admin", "Admin", "12345678", "0000");
            user.setPassword(passwordEncoder.encode(user.getPassword()));
            Authority authority = new Authority(user, "ROLE_ADMIN");
            jdbcTemplate.update("insert into users (username, email, firstname, lastname, phone_no, password, enabled) values (?, ?, ?, ?, ?, ?, ?);",
                    user.getUsername(), user.getEmail(), user.getFirstname(), user.getLastname(), user.getPhone_no(), user.getPassword(), true);
            jdbcTemplate.update("insert into authorities (username, authority) values (?, ?);",
                    user.getUsername(), authority.getAuthority());
        }

    }
}
