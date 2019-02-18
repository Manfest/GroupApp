package com.korchak.thymeleaf.model;

import java.util.Set;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.Table;

@Entity
@Table(name = "lab_group", schema = "people")
public class Group {



  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "group_id")
  private Integer id;

  @Column(name = "group_name")
  private String name;

  @ManyToMany(mappedBy = "groups", fetch = FetchType.LAZY)
  private Set<Student> students;


  public Group() {
  }

  public Group(String name) {
    this.name = name;
//    this.students = students;
  }

  public void addStudent(Student student) {
    this.students.add(student);
  }

  public Set<Student> getStudents() {
    return students;
  }

  public void setStudents(Set<Student> students) {
    this.students = students;
  }

  public Group(Integer id) {
    this.id = id;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public Integer getId() {
    return id;
  }

  public void setId(Integer id) {
    this.id = id;
  }
}
