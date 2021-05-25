package com.thethreeamigosmakingsense.covidoperationscentralsystem.Repository;

import com.thethreeamigosmakingsense.covidoperationscentralsystem.Model.Booking;
import com.thethreeamigosmakingsense.covidoperationscentralsystem.Model.BookingType;
import com.thethreeamigosmakingsense.covidoperationscentralsystem.Model.TestResult;
import com.thethreeamigosmakingsense.covidoperationscentralsystem.Model.Vaccine;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import org.springframework.web.server.ResponseStatusException;

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

    public boolean createBooking(Booking booking, BookingType bookingType) {

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

            if (bookingType instanceof TestResult) {
                sql = "insert into TestResult values (?, ?);";
                jdbcTemplate.update(sql,
                        keyHolder.getKey(), bookingType.getStatus());

            } else if (bookingType instanceof Vaccine) {
                sql = "insert into vaccine values (?, ?, ?);";
                jdbcTemplate.update(sql,
                        keyHolder.getKey(), ((Vaccine)bookingType).getType(), bookingType.getStatus());
            }

        } catch (DataIntegrityViolationException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public List<Booking> fetchAllBookingsByType(String type) {

        String sql = "SELECT * FROM bookings WHERE type = ?";
        RowMapper<Booking> rowMapper = new BeanPropertyRowMapper<>(Booking.class);
        return jdbcTemplate.query(sql, rowMapper, type);
    }

    public Booking fetchBookingByID(int id) {

        String sql = "SELECT * FROM bookings WHERE booking_id = ?";
        RowMapper<Booking> rowMapper = new BeanPropertyRowMapper<>(Booking.class);
        return jdbcTemplate.queryForObject(sql, rowMapper, id);
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
            return jdbcTemplate.queryForObject(sql, rowMapper, booking.getBooking_id());
        } else {
            sql = "SELECT * FROM vaccine WHERE booking_id = ?";
            RowMapper<Vaccine> rowMapper = new BeanPropertyRowMapper<>(Vaccine.class);
            return jdbcTemplate.queryForObject(sql, rowMapper, booking.getBooking_id());
        }
    }

    public boolean isAvailable(Booking booking) {

        String sql = "SELECT * FROM bookings WHERE date = ? and time = ? and type = ?";
        RowMapper<Booking> rowMapper = new BeanPropertyRowMapper<>(Booking.class);
        List<Booking> bookingList =
                jdbcTemplate.query(sql, rowMapper, booking.getDate(), booking.getTime(), booking.getType());

        return bookingList.size() == 0;
    }

    public void updateStatus(BookingType bookingType) {

        String sql;

        if (bookingType instanceof TestResult) {
            sql = "UPDATE testresult SET status = ? WHERE booking_id = ?";
            jdbcTemplate.update(sql, bookingType.getStatus(), bookingType.getBooking_id());
            return;

        } else if (bookingType instanceof Vaccine) {
            sql = "UPDATE vaccine SET status = ? WHERE booking_id = ?";
            jdbcTemplate.update(sql, bookingType.getStatus(), bookingType.getBooking_id());
            return;
        }

        throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
    }
}
