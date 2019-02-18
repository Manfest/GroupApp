package com.korchak.thymeleaf.repository;

import com.korchak.thymeleaf.model.Student;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StudentRepository extends JpaRepository<Student, Integer> {



  public List<Student> findByGroups(Integer id);

  public List<Student> findStudentsByGroupsName(String group_name);

  public Student  findStudentById(Integer id);

  public List<Student> findStudentsByNameOrSurnameOrderByName(String name, String surname);

}
