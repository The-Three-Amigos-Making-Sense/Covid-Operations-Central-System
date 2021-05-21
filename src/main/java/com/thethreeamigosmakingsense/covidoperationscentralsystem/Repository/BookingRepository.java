package com.thethreeamigosmakingsense.covidoperationscentralsystem.Repository;

import com.thethreeamigosmakingsense.covidoperationscentralsystem.Model.Booking;
import com.thethreeamigosmakingsense.covidoperationscentralsystem.Model.BookingType;
import com.thethreeamigosmakingsense.covidoperationscentralsystem.Model.TestResult;
import com.thethreeamigosmakingsense.covidoperationscentralsystem.Model.Vaccine;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
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

        String sql;

        KeyHolder keyHolder = new GeneratedKeyHolder();

        try {
            sql = "insert into bookings values (?, ?, ?, ?, ?);";
            String finalSql = sql;
            jdbcTemplate.update(
                    connection -> {
                        PreparedStatement ps = connection.prepareStatement(finalSql, new String[] {"id"});
                        ps.setString(1, null);
                        ps.setString(2, http.getRemoteUser());
                        ps.setString(3, booking.getDate());
                        ps.setString(4, booking.getTime());
                        ps.setString(5, booking.getType().toString());
                        return ps;
                    },
                    keyHolder);

            if (booking.getType().equals("TEST")) {
                sql = "insert into TestResult values (?, ?);";
                jdbcTemplate.update(sql, keyHolder.getKey(), "TEST_PENDING");

            } else if (booking.getType().equals("VACCINE")) {
                sql = "insert into vaccine values (?, ?, ?);";
                jdbcTemplate.update(sql, keyHolder.getKey(), "FIRST_SHOT", "PENDING");
            }

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

        String sql = "SELECT * FROM bookings WHERE username = ? ORDER BY booking_id DESC";
        RowMapper<Booking> rowMapper = new BeanPropertyRowMapper<>(Booking.class);

        return jdbcTemplate.query(sql, rowMapper, username);
    }

    public BookingType fetchStatus(Booking booking) {

        String type = booking.getType();
        String sql;

        if (type.equals("TEST")) {
            sql = "SELECT * FROM testresult WHERE booking_id = ?";
            RowMapper<TestResult> rowMapper = new BeanPropertyRowMapper<>(TestResult.class);
            return jdbcTemplate.query(sql, rowMapper, booking.getBooking_id()).get(0);
        } else {
            sql = "SELECT * FROM vaccine WHERE booking_id = ?";
            RowMapper<Vaccine> rowMapper = new BeanPropertyRowMapper<>(Vaccine.class);
            return jdbcTemplate.query(sql, rowMapper, booking.getBooking_id()).get(0);
        }
    }
}
