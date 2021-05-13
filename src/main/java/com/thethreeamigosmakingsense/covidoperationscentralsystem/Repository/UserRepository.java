package com.thethreeamigosmakingsense.covidoperationscentralsystem.Repository;

import com.mysql.cj.jdbc.exceptions.MysqlDataTruncation;
import com.mysql.cj.jdbc.exceptions.SQLExceptionsMapping;
import com.thethreeamigosmakingsense.covidoperationscentralsystem.Model.Authority;
import com.thethreeamigosmakingsense.covidoperationscentralsystem.Model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.DependsOn;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Repository;

import javax.servlet.http.HttpServletRequest;

@Repository
@DependsOn("securityConfig")
public class UserRepository {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private HttpServletRequest httpServletRequest;

    public void fetchUser() {

        System.out.println(httpServletRequest.getRemoteUser());

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