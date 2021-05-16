package com.thethreeamigosmakingsense.covidoperationscentralsystem.Repository;

import com.thethreeamigosmakingsense.covidoperationscentralsystem.Model.Booking;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import javax.servlet.http.HttpServletRequest;
import javax.sql.DataSource;
import java.sql.PreparedStatement;
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

        String bookingsSQL = "insert into bookings values (?, ?, ?, ?, ?);";

        KeyHolder keyHolder = new GeneratedKeyHolder();
        try {
            jdbcTemplate.update(
                    connection -> {
                        PreparedStatement ps = connection.prepareStatement(bookingsSQL, new String[] {"id"});
                        ps.setString(1, null);
                        ps.setString(2, http.getRemoteUser());
                        ps.setString(3, booking.getDate());
                        ps.setString(4, booking.getTime());
                        ps.setString(5, booking.getType());
                        return ps;
                    },
                    keyHolder);

            if (booking.getType().equals("test")) {
                jdbcTemplate.update("insert into TestResult values (?, ?);", keyHolder.getKey(), "TEST_PENDING");

            } else if (booking.getType().equals("vaccine")) {
                jdbcTemplate.update("insert into authorities values (?, ?);", keyHolder.getKey()
                        , "FIRST_SHOT");
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
        return jdbcTemplate.query(sql, rowMapper);
    }

    private boolean checkAvailability(Booking booking) {

        for (Booking booked : fetchAllBookings()) {
            if (booked.getDate().equals(booking.getDate()) &&
                    booked.getTime().equals(booking.getTime()))
                return false;
        }
        return true;
    }
}
