package com.korchak.thymeleaf.service;

import com.korchak.thymeleaf.model.Password;
import com.korchak.thymeleaf.repository.PasswordRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PasswordService {

  @Autowired
  private PasswordRepository passwordRepository;


  public void addPassword(Password password) {
    passwordRepository.save(password);
  }

  public void deletePasswordById(Integer id){
    passwordRepository.deleteById(id);
  }
}
