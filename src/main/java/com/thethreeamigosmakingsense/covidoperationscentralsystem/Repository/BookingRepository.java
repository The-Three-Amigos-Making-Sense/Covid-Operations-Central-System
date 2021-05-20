package com.thethreeamigosmakingsense.covidoperationscentralsystem.Repository;

import com.thethreeamigosmakingsense.covidoperationscentralsystem.Model.Booking;
import com.thethreeamigosmakingsense.covidoperationscentralsystem.Model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import javax.servlet.http.HttpServletRequest;
import javax.sql.DataSource;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.List;
import java.util.TreeMap;

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

            if (booking.getType().equals("TEST")) {
                jdbcTemplate.update("insert into TestResult values (?, ?);", keyHolder.getKey(), "TEST_PENDING");

            } else if (booking.getType().equals("VACCINE")) {
                jdbcTemplate.update("insert into vaccine values (?, ?);", keyHolder.getKey()
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

    public List<Booking> fetchAllBookings(String type) {

        String sql = "SELECT * FROM bookings WHERE type = ?";
        RowMapper<Booking> rowMapper = new BeanPropertyRowMapper<>(Booking.class);
        return jdbcTemplate.query(sql, rowMapper, type);
    }

    public List<Booking> fetchUsersBookings(String username) {
        String sql = "SELECT * FROM bookings WHERE username = ?";
        RowMapper<Booking> rowMapper = new BeanPropertyRowMapper<>(Booking.class);
        return jdbcTemplate.query(sql, rowMapper, username);
    }

    public String fetchStatus(Booking booking) {

        String type = booking.getType();
        String sql;

        if (type.equals("TEST")) {

            sql = "SELECT status FROM testresult WHERE booking_id = ?";

        } else {

            sql = "SELECT type FROM vaccine WHERE booking_id = ?";
        }

        //RowMapper<String> rowMapper = new BeanPropertyRowMapper<>(String.class);
        System.out.println(booking.getBooking_id());
        return jdbcTemplate.queryForObject(sql, String.class, booking.getBooking_id());

    }
}
