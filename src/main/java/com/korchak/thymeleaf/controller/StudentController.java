package com.korchak.thymeleaf.controller;

import com.korchak.thymeleaf.model.Group;
import com.korchak.thymeleaf.model.Student;
import com.korchak.thymeleaf.service.CurrentUserService;
import com.korchak.thymeleaf.service.GroupService;
import com.korchak.thymeleaf.service.StudentService;
import java.util.Date;
import java.util.InputMismatchException;
import java.util.regex.Pattern;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class StudentController {

  private StudentService studentService;
  private GroupService groupService;
  private CurrentUserService currentUserService;

  private static String infoMessage = "";
  private static String nameMessage = "";
  private static String surMessage = "";
  private static String emailMessage = "";
  private static String sort = "asc";

  @Autowired
  public StudentController(StudentService studentService, GroupService groupService,
      CurrentUserService currentUserService){
    this.studentService = studentService;
    this.groupService = groupService;
    this.currentUserService = currentUserService;
  }




  @GetMapping(value = "/students/search")
  public String searchForStudent(@RequestParam String searchName, Model model){
    if(currentUserService.getUser() == null){
      return "redirect:/";
    }
    if((currentUserService.getUser().getSurname() != null)) {
      model.addAttribute("userName", (currentUserService.getUser().getName().concat(" ")
          .concat(currentUserService.getUser().getSurname())));
    } else {
      model.addAttribute("userName", currentUserService.getUser().getName());
    }
    model.addAttribute("title", "all students");
    model.addAttribute("students",
        studentService.getStudentByNameOrSurname(searchName, searchName));
    return "student/studentList";
  }


  // students page


  @GetMapping(value = "/students")
  public String getAllStudents(Model model) {
    if(currentUserService.getUser() == null){
      return "redirect:/";
    }
    if((currentUserService.getUser().getSurname() != null)) {
      model.addAttribute("userName", (currentUserService.getUser().getName().concat(" ")
          .concat(currentUserService.getUser().getSurname())));
    } else {
      model.addAttribute("userName", currentUserService.getUser().getName());
    }
    model.addAttribute("title", "students");
    model.addAttribute("infoMessage", infoMessage);
    infoMessage = "";
    if(sort.equals("asc")) {
      model.addAttribute("students", studentService.getAllAsc());
    } else {
      model.addAttribute("students", studentService.getAllDesc());
    }
    return "student/studentList";
  }

  @GetMapping(value = "/students/sort")
  public String sortStudents(){
    if(sort.equals("asc")){
      sort = "desc";
    } else {
      sort = "asc";
    }
    return "redirect:/students";
  }


  @GetMapping(value = "/students/{studentId}/edit")
  public String editStudent(@PathVariable Integer studentId, Model model){
    if(currentUserService.getUser() == null){
      return "redirect:/";
    }
    if((currentUserService.getUser().getSurname() != null)) {
      model.addAttribute("userName", (currentUserService.getUser().getName().concat(" ")
          .concat(currentUserService.getUser().getSurname())));
    } else {
      model.addAttribute("userName", currentUserService.getUser().getName());
    }
    model.addAttribute("nameMessage", nameMessage);
    model.addAttribute("surMessage", surMessage);
    model.addAttribute("emailMessage", emailMessage);
    model.addAttribute("title", "edit");
    model.addAttribute("student", studentService.getStudentById(studentId));
    nameMessage = "";
    surMessage = "";
    emailMessage = "";

    return "student/editStudentForm";
  }

  @PostMapping(value="/students/{studentId}/edit")
  public String putEditedStudent(@ModelAttribute Student student, @PathVariable Integer studentId){
    String name = student.getName();
    String surname = student.getSurname();
    String email = student.getEmail();
    Group group;
    if (!(name.equalsIgnoreCase("null")) && (name.length() != 0)) {
      if (name.length() > 30) {
        nameMessage = " should be less then 30 characters!";
        return "redirect:/students/{studentId}/edit";
      }
      if (!(surname.equalsIgnoreCase("null")) && (surname.length() != 0)) {
        if (surname.length() > 40) {
          surMessage = " should be less then 30 characters!";
          return "redirect:/students/{studentId}/edit";
        }
        if (!(email.equalsIgnoreCase("null")) && (email.length() != 0)) {
          if (email.length() > 40) {
            emailMessage = " should be less then 40 characters!";
            return "redirect:/students/{studentId}/edit";
          }
          if (!email.matches("^([a-zA-Z0-9_\\-\\.]+)@([a-zA-Z0-9_\\-\\.]+)\\.([a-zA-Z]{2,5})$")) {
            emailMessage = "wrong email!";
            return "redirect:/students/{studentId}/edit";
          }

          try {
            student.setId(studentId);
            studentService.addStudent(student);
          } catch (InputMismatchException e){
            emailMessage = "email already exists!";
            return "redirect:/students/{studentId}/edit";

          }
        } else {
          emailMessage = "email is required!";
          return "redirect:/students/{studentId}/edit";
        }


      } else {
        surMessage = "surname is required!";
        return "redirect:/students/{studentId}/edit";
      }


    } else {
      nameMessage = "name is required!!";
      return "redirect:/students/{studentId}/edit";
    }
    infoMessage = "student was edited successful";
    return "redirect:/students";
  }

  @GetMapping(value = "/students/{studentId}")
  public String deleteStudent(@PathVariable Integer studentId){
    if(currentUserService.getUser() == null){
      return "redirect:/";
    }
    studentService.deleteByid(studentId);
    infoMessage = "student was deleted successful";
    return "redirect:/students";
  }


  // groups page



  @GetMapping(value = "/groups/{groupName}/students")
  public String getStudentsOfGroup(@PathVariable String groupName, Model model) {
    if(currentUserService.getUser() == null){
      return "redirect:/";
    }
    if((currentUserService.getUser().getSurname() != null)) {
      model.addAttribute("userName", (currentUserService.getUser().getName().concat(" ")
          .concat(currentUserService.getUser().getSurname())));
    } else {
      model.addAttribute("userName", currentUserService.getUser().getName());
    }
    model.addAttribute("groupName", groupName);
    model.addAttribute("title", "students_of_" + groupName);
    model.addAttribute("students", studentService.getStudentsOfGroup(groupName));
    model.addAttribute("infoMessage", infoMessage);
    infoMessage = "";
    return "student/studentsOfGroup";
  }

  @GetMapping(value = "/groups/{groupName}/students/sort")
  public String sortStudentsOfGroup(@PathVariable String groupName){
    if(sort.equals("asc")){
      sort = "desc";
    } else {
      sort = "asc";
    }
    return "redirect:/groups/{groupName}/students";
  }


  @GetMapping(value = "/groups/{groupName}/students/add")
  public String getPageAddStudent(@PathVariable String groupName, Model model) {
    if(currentUserService.getUser() == null){
      return "redirect:/";
    }
    if((currentUserService.getUser().getSurname() != null)) {
      model.addAttribute("userName", (currentUserService.getUser().getName().concat(" ")
          .concat(currentUserService.getUser().getSurname())));
    } else {
      model.addAttribute("userName", currentUserService.getUser().getName());
    }
    model.addAttribute("title", "add");
    model.addAttribute("groupName", groupName);
    model.addAttribute("nameMessage", nameMessage);
    model.addAttribute("surMessage", surMessage);
    model.addAttribute("emailMessage", emailMessage);
    nameMessage = "";
    surMessage = "";
    emailMessage = "";
    return "student/addStudent";
  }

  @PostMapping(value = "/groups/{groupName}/students/add")
  public String addStudent(@ModelAttribute Student student, @PathVariable String groupName,
      Model model) {
    model.addAttribute("title", "add student");
    String name = student.getName();
    String surname = student.getSurname();
    String email = student.getEmail();
    Group group;
    if ((name != null) && (name.length() != 0)) {
      if (name.length() > 30) {
        nameMessage = " should be less then 30 characters!";
        return "redirect:/groups/{groupName}/students/add";
      }
      if ((surname != null) && (surname.length() != 0)) {
        if (surname.length() > 40) {
          surMessage = " should be less then 30 characters!";
          return "redirect:/groups/{groupName}/students/add";
        }
        if ((email != null) && (email.length() != 0)) {
          if (email.length() > 40) {
            emailMessage = " should be less then 40 characters!";
            return "redirect:/groups/{groupName}/students/add";
          }
          if (!email.matches("^([a-zA-Z0-9_\\-\\.]+)@([a-zA-Z0-9_\\-\\.]+)\\.([a-zA-Z]{2,5})$")) {
            emailMessage = "wrong email!";
            return "redirect:/groups/{groupName}/students/add";
          }

          try {
            group = groupService.getGroupByName(groupName);
            student.addGroup(group);
//          student.setDate(new Date());
//          group.addStudent(student);
//          groupService.updateGroup(group);
            studentService.addStudent(student);
          } catch (InputMismatchException e){
            emailMessage = "email already exists!";
            return "redirect:/groups/{groupName}/students/add";

          }
        } else {
          emailMessage = "email is required!";
          return "redirect:/groups/{groupName}/students/add";
        }


      } else {
        surMessage = "surname is required!";
        return "redirect:/groups/{groupName}/students/add";
      }


    } else {
      nameMessage = "name is required!!";
      return "redirect:/groups/{groupName}/students/add";
    }
    infoMessage = "student was added";
    return "redirect:/groups/{groupName}/students";
  }


  @GetMapping(value = "/groups/{groupName}/students/{studentId}/edit")
  public String getEditStudentOfGroup(@PathVariable String groupName,
      @PathVariable Integer studentId, Model model){
    if(currentUserService.getUser() == null){
      return "redirect:/";
    }
    if((currentUserService.getUser().getSurname() != null)) {
      model.addAttribute("userName", (currentUserService.getUser().getName().concat(" ")
          .concat(currentUserService.getUser().getSurname())));
    } else {
      model.addAttribute("userName", currentUserService.getUser().getName());
    }
    model.addAttribute("nameMessage", nameMessage);
    model.addAttribute("surMessage", surMessage);
    model.addAttribute("emailMessage", emailMessage);
    model.addAttribute("title", "edit student");
    model.addAttribute("student", studentService.getStudentById(studentId));
    nameMessage = "";
    surMessage = "";
    emailMessage = "";

    return "student/editStudentForm";
  }

  @PostMapping(value = "/groups/{groupName}/students/{studentId}/edit")
  public String postEditedStudent(@ModelAttribute Student student, @PathVariable Integer studentId,
      @PathVariable String groupName, Model model){
    String name = student.getName();
    String surname = student.getSurname();
    String email = student.getEmail();
    Group group;
    if ((name != null) && (name.length() != 0)) {
      if (name.length() > 30) {
        nameMessage = " should be less then 30 characters!";
        return "redirect:/groups/{groupName}/students/{studentId}/edit";
      }
      if ((surname != null) && (surname.length() != 0)) {
        if (surname.length() > 40) {
          surMessage = " should be less then 30 characters!";
          return "redirect:/groups/{groupName}/students/{studentId}/edit";
        }
        if ((email != null) && (email.length() != 0)) {
          if (email.length() > 40) {
            emailMessage = " should be less then 40 characters!";
            return "redirect:/groups/{groupName}/students/{studentId}/edit";
          }
          if (!email.matches("^([a-zA-Z0-9_\\-\\.]+)@([a-zA-Z0-9_\\-\\.]+)\\.([a-zA-Z]{2,5})$")) {
            emailMessage = "wrong email!";
            return "redirect:/groups/{groupName}/students/{studentId}/edit";
          }

          try {
            student.setId(studentId);
            group = groupService.getGroupByName(groupName);
            student.addGroup(group);
            studentService.addStudent(student);
          } catch (InputMismatchException e){
            emailMessage = "email already exists!";
            return "redirect:/groups/{groupName}/students/{studentId}/edit";

          }
        } else {
          emailMessage = "email is required!";
          return "redirect:/groups/{groupName}/students/{studentId}/edit";
        }


      } else {
        surMessage = "surname is required!";
        return "redirect:/groups/{groupName}/students/{studentId}/edit";
      }


    } else {
      nameMessage = "name is required!!";
      return "redirect:/groups/{groupName}/students/{studentId}/edit";
    }
    infoMessage = "student was edited successful";
    return "redirect:/groups/{groupName}/students";
  }


  @GetMapping(value = "/groups/{groupName}/students/{studentId}/delete")
  public String deleteStudentFromGroup(@PathVariable String groupName,
      @PathVariable Integer studentId){
    if(currentUserService.getUser() == null){
      return "redirect:/";
    }
    studentService.deleteByid(studentId);
    infoMessage = "student was deleted successful";
    return "redirect:/groups/{groupName}/students";
  }

//  @GetMapping(value = "/{groupName}/students")
//  public List<Student> getAll(@PathVariable Integer groupId) {
//    return studentService.getAllStudents(groupId);
//  }
//
//  @PostMapping(value = "/{groupName}/students")
//  public List<Student> postOne(@RequestBody final Student student, @PathVariable Integer groupId) {
//    student.addGroup(new Group(groupId));
//    return studentService.postStudent(student);
//  }
//
//  @GetMapping(value = "/{groupName}/students/{id}")
//  public Optional<Student> getStudent(@PathVariable Integer id) {
//    return studentService.getStudentById(id);
//  }
//
//  @PutMapping(value = "/{groupName}/students/{id}")
//  public List<Student> updateStudent(@RequestBody Student student, @PathVariable Integer groupId) {
//    student.addGroup(new Group(groupId));
//    return studentService.updateByid(student);
//  }
//
//  @DeleteMapping(value = "/{groupName}/students/{id}")
//  public List<Student> deleteStudent(@PathVariable Integer id) {
//    return studentService.deleteByid(id);
//  }

}
