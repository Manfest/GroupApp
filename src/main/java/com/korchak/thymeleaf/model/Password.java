package com.korchak.thymeleaf.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

@Entity
@Table(name = "user_password", schema = "people")
public class Password {


  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "pass_id")
  private Integer id;


  @Column(name = "password", length = 40, nullable = false)
  private String password;

  @OneToOne(optional = false)
  @JoinColumn(name = "user_id", nullable = false)
  private User user;


  public Password(){}

  public Password(String password, User user) {
    this.password = password;
    this.user = user;
  }



  public Integer getId() {
    return id;
  }

  public void setId(Integer id) {
    this.id = id;
  }

  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = password;
  }

  public User getUser() {
    return user;
  }

  public void setUser(User user) {
    this.user = user;
  }
}
