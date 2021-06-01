-- cocsdb.users definition

CREATE TABLE `users` (
                         `username` varchar(11) NOT NULL,
                         `email` varchar(320) NOT NULL,
                         `firstname` varchar(25) NOT NULL,
                         `lastname` varchar(25) NOT NULL,
                         `phone_no` varchar(8) NOT NULL,
                         `password` varchar(60) NOT NULL,
                         `enabled` tinyint(1) NOT NULL,
                         PRIMARY KEY (`username`),
                         UNIQUE KEY `email` (`email`),
                         UNIQUE KEY `phone_no` (`phone_no`),
                         FULLTEXT KEY `userIndex` (`username`,`email`,`firstname`,`lastname`,`phone_no`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;


-- cocsdb.authorities definition

CREATE TABLE `authorities` (
                               `username` varchar(11) NOT NULL,
                               `authority` varchar(14) NOT NULL,
                               PRIMARY KEY (`username`),
                               CONSTRAINT `Authorities` FOREIGN KEY (`username`) REFERENCES `users` (`username`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;


-- cocsdb.bookings definition

CREATE TABLE `bookings` (
                            `booking_id` int(11) NOT NULL AUTO_INCREMENT,
                            `username` varchar(11) NOT NULL,
                            `date` varchar(10) NOT NULL,
                            `time` varchar(5) NOT NULL,
                            `type` varchar(7) NOT NULL,
                            PRIMARY KEY (`booking_id`),
                            KEY `Bookings` (`username`),
                            CONSTRAINT `Bookings` FOREIGN KEY (`username`) REFERENCES `users` (`username`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=11 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;


-- cocsdb.testresult definition

CREATE TABLE `testresult` (
                              `booking_id` int(10) NOT NULL,
                              `status` varchar(14) NOT NULL,
                              PRIMARY KEY (`booking_id`),
                              CONSTRAINT `TestResult` FOREIGN KEY (`booking_id`) REFERENCES `bookings` (`booking_id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;


-- cocsdb.vaccine definition

CREATE TABLE `vaccine` (
                           `booking_id` int(10) NOT NULL,
                           `type` varchar(11) NOT NULL,
                           `status` varchar(9) NOT NULL,
                           PRIMARY KEY (`booking_id`),
                           CONSTRAINT `Vaccine` FOREIGN KEY (`booking_id`) REFERENCES `bookings` (`booking_id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;