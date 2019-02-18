package com.korchak.thymeleaf.service;

import com.korchak.thymeleaf.model.User;
import com.korchak.thymeleaf.repository.UserRepository;
import java.util.InputMismatchException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {

  @Autowired
  private UserRepository userRepository;


  public User getUserByEmail(String email){
    return userRepository.findUserByEmail(email);
  }

  public void addUser(User user) throws InputMismatchException {
    try{
      userRepository.save(user);
    }catch (Exception e){
      throw new InputMismatchException(e.getMessage());
    }
  }

  public void deleteUserById(Integer id){
    userRepository.deleteById(id);
  }

}
