package com.thethreeamigosmakingsense.covidoperationscentralsystem.Controller;

import com.thethreeamigosmakingsense.covidoperationscentralsystem.Model.User;
import com.thethreeamigosmakingsense.covidoperationscentralsystem.Repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ProfileController {

@Autowired
UserRepository userRepository;

 @GetMapping("/profile")
    private String profile(Model model){

      User user = userRepository.fetchUser();

      model.addAttribute("navItem","profile");
      model.addAttribute("username",user.getUsername());
      model.addAttribute("email", user.getEmail());
      model.addAttribute("firstname",user.getFirstname());
      model.addAttribute("lastname", user.getLastname());
      model.addAttribute("phone_no", user.getPhone_no());

      return "profile/profile";
 }
}
