package com.thethreeamigosmakingsense.covidoperationscentralsystem.Service;

import com.thethreeamigosmakingsense.covidoperationscentralsystem.Model.Authority;
import com.thethreeamigosmakingsense.covidoperationscentralsystem.Model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.DependsOn;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@DependsOn("securityConfig")
public class UserService {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public boolean registerUser(User user, Authority authority) {

        user.setPassword(passwordEncoder.encode(user.getPassword()));

        try {
            jdbcTemplate.update("insert into users values (?, ?, ?);",
                    user.getUsername(), user.getPassword(), true);
            jdbcTemplate.update("insert into authorities values (?, ?);",
                    user.getUsername(), authority.getAuthority());

        } catch (DuplicateKeyException e) {
            return false;
        }
        return true;
    }
}