package com.korchak.thymeleaf.service;

import com.korchak.thymeleaf.model.User;
import org.springframework.stereotype.Service;

@Service
public class CurrentUserService {

  private User user;

  public User getUser() {
    return user;
  }

  public void setUser(User user) {
    this.user = user;
  }

  public void removeUser(){
    user = null;
  }
}
