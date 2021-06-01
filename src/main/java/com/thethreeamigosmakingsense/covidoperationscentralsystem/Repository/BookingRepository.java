package com.thethreeamigosmakingsense.covidoperationscentralsystem.Repository;

import com.thethreeamigosmakingsense.covidoperationscentralsystem.Model.Booking;
import com.thethreeamigosmakingsense.covidoperationscentralsystem.Model.BookingType;
import com.thethreeamigosmakingsense.covidoperationscentralsystem.Model.TestResult;
import com.thethreeamigosmakingsense.covidoperationscentralsystem.Model.Vaccine;
import groovy.lang.Tuple2;
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

import java.sql.PreparedStatement;
import java.util.List;

@Repository
public class BookingRepository {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    /**
     * Method for saving a new booking to database
     * @param booking object
     * @param bookingType TestResult or Vaccine object
     * @return boolean true if successfully save to database
     */
    public boolean createBooking(Booking booking, BookingType bookingType) {

        String sql;

        KeyHolder keyHolder = new GeneratedKeyHolder(); // holds booking_id when generated

        try {
            sql = "insert into bookings values (?, ?, ?, ?, ?);";
            String finalSql = sql;
            jdbcTemplate.update(
                    connection -> {
                        PreparedStatement ps = connection.prepareStatement(finalSql, new String[] {"id"});
                        ps.setString(1, null);
                        ps.setString(2, booking.getUsername());
                        ps.setString(3, booking.getDate());
                        ps.setString(4, booking.getTime());
                        ps.setString(5, booking.getType());
                        return ps;
                    },
                    keyHolder);

            if (bookingType instanceof TestResult) {
                sql = "insert into testresult values (?, ?);";
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

    /**
     * @return list of all bookings in database
     */
    public List<Booking> fetchAllBookings() {
        String sql = "SELECT * FROM bookings";
        RowMapper<Booking> rowMapper = new BeanPropertyRowMapper<>(Booking.class);
        return jdbcTemplate.query(sql, rowMapper);
    }

    /**
     * @param type test or vaccine
     * @return list of all bookings by type
     */
    public List<Booking> fetchAllBookingsByType(String type) {

        String sql = "SELECT * FROM bookings WHERE type = ?";
        RowMapper<Booking> rowMapper = new BeanPropertyRowMapper<>(Booking.class);
        return jdbcTemplate.query(sql, rowMapper, type);
    }

    /**
     * @param id of booking
     * @return Booking object with id
     */
    public Booking fetchBookingByID(int id) {

        String sql = "SELECT * FROM bookings WHERE booking_id = ?";
        RowMapper<Booking> rowMapper = new BeanPropertyRowMapper<>(Booking.class);
        return jdbcTemplate.queryForObject(sql, rowMapper, id);
    }

    /**
     * @param username of user
     * @return list of bookings by user with username
     */
    public List<Booking> fetchUsersBookings(String username) {

        String sql = "SELECT * FROM bookings WHERE username = ? ORDER BY booking_id DESC";
        RowMapper<Booking> rowMapper = new BeanPropertyRowMapper<>(Booking.class);

        return jdbcTemplate.query(sql, rowMapper, username);
    }

    /**
     * @param booking object
     * @return BookingType belonging to booking by foreign key
     */
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

    /**
     * Updates Status column in TestResult or Vaccine table by foreign key
     * @param bookingType TestResult or Vaccine object
     */
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
