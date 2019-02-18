package com.korchak.thymeleaf.repository;

import com.korchak.thymeleaf.model.Password;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PasswordRepository extends JpaRepository<Password, Integer> {


}
