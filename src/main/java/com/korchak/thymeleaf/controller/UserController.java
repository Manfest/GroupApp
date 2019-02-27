package com.korchak.thymeleaf.controller;

import com.korchak.thymeleaf.model.LoginForm;
import com.korchak.thymeleaf.model.Password;
import com.korchak.thymeleaf.model.User;
import com.korchak.thymeleaf.service.CurrentUserService;
import com.korchak.thymeleaf.service.EmailService;
import com.korchak.thymeleaf.service.PasswordService;
import com.korchak.thymeleaf.service.StudentService;
import com.korchak.thymeleaf.service.UserService;
import java.util.InputMismatchException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class UserController {

  private static CurrentUserService currentUserService;
  private UserService userService;
  private PasswordService passwordService;
  private EmailService emailService;

  private static String emailMessage = "";
  private static String passwordMessage = "";
  private static String nameMessage = "";
  private static String surMessage = "";
  private static String infoMessage = "";


  @Autowired
  public UserController(UserService userService, CurrentUserService currentUserService,
      PasswordService passwordService, EmailService emailService) {

    UserController.currentUserService = currentUserService;
    this.userService = userService;
    this.passwordService = passwordService;
    this.emailService = emailService;
  }


  private static boolean isCurrentUser() {
    if (currentUserService.getUser() != null) {
      return true;
    } else {
      return false;
    }
  }

  private static String getCurrentUserName() {
    if ((currentUserService.getUser().getSurname() != null)) {
      return ((currentUserService.getUser().getName().concat(" ")
          .concat(currentUserService.getUser().getSurname())));
    } else {
      return (currentUserService.getUser().getName());
    }
  }


  @GetMapping(value = "/")
  public String getLoginPage(Model model) {
    if (isCurrentUser()) {
      return "redirect:/groups";
    }
    model.addAttribute("title", "login please");
    model.addAttribute("passwordMessage", passwordMessage);
    model.addAttribute("emailMessage", emailMessage);
    model.addAttribute("infoMessage", infoMessage);
    emailMessage = "";
    passwordMessage = "";
    infoMessage = "";
    return "user/loginPage";
  }

  @PostMapping(value = "/")
  public String loginPost(@ModelAttribute LoginForm loginform,
      @Nullable @RequestParam String emailForget,
      Model model) {

    if (emailForget != null) {

      try {
        User user = userService.getUserByEmail(emailForget);
        if (user != null) {
          emailService.sendEmail(user);

          infoMessage = "Password sended on your email!";
          return "redirect:/";
        }

      } catch (Exception e) {
        e.printStackTrace();
      }
      infoMessage = "Email is wrong!";
      return "redirect:/";
    }

    String email = loginform.getEmail();
    String password = loginform.getPassword();

    if (!email.matches("^([a-zA-Z0-9_\\-\\.]+)@([a-zA-Z0-9_\\-\\.]+)\\.([a-zA-Z]{2,5})$")
        || (email.length() > 30)) {
      emailMessage = "wrong email!";
      passwordMessage = "";
      return "redirect:/";
    } else if ((password.length() < 5) || (password.length()) > 30) {
      emailMessage = "";
      passwordMessage = "should be in range [5-30] symbols!";
      return "redirect:/";
    }
    try {
      User user = userService.getUserByEmail(email);
      if (user == null) {
        emailMessage = "wrong email!";
        passwordMessage = "";
        return "redirect:/";
      } else if (!(user.getPassword().getPassword()).equals(password)) {
        emailMessage = "";
        passwordMessage = "wrong password!";
        return "redirect:/";
      } else {
        currentUserService.setUser(user);
        emailMessage = "";
        passwordMessage = "";
        return "redirect:/groups";
      }


    } catch (Exception e) {
      e.printStackTrace();
      emailMessage = "wrong email!";
      return "redirect:/";
    }


  }

  @GetMapping(value = "/logout")
  public String logout(Model model) {
    if (currentUserService.getUser() == null) {
      return "redirect:/";
    }
    currentUserService.removeUser();
    return "redirect:/";
  }


  @GetMapping(value = "/user")
  public String getUserInfo(Model model) {
    if (!isCurrentUser()) {
      return "redirect:/";
    }
    model.addAttribute("userName", getCurrentUserName());
    model.addAttribute("title", "user information");
    model.addAttribute("name", currentUserService.getUser().getName());
    model.addAttribute("surname", currentUserService.getUser().getSurname());
    model.addAttribute("email", currentUserService.getUser().getEmail());
    model.addAttribute("password", currentUserService.getUser().getPassword().getPassword());
    model.addAttribute("nameMessage", nameMessage);
    model.addAttribute("surMessage", surMessage);
    model.addAttribute("emailMessage", emailMessage);
    model.addAttribute("passwordMessage", passwordMessage);
    model.addAttribute("infoMessage", infoMessage);
    infoMessage = "";
    emailMessage = "";
    passwordMessage = "";
    nameMessage = "";
    surMessage = "";

    return "user/userInformation";
  }


  @PostMapping("/user")
  public String putUserInfo(@RequestParam String name, @RequestParam String surname,
      @RequestParam String email, @RequestParam String password) {
    if (currentUserService.getUser() == null) {
      return "redirect:/";
    }

    if (!(name.equalsIgnoreCase("null")) && (name.length() != 0) &&
        (name.matches("^([a-zA-Z]+)$"))) {
      if (name.length() > 30) {
        nameMessage = "should be less then 30 characters!";
        return "redirect:/user";
      }
      if (!(surname.equalsIgnoreCase("null")) && surname.matches("^([a-zA-Z]+)$")) {
        if (surname.length() > 30) {
          surMessage = "should be less then 30 characters!";
          return "redirect:/user";
        }
        if (!(email.equalsIgnoreCase("null")) && (email.length() != 0)) {
          if (email.length() > 30) {
            emailMessage = "should be less then 30 characters!";
            return "redirect:/user";
          }
          if (!email.matches("^([a-zA-Z0-9_\\-\\.]+)@([a-zA-Z0-9_\\-\\.]+)\\.([a-zA-Z]{2,5})$")) {
            emailMessage = "wrong email!";
            return "redirect:/user";
          }
          if (password.matches("[a-zA-Z0-9]+") && (password.length() != 0)) {
            if ((password.length() < 5) || (password.length() > 30)) {
              passwordMessage = "should be in range [5-30] symbols!";
              return "redirect:/user";
            }
            try {
              Password pas = new Password();
              pas.setPassword(password);
              pas.setId(currentUserService.getUser().getPassword().getId());
              User user = new User(name, surname, email);
              user.setId(currentUserService.getUser().getId());
              pas.setUser(user);
              user.setPassword(pas);
              userService.addUser(user);
              passwordService.addPassword(pas);
              currentUserService.setUser(user);

            } catch (InputMismatchException e) {
              emailMessage = "email already exists!";
              return "redirect:/user";
            }
          } else {
            passwordMessage = "wrong password!";
            return "redirect:/user";
          }
        } else {
          emailMessage = "email is required!";
          return "redirect:/user";
        }
      } else {
        surMessage = "surname is required!";
        return "redirect:/user";
      }
    } else {
      nameMessage = "name is required!!";
      return "redirect:/user";
    }
    infoMessage = "information changed";
    return "redirect:/user";
  }




}
