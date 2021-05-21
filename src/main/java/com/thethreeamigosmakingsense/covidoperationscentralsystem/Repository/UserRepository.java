package com.thethreeamigosmakingsense.covidoperationscentralsystem.Repository;

import com.thethreeamigosmakingsense.covidoperationscentralsystem.Model.Authority;
import com.thethreeamigosmakingsense.covidoperationscentralsystem.Model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.DependsOn;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Repository;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Repository
@DependsOn("securityConfig")
public class UserRepository {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private HttpServletRequest http;

    public User fetchUser(String username) {

        String query = "SELECT username, email, firstname, lastname, " +
                "phone_no FROM users WHERE username = ?";
        RowMapper<User> rowMapper = new BeanPropertyRowMapper<>(User.class);
        List<User> userList = jdbcTemplate.query(query, rowMapper, username);
        if (userList.size() != 1) return null;
        return userList.get(0);
    }

    public User fetchRemoteUser() {

        String remoteUser = http.getRemoteUser();

        String query = "SELECT username, email, firstname, lastname, " +
                "phone_no FROM users WHERE username = ?";
        RowMapper<User> rowMapper = new BeanPropertyRowMapper<>(User.class);
        List<User> userList = jdbcTemplate.query(query, rowMapper, remoteUser);
        return userList.get(0);
    }

    public List<User> fetchAllUsers() {

        String query = "SELECT users.username, email, firstname, lastname, phone_no  FROM users";
        RowMapper<User> rowMapper = new BeanPropertyRowMapper<>(User.class);
        return jdbcTemplate.query(query, rowMapper);
    }

    public List<User> searchAllUsers(String searchTerm) {

        String query = "SELECT users.username, email, firstname, lastname, phone_no  FROM users " +
                "WHERE MATCH(users.username, email, firstname, lastname, phone_no) " +
                "AGAINST(? IN NATURAL LANGUAGE MODE);";
        RowMapper<User> rowMapper = new BeanPropertyRowMapper<>(User.class);
        return jdbcTemplate.query(query, rowMapper, searchTerm);
    }

    public boolean saveNewUser(User user) {

        Authority authority = new Authority(user, "ROLE_USER");
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        try {
            String query = "insert into users values (?, ?, ?, ?, ?, ?, ?);";
            jdbcTemplate.update(query,
                    user.getUsername(), user.getEmail(), user.getFirstname(),
                    user.getLastname(), user.getPhone_no(), user.getPassword(), true);

            query = "insert into authorities values (?, ?);";
            jdbcTemplate.update(query,
                    user.getUsername(), authority.getAuthority().toString());

        } catch (DataIntegrityViolationException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public boolean updateUser(User user) {

        try {
            String sql = "UPDATE users SET email = ?, firstname = ?, lastname = ?, phone_no = ? " +
                    "WHERE username = ?";
            jdbcTemplate.update(sql,
            user.getEmail(), user.getFirstname(), user.getLastname(), user.getPhone_no(), http.getRemoteUser());
        } catch (DataIntegrityViolationException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public String getAuthority(String username) {
        String query = "SELECT * FROM authorities WHERE username = ?";
        RowMapper<Authority> rowMapper = new BeanPropertyRowMapper<>(Authority.class);
        List<Authority> authList = jdbcTemplate.query(query, rowMapper, username);
        return authList.get(0).getAuthority();
    }
}