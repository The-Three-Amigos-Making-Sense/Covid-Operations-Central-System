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
                        "cpr        int(10) NOT NULL AUTO_INCREMENT, " +
                        "email      varchar(320) NOT NULL UNIQUE, " +
                        "first_name varchar(25) NOT NULL, " +
                        "last_name  varchar(25) NOT NULL, " +
                        "phone_no   int(8) NOT NULL UNIQUE, " +
                        "password   varchar(60) NOT NULL, " +
                        "enabled    boolean NOT NULL, " +
                        "PRIMARY KEY (cpr));",

                "CREATE TABLE IF NOT EXISTS Authorities (" +
                        "user_cpr  int(10) NOT NULL, " +
                        "authority varchar(14) NOT NULL, " +
                        "PRIMARY KEY (user_cpr));",

                "CREATE TABLE IF NOT EXISTS Bookings (" +
                        "booking_id int(10) NOT NULL, " +
                        "user_cpr   int(10) NOT NULL, " +
                        "`date`     date NOT NULL, " +
                        "time       time NOT NULL, " +
                        "type       varchar(7) NOT NULL, " +
                        "PRIMARY KEY (booking_id));",

                "CREATE TABLE IF NOT EXISTS TestResult (" +
                        "booking_id int(10) NOT NULL, " +
                        "status     varchar(14) NOT NULL, " +
                        "PRIMARY KEY (booking_id));",

                "CREATE TABLE IF NOT EXISTS Vaccine (" +
                        "booking_id int(10) NOT NULL, " +
                        "type       varchar(11) NOT NULL, " +
                        "PRIMARY KEY (booking_id));",

                "ALTER TABLE Authority ADD CONSTRAINT FKAuthority423494 FOREIGN KEY (user_cpr) REFERENCES Users (cpr);",
                "ALTER TABLE Bookings ADD CONSTRAINT FKBookings883765 FOREIGN KEY (user_cpr) REFERENCES Users (cpr);",
                "ALTER TABLE Vaccine ADD CONSTRAINT FKVaccine210729 FOREIGN KEY (booking_id) REFERENCES Bookings (booking_id);",
                "ALTER TABLE TestResult ADD CONSTRAINT FKTestResult401156 FOREIGN KEY (booking_id) REFERENCES Bookings (booking_id);");





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
