INSERT INTO cocsdb.users (username,email,firstname,lastname,phone_no,password,enabled) VALUES
('091055-1234','ladybird55@gmail.com','Mette','Rockholm','12770909','$2a$12$7Kmdc5GeKc2fxb6Pcu0ndu/dOlCGAp5wZaEV9kGzoTG572I0KS0UC',1),
('112290-4321','ralphpet@mail.com','Ralph','Peterson','11229043','$2a$12$XQ/rNsZUHH6V./ucvqOGD.R1NpN.OaAg1wAxokb.2fzozEtjeDb1W',1),
('121212-1212','jenshansen@gmail.com','Jens','Hansen','12121212','$2a$12$C3lhhR3KA8Gx/ZqZcOZBXen.4QnKvn4S6TbXQY8189YKXTriQquaC',1),
('123456-1234','asdf@asdf.com','Johnny','Doe','12345612','$2a$12$C.TQYEEpaFkA0Pk6kGO8levWtbLQoJloLPJTgNvsrZl6yOh98aTXW',1),
('160187-1234','secret@hank.org','Peter','Broman','60681234','$2a$12$IgXkVz9eBqiqH.TedXhUm.rwEPMkMuhM4MDQVVDc1Q758.LmZrgJ6',1),
('281189-1337','coolmaster@cool.com','Jazzy','McJazz','69696969','$2a$12$i.9qMyzoqjcmsF7JlGEY7eUsHdtPrpxvGOR9CiIBHijY4ITbuMkke',1),
('Admin','admin@cocs.com','Bob','Anderson','12345678','$2a$12$YuvXLhgJaYNHp71TOyotBeYOtGj44DYAQymkvLAFgpdcRCH3izpAW',1),
('Personnel','personnel@cocs.com','Bob','Person','87654321','$2a$12$/Z2Z2LadWIqr0wjn5Sn/XOV.F4sltqyeO8tL/litM9n/Zk2PTMnxa',1);

INSERT INTO cocsdb.authorities (username,authority) VALUES
('091055-1234','ROLE_USER'),
('112290-4321','ROLE_USER'),
('121212-1212','ROLE_USER'),
('123456-1234','ROLE_USER'),
('160187-1234','ROLE_USER'),
('281189-1337','ROLE_ADMIN'),
('Admin','ROLE_ADMIN'),
('Personnel','ROLE_PERSONNEL');

INSERT INTO cocsdb.bookings (username,`date`,`time`,`type`) VALUES
('123456-1234','24-05-2021','18:50','TEST'),
('281189-1337','24-05-2021','18:20','VACCINE'),
('Personnel','25-05-2021','09:10','TEST'),
('281189-1337','25-05-2021','09:50','VACCINE'),
('281189-1337','25-05-2021','10:20','VACCINE'),
('Admin','25-05-2021','09:00','VACCINE'),
('Admin','19-06-2021','09:00','VACCINE'),
('Personnel','26-05-2021','09:00','TEST'),
('Admin','25-06-2021','10:30','VACCINE'),
('160187-1234','27-05-2021','09:00','TEST');
INSERT INTO cocsdb.bookings (username,`date`,`time`,`type`) VALUES
('160187-1234','27-05-2021','09:00','VACCINE'),
('160187-1234','20-06-2021','09:00','VACCINE'),
('Admin','28-05-2021','10:00','TEST'),
('Admin','29-05-2021','09:30','TEST'),
('Admin','31-05-2021','09:00','TEST'),
('Admin','29-06-2021','09:30','VACCINE'),
('Admin','01-06-2021','09:40','TEST'),
('Admin','01-06-2021','09:30','TEST'),
('091055-1234','01-06-2021','18:50','TEST'),
('091055-1234','01-06-2021','18:40','VACCINE');
INSERT INTO cocsdb.bookings (username,`date`,`time`,`type`) VALUES
('091055-1234','01-06-2021','17:40','VACCINE'),
('091055-1234','25-06-2021','18:40','VACCINE'),
('091055-1234','25-06-2021','17:40','VACCINE');

INSERT INTO cocsdb.testresult (booking_id,status) VALUES
(15,'TEST_PENDING'),
(20,'POSITIVE'),
(36,'NEGATIVE'),
(39,'POSITIVE'),
(42,'POSITIVE'),
(43,'POSITIVE'),
(44,'CANCELLED'),
(46,'CANCELLED'),
(47,'CANCELLED'),
(48,'CANCELLED');

INSERT INTO cocsdb.vaccine (booking_id,`type`,status) VALUES
(19,'FIRST_SHOT','CANCELLED'),
(25,'FIRST_SHOT','CANCELLED'),
(26,'FIRST_SHOT','CANCELLED'),
(34,'FIRST_SHOT','RECEIVED'),
(35,'SECOND_SHOT','CANCELLED'),
(38,'SECOND_SHOT','CANCELLED'),
(40,'FIRST_SHOT','RECEIVED'),
(41,'SECOND_SHOT','PENDING'),
(45,'SECOND_SHOT','RECEIVED'),
(49,'FIRST_SHOT','CANCELLED');
INSERT INTO cocsdb.vaccine (booking_id,`type`,status) VALUES
(50,'FIRST_SHOT','RECEIVED'),
(51,'SECOND_SHOT','CANCELLED'),
(52,'SECOND_SHOT','RECEIVED');