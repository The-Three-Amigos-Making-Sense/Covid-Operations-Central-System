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

    public User fetchUser() {

        String remoteUser = http.getRemoteUser();

        String query = "SELECT username, email, firstname, lastname, " +
                "phone_no FROM users WHERE username = ?";
        RowMapper<User> rowMapper = new BeanPropertyRowMapper<>(User.class);
        List<User> userList = jdbcTemplate.query(query, rowMapper, remoteUser);
        return userList.get(0);
    }

    public List<User> fetchAllUsersWithRole(String role) {

        String query = "SELECT users.username, email, firstname, lastname, phone_no  FROM users " +
                "LEFT JOIN authorities a on users.username = a.username " +
                "WHERE a.authority = ?;";
        RowMapper<User> rowMapper = new BeanPropertyRowMapper<>(User.class);
        return jdbcTemplate.query(query, rowMapper, role);
    }

    public List<User> searchAllUsersWithRole(String role, String searchTerm) {

        String query = "SELECT users.username, email, firstname, lastname, phone_no  FROM users " +
                "LEFT JOIN authorities a on users.username = a.username " +
                "WHERE a.authority = ? " +
                "AND MATCH(users.username, email, firstname, lastname, phone_no) " +
                "AGAINST(? IN NATURAL LANGUAGE MODE);";
        RowMapper<User> rowMapper = new BeanPropertyRowMapper<>(User.class);
        return jdbcTemplate.query(query, rowMapper, role, searchTerm);
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
                    user.getUsername(), authority.getAuthority());

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
}