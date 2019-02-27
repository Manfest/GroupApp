package com.korchak.thymeleaf.controller;

import com.korchak.thymeleaf.model.Group;
import com.korchak.thymeleaf.model.Student;
import com.korchak.thymeleaf.service.CurrentUserService;
import com.korchak.thymeleaf.service.EmailService;
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

  private static CurrentUserService currentUserService;
  private StudentService studentService;
  private GroupService groupService;
  private EmailService emailService;

  private static String infoMessage = "";
  private static String nameMessage = "";
  private static String surMessage = "";
  private static String emailMessage = "";
  private static String sort = "asc";

  @Autowired
  public StudentController(StudentService studentService, GroupService groupService,
      EmailService emailService, CurrentUserService currentUserService) {

    StudentController.currentUserService = currentUserService;
    this.studentService = studentService;
    this.groupService = groupService;
    this.emailService = emailService;
  }


  private static boolean isCurrentUser() {
    if (currentUserService.getUser() != null) {
      return true;
    } else {
      return false;
    }
  }

  private static String getCurrentUserName() {
    if ((currentUserService.getUser().getSurname() != null)) {
      return ((currentUserService.getUser().getName().concat(" ")
          .concat(currentUserService.getUser().getSurname())));
    } else {
      return (currentUserService.getUser().getName());
    }
  }

  // students page


  @GetMapping(value = "/students/search")
  public String searchForStudent(@RequestParam String searchName, Model model) {
    if (!isCurrentUser()) {
      return "redirect:/";
    }
    model.addAttribute("userName", getCurrentUserName());
    model.addAttribute("title", "searched students");
    model.addAttribute("students",
        studentService.getStudentByNameOrSurname(searchName, searchName));
    return "student/studentList";
  }


  @GetMapping(value = "/students")
  public String getAllStudents(Model model) {
    if (!isCurrentUser()) {
      return "redirect:/";
    }
    model.addAttribute("userName", getCurrentUserName());
    model.addAttribute("title", "all students");
    model.addAttribute("infoMessage", infoMessage);
    infoMessage = "";
    if (sort.equals("asc")) {
      model.addAttribute("students", studentService.getAllAsc());
    } else {
      model.addAttribute("students", studentService.getAllDesc());
    }
    return "student/studentList";
  }

  @GetMapping(value = "/students/sort")
  public String sortStudents() {
    if (sort.equals("asc")) {
      sort = "desc";
    } else {
      sort = "asc";
    }
    return "redirect:/students";
  }


  @GetMapping(value = "/students/{studentId}/edit")
  public String editStudent(@PathVariable Integer studentId, Model model) {
    if (!isCurrentUser()) {
      return "redirect:/";
    }
    model.addAttribute("userName", getCurrentUserName());
    model.addAttribute("emailMessage", emailMessage);
    model.addAttribute("nameMessage", nameMessage);
    model.addAttribute("surMessage", surMessage);
    model.addAttribute("title", "edit student");
    model.addAttribute("student", studentService.getStudentById(studentId));
    nameMessage = "";
    surMessage = "";
    emailMessage = "";

    return "student/editStudentForm";
  }

  @PostMapping(value = "/students/{studentId}/edit")
  public String putEditedStudent(@ModelAttribute Student student, @PathVariable Integer studentId) {
    String name = student.getName();
    String surname = student.getSurname();
    String email = student.getEmail();
    Group group;
    if ((!name.equalsIgnoreCase("null")) && (name.trim().length() != 0)) {
      if (name.trim().length() > 30) {
        nameMessage = " should be less then 30 characters!";
        return "redirect:/students/{studentId}/edit";
      }
      if (!surname.equalsIgnoreCase("null")) {
        if (surname.trim().length() > 40) {
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
          } catch (InputMismatchException e) {
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
  public String deleteStudent(@PathVariable Integer studentId) {
    if (!isCurrentUser()) {
      return "redirect:/";
    }
    studentService.deleteByid(studentId);
    infoMessage = "student was deleted successful";
    return "redirect:/students";
  }

  @GetMapping(value = "/students/{id}/send_email")
  public String getSendEmailPage(@PathVariable Integer id, Model model) {
    if (!isCurrentUser()) {
      return "redirect:/";
    }
    model.addAttribute("userName", getCurrentUserName());
    model.addAttribute("title", "send email");
    model.addAttribute("studentName", studentService.getStudentById(id).getName()
        .concat(" ").concat(studentService.getStudentById(id).getSurname()));
    model.addAttribute("emailMessage", emailMessage);
    emailMessage = "";

    return "student/sendEmail";
  }


  @PostMapping(value = "/students/{id}/send_email")
  public String postEmailpage(@RequestParam String emailText, @RequestParam String subject,
      @PathVariable Integer id) {
    if (emailText.length() == 0) {
      emailMessage = "message is required!";
      return "redirect:/students/{id}/send_email";
    }
    String userEmail = currentUserService.getUser().getEmail();
    String studentEmail = studentService.getStudentById(id).getEmail();
    emailService.sendEmailToStudent(userEmail, studentEmail, emailText, subject);
    infoMessage = "email sended successful";
    return "redirect:/students";
  }

  // groups page


  @GetMapping(value = "/groups/{groupName}/students")
  public String getStudentsOfGroup(@PathVariable String groupName, Model model) {
    if (!isCurrentUser()) {
      return "redirect:/";
    }
    model.addAttribute("userName", getCurrentUserName());
    model.addAttribute("groupName", groupName);
    model.addAttribute("title", "students of " + groupName);
    model.addAttribute("students", studentService.getStudentsOfGroup(groupName));
    model.addAttribute("infoMessage", infoMessage);
    infoMessage = "";
    return "student/studentsOfGroup";
  }

  @GetMapping(value = "/groups/{groupName}/students/sort")
  public String sortStudentsOfGroup(@PathVariable String groupName) {
    if (sort.equals("asc")) {
      sort = "desc";
    } else {
      sort = "asc";
    }
    return "redirect:/groups/{groupName}/students";
  }


  @GetMapping(value = "/groups/{groupName}/students/add")
  public String getPageAddStudent(@PathVariable String groupName, Model model) {
    if (!isCurrentUser()) {
      return "redirect:/";
    }
    model.addAttribute("userName", getCurrentUserName());
    model.addAttribute("title", "add student to " + groupName);
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
    if ((!name.equalsIgnoreCase("null")) && (name.trim().length() != 0)) {
      if (name.trim().length() > 30) {
        nameMessage = " should be less then 30 characters!";
        return "redirect:/groups/{groupName}/students/add";
      } else if (!name.matches("^([a-zA-Z]+)$")) {
        nameMessage = "wrong name!";
        return "redirect:/groups/{groupName}/students/add";
      }
      if (!surname.equalsIgnoreCase("null")) {
        if (surname.length() > 40) {
          surMessage = " should be less then 30 characters!";
          return "redirect:/groups/{groupName}/students/add";
        } else if ((surname.trim().length() != 0) && (!surname.matches("^([a-zA-Z]+)$"))) {
          surMessage = "wrong surname!";
          return "redirect:/groups/{groupName}/students/add";
        }
        if ((email != null) && (email.trim().length() != 0)) {
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
          } catch (InputMismatchException e) {
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
      @PathVariable Integer studentId, Model model) {
    if (!isCurrentUser()) {
      return "redirect:/";
    }
    model.addAttribute("userName", getCurrentUserName());
    model.addAttribute("nameMessage", nameMessage);
    model.addAttribute("surMessage", surMessage);
    model.addAttribute("emailMessage", emailMessage);
    model.addAttribute("title", "edit student of " + groupName + " group");
    model.addAttribute("student", studentService.getStudentById(studentId));
    nameMessage = "";
    surMessage = "";
    emailMessage = "";

    return "student/editStudentForm";
  }

  @PostMapping(value = "/groups/{groupName}/students/{studentId}/edit")
  public String postEditedStudent(@ModelAttribute Student student, @PathVariable Integer studentId,
      @PathVariable String groupName, Model model) {
    String name = student.getName();
    String surname = student.getSurname();
    String email = student.getEmail();
    Group group;
    if ((!name.equalsIgnoreCase("null")) && (name.trim().length() != 0)) {
      if (name.trim().length() > 30) {
        nameMessage = " should be less then 30 characters!";
        return "redirect:/groups/{groupName}/students/{studentId}/edit";
      } else if (!name.matches("^([a-zA-Z]+)$")) {
        nameMessage = "wrong name!";
        return "redirect:/groups/{groupName}/students/add";
      }
      if (!surname.equalsIgnoreCase("null")) {
        if (surname.trim().length() > 40) {
          surMessage = " should be less then 30 characters!";
          return "redirect:/groups/{groupName}/students/{studentId}/edit";
        } else if ((surname.trim().length() != 0) && (!surname.matches("^([a-zA-Z]+)$"))) {
          surMessage = "wrong surname!";
          return "redirect:/groups/{groupName}/students/add";
        }
        if ((email != null) && (email.trim().length() != 0)) {
          if (email.trim().length() > 40) {
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
          } catch (InputMismatchException e) {
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
      @PathVariable Integer studentId) {
    if (currentUserService.getUser() == null) {
      return "redirect:/";
    }
    studentService.deleteByid(studentId);
    infoMessage = "student was deleted successful";
    return "redirect:/groups/{groupName}/students";
  }


  @GetMapping(value = "/groups/{name}/students/{id}/send_email")
  public String getSendEmailPageForGroup(@PathVariable Integer id, Model model) {
    if (!isCurrentUser()) {
      return "redirect:/";
    }
    model.addAttribute("title", "send email");
    model.addAttribute("userName", getCurrentUserName());
    model.addAttribute("studentName", studentService.getStudentById(id).getName()
        .concat(" ").concat(studentService.getStudentById(id).getSurname()));
    model.addAttribute("emailMessage", emailMessage);
    emailMessage = "";
    return "student/sendEmail";
  }

  @PostMapping(value = "/groups/{name}/students/{id}/send_email")
  public String postEmailpageForGroup(@RequestParam String emailText, @RequestParam String subject,
      @PathVariable Integer id) {
    if (emailText.trim().length() == 0) {
      emailMessage = "message is required!";
      return "redirect:/groups/{name}/students/{id}/send_email";
    }
    String studentEmail = studentService.getStudentById(id).getEmail();
    String userEmail = currentUserService.getUser().getEmail();
    emailService.sendEmailToStudent(userEmail, studentEmail, emailText, subject);
    infoMessage = "email sended successful";
    return "redirect:/groups/{name}/students";

  }


}
