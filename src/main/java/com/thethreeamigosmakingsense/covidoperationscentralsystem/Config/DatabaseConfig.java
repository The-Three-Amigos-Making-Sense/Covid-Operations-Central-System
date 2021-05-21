package com.thethreeamigosmakingsense.covidoperationscentralsystem.Config;

import com.thethreeamigosmakingsense.covidoperationscentralsystem.Model.Authority;
import com.thethreeamigosmakingsense.covidoperationscentralsystem.Model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.annotation.PostConstruct;

@Configuration
public class DatabaseConfig {

    @Autowired
    JdbcTemplate jdbcTemplate;

    @Autowired
    PasswordEncoder passwordEncoder;

    @PostConstruct
    public void initializeDatabase() {

        String query;
        String[] queries = {
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
                        "CONSTRAINT Authorities FOREIGN KEY (username) " +
                        "   REFERENCES Users (username) ON DELETE CASCADE);",

                "CREATE TABLE IF NOT EXISTS Bookings (" +
                        "booking_id int AUTO_INCREMENT, " +
                        "username   varchar(11) NOT NULL, " +
                        "`date`     timestamp NOT NULL, " +
                        "time       varchar(5) NOT NULL, " +
                        "type       varchar(7) NOT NULL, " +
                        "PRIMARY KEY (booking_id), " +
                        "CONSTRAINT Bookings FOREIGN KEY (username) " +
                        "   REFERENCES Users (username) ON DELETE CASCADE);",

                "CREATE TABLE IF NOT EXISTS TestResult (" +
                        "booking_id int(10) NOT NULL, " +
                        "status     varchar(14) NOT NULL, " +
                        "PRIMARY KEY (booking_id)," +
                        "CONSTRAINT TestResult FOREIGN KEY (booking_id) " +
                        "   REFERENCES Bookings (booking_id) ON DELETE CASCADE);",

                "CREATE TABLE IF NOT EXISTS Vaccine (" +
                        "booking_id int(10) NOT NULL, " +
                        "type       varchar(11) NOT NULL, " +
                        "status varchar(8) NOT NULL, " +
                        "PRIMARY KEY (booking_id), " +
                        "CONSTRAINT Vaccine FOREIGN KEY (booking_id) " +
                        "   REFERENCES Bookings (booking_id) ON DELETE CASCADE);",

                "ALTER TABLE Bookings AUTO_INCREMENT = 1",
        };

        jdbcTemplate.batchUpdate(queries);

        // Checks if indexes exist and create them if they don't ('IF NOT EXIST' cannot be used on indexes)
        query = "SELECT COUNT(1) FROM information_schema.STATISTICS " +
                "WHERE TABLE_SCHEMA=DATABASE() AND TABLE_NAME='users' " +
                "AND INDEX_NAME = ?";
        String index = "userIndex";
        Integer indexes = jdbcTemplate.queryForObject(query, Integer.class, index);

        if (indexes == null || indexes == 0) {

            query = "CREATE FULLTEXT INDEX userIndex ON users(username, email, firstname, lastname, phone_no)";
            jdbcTemplate.update(query);
        }

        // Creates an Admin account if none exists
        String getNumberOfAdmins = "SELECT COUNT(*) FROM authorities WHERE authority = ?";
        Integer numberOfAdmins = jdbcTemplate.queryForObject(
                getNumberOfAdmins, Integer.class, "ROLE_ADMIN");

        if (numberOfAdmins == null || numberOfAdmins == 0) {
            User admin = new User("Admin", "admin@cocs.com", "Admin",
                    "Admin", "12345678", "0000");
            admin.setPassword(passwordEncoder.encode(admin.getPassword()));

            Authority adminAuthority = new Authority(admin, "ROLE_ADMIN");

            String insertAdmin = "insert into users (username, email, firstname, " +
                    "lastname, phone_no, password, enabled) values (?, ?, ?, ?, ?, ?, ?);";
            jdbcTemplate.update(insertAdmin,
                    admin.getUsername(), admin.getEmail(), admin.getFirstname(),
                    admin.getLastname(), admin.getPhone_no(), admin.getPassword(), true);

            String insertAdminAuthority = "insert into authorities (username, authority) values (?, ?);";
            jdbcTemplate.update(insertAdminAuthority,
                    admin.getUsername(), adminAuthority.getAuthority());
        }

        // Creates a Personnel account if none exists
        String getNumberOfPersonnel = "SELECT COUNT(*) FROM authorities WHERE authority = ?" ;
        Integer numberOfPersonnel = jdbcTemplate.queryForObject(
                getNumberOfPersonnel, Integer.class, "ROLE_PERSONNEL");

        if (numberOfPersonnel == null || numberOfPersonnel == 0) {
            User personnel = new User("Personnel", "personnel@cocs.com", "Test",
                    "Personnel", "87654321", "0000");
            personnel.setPassword(passwordEncoder.encode(personnel.getPassword()));

            Authority personnelAuthority = new Authority(personnel,"ROLE_PERSONNEL");

            String insertPersonnel = "insert into users (username, email, firstname, " +
                    "lastname, phone_no, password, enabled) values (?, ?, ?, ?, ?, ?, ?);";
            jdbcTemplate.update(insertPersonnel,
                    personnel.getUsername(), personnel.getEmail(), personnel.getFirstname(),
                    personnel.getLastname(), personnel.getPhone_no(), personnel.getPassword(), true);

            String insertPersonnelAuthority = "insert into authorities (username, authority) values (?, ?);";
            jdbcTemplate.update(insertPersonnelAuthority,
                    personnel.getUsername(), personnelAuthority.getAuthority());
        }
    }
}
