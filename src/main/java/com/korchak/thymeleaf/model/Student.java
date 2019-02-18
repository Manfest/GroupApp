package com.korchak.thymeleaf.model;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.Set;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Table;

@Entity
@Table(name = "student", schema = "people")
public class Student {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "student_id", nullable = false)
  private Integer id;

  @Column(name = "name", length = 30, nullable = false)
  private String name;

  @Column(name = "surname", length = 40)
  private String surname;

  @Column(name = "email", nullable = false, length = 40)
  private String email;

  @Column(name = "adding_date")
  private Date date = new Date();

  public Date getDate() {
    return date;
  }

  public void setDate(Date date) {
    this.date = date;
  }

  @ManyToMany(fetch = FetchType.LAZY)
  @JoinTable(name = "student_has_group",
      joinColumns = @JoinColumn(name = "student_id", referencedColumnName = "student_id"),
      inverseJoinColumns = @JoinColumn(name = "group_id", referencedColumnName = "group_id"))
  private Set<Group> groups;


  public Student() {
  }

  public Student(String name, String surname, String email) {
    this.name = name;
    this.surname = surname;
    this.email = email;
  }

  public void addGroup(Group group) {
    if (groups == null) {
      groups = new LinkedHashSet<>();
    }
    this.groups.add(group);
  }

  public Set<Group> getGroups() {
    return groups;
  }

  public void setGroups(Set<Group> groups) {
    this.groups = groups;
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


  public String groupsToString() {
    return groups.toString();
  }
}
