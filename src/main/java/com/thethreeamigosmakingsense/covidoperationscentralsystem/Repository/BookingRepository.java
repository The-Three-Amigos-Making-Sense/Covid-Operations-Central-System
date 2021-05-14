package com.thethreeamigosmakingsense.covidoperationscentralsystem.Repository;

import com.thethreeamigosmakingsense.covidoperationscentralsystem.Model.Authority;
import com.thethreeamigosmakingsense.covidoperationscentralsystem.Model.Booking;
import com.thethreeamigosmakingsense.covidoperationscentralsystem.Model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import javax.servlet.http.HttpServletRequest;
import javax.sql.DataSource;
import java.util.List;

@Repository
public class BookingRepository {

    @Autowired
    DataSource dataSource;

    @Autowired
    JdbcTemplate jdbcTemplate;

    @Autowired
    HttpServletRequest http;

    public boolean createBooking(Booking booking) {

        System.out.println(booking.getTime());

        try {
            int id = jdbcTemplate.update("insert into bookings values (?, ?, ?, ?, ?);",
                    null, http.getRemoteUser(), booking.getDate(),
                    booking.getTime(), booking.getType());

            if (booking.getType().equals("test")) {
                jdbcTemplate.update("insert into TestResult values (?, ?);", id, "TEST_PENDING");

            } else if (booking.getType().equals("vaccine")) {
                jdbcTemplate.update("insert into authorities values (?, ?);",
                        id, "FIRST_SHOT");
            }



        } catch (DuplicateKeyException e) {
            e.printStackTrace();
            return false;
        } catch (DataIntegrityViolationException e) {
            e.printStackTrace();
            return false;
        }
        return true;

    }

    public List<Booking> fetchAllBookings() {

        String sql = "SELECT * FROM bookings";
        RowMapper<Booking> rowMapper = new BeanPropertyRowMapper<>(Booking.class);
        List<Booking> bookingsList = jdbcTemplate.query(sql, rowMapper);
        return bookingsList;
    }
}
