package com.korchak.thymeleaf.model;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.persistence.Table;

@Entity
@Table(name="user_tab",schema = "people")
public class User {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "user_id")
  private Integer id;

  @Column(name="name", nullable = false, length = 30)
  private String name;

  @Column(name = "surname", length = 30)
  private String surname;

  @Column(name = "email",length = 30, nullable = false)
  private String email;

  @OneToOne(mappedBy = "user", cascade = CascadeType.ALL)
  private Password password;


  public User() {
  }

  public User(String name, String surname, String email) {
    this.name = name;
    this.surname = surname;
    this.email = email;
  }

  public User(String name, String surname, String email,
      Password password) {
    this.name = name;
    this.surname = surname;
    this.email = email;
    this.password = password;
  }


  public User(String name, String email, Password password) {
    this.name = name;
    this.email = email;
    this.password = password;
  }

  public Integer getId() {
    return id;
  }

  public void setId(Integer id) {
    this.id = id;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getSurname() {
    return surname;
  }

  public void setSurname(String surname) {
    this.surname = surname;
  }

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public Password getPassword() {
    return password;
  }

  public void setPassword(Password password) {
    this.password = password;
  }


  @Override
  public String toString() {
    return "User{" +
        "id=" + id +
        ", name='" + name + '\'' +
        ", surname='" + surname + '\'' +
        ", email='" + email + '\'' +
        ", password=" + password +
        '}';
  }
}

