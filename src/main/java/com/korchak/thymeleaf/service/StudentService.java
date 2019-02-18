package com.korchak.thymeleaf.service;

import com.korchak.thymeleaf.model.Student;
import com.korchak.thymeleaf.repository.StudentRepository;
import java.sql.SQLInput;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Optional;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Service;

@Service
public class StudentService {

  @Autowired
  private StudentRepository studentRepository;

  public List<Student> getAllAsc() {
    return studentRepository.findAll(Sort.by(Direction.ASC, "name"));
  }

  public List<Student> getAllDesc(){
    return studentRepository.findAll(Sort.by(Direction.DESC, "name"));
  }

  public List<Student> getStudentsOfGroup(String groupName) {
    return studentRepository.findStudentsByGroupsName(groupName);
  }

  public void addStudent(Student student) throws InputMismatchException {
    try {
      studentRepository.save(student);
    } catch (Exception e) {
      throw new InputMismatchException(e.getMessage());
    }
  }

  public List<Student> getStudentByNameOrSurname(String name, String surname){
    return studentRepository.findStudentsByNameOrSurnameOrderByName(name, surname);
  }

  public Student getStudentById(Integer id) {
    return studentRepository.findStudentById(id);
  }

  public List<Student> updateByid(Student student) {
//    studentRepository.findById(id).map(s -> {
//      s.setId(id);
//      s.setName(student.getName());
//      s.setSurname(student.getSurname());
//      s.setEmail(student.getEmail());
//      return studentRepository.save(s);
//    }).orElseGet(() -> {
//      student.setId(id);
//      return studentRepository.save(student);
//    });
    studentRepository.save(student);
    return studentRepository.findAll();
  }

  public List<Student> deleteByid(Integer id) {
    studentRepository.deleteById(id);
    return studentRepository.findAll();
  }
}
