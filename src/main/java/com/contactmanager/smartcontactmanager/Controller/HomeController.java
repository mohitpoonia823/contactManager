package com.contactmanager.smartcontactmanager.Controller;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import com.contactmanager.smartcontactmanager.Entities.User;
import com.contactmanager.smartcontactmanager.Helper.Message;
import com.contactmanager.smartcontactmanager.Repository.UserRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;


@Controller
public class HomeController {



    @Autowired
   private UserRepository userRepository;

   @Autowired
   private BCryptPasswordEncoder bCryptPasswordEncoder;

    @GetMapping("/")
    public String home(Model model) {
        model.addAttribute("title", "Home-Smart Contact Manager");
        return "home";
    }

    @GetMapping("/about")
    public String about() {

        return "about";
    }

    @GetMapping("/signup")
    public String signup(Model model) {

        model.addAttribute("user", new User());
        return "signUp";
    }

    @PostMapping("/do_register")
    private String registerUser(@Valid @ModelAttribute("user") User user, BindingResult result1,
            @RequestParam(value = "agreement", defaultValue = "false") boolean agreement, Model model, HttpSession session) {
        try {
            if (!agreement) {
                System.out.println("you have not agreed the terms and conditions");
                throw new Exception("you have not agreed the terms and conditions");
            }
            if(result1.hasErrors()){
                model.addAttribute("user",user);
                return "signUp";
            }
            user.setuRole("ROLE_USER");
            user.setEnabled(true);
            user.setImageUrl("default.png");
            user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
            User result = this.userRepository.save(user);
            model.addAttribute("user", new User());
            session.setAttribute("message", new Message("Successfully Registered!!", "alert-success"));

            return "signUp";

        } catch (Exception e) {
            e.printStackTrace();
            session.setAttribute("message", new Message("Something went wrong!!"+e.getMessage(), "alert-danger"));
            return "signUp";
        }
       
    
    }

    @GetMapping("/signin")
    public String customLogin(Model model){
         model.addAttribute("title", "Login Page");
        return "login";

    }

}
