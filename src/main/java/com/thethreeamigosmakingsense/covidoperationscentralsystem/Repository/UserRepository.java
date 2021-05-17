package com.thethreeamigosmakingsense.covidoperationscentralsystem.Repository;

import com.thethreeamigosmakingsense.covidoperationscentralsystem.Model.Authority;
import com.thethreeamigosmakingsense.covidoperationscentralsystem.Model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.DependsOn;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.DuplicateKeyException;
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

        String sql = "SELECT username, email, firstname, lastname, " +
                "phone_no FROM users WHERE username = ?";
        RowMapper<User> rowMapper = new BeanPropertyRowMapper<>(User.class);
        List<User> userList = jdbcTemplate.query(sql, rowMapper, remoteUser);
        return userList.get(0);

    }

    public boolean saveNewUser(User user) {

        Authority authority = new Authority(user, "ROLE_USER");
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        try {
            jdbcTemplate.update("insert into users values (?, ?, ?, ?, ?, ?, ?);",
                    user.getUsername(), user.getEmail(), user.getFirstname(),
                    user.getLastname(), user.getPhone_no(), user.getPassword(), true);
            jdbcTemplate.update("insert into authorities values (?, ?);",
                    user.getUsername(), authority.getAuthority());

        } catch (DuplicateKeyException e) {
            e.printStackTrace();
            return false;
        } catch (DataIntegrityViolationException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }
}