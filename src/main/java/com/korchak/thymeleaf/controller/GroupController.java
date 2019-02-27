package com.korchak.thymeleaf.controller;

import com.korchak.thymeleaf.model.Group;
import com.korchak.thymeleaf.model.Student;
import com.korchak.thymeleaf.service.CurrentUserService;
import com.korchak.thymeleaf.service.GroupService;
import com.korchak.thymeleaf.service.StudentService;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Set;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
//@RequestMapping(value = "/groups")
public class GroupController {

  private static CurrentUserService currentUserService;
  private GroupService groupService;
  private StudentService studentService;

  private static String infoMessage = "";
  private static String nameMessage = "";
  private static String sort = "asc";

  @Autowired

  public GroupController(GroupService groupService, StudentService studentService,
      CurrentUserService currentUserService) {

    GroupController.currentUserService = currentUserService;
    this.groupService = groupService;
    this.studentService = studentService;
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


  @GetMapping(value = "/groups")
  public String getAll(Model model) {
    if (!isCurrentUser()) {
      return "redirect:/";
    }
    model.addAttribute("userName", getCurrentUserName());
    if (sort.equals("asc")) {
      model.addAttribute("groups", groupService.getAllGroupsAsc());
    } else {
      model.addAttribute("groups", groupService.getAllGroupsDesc());
    }
    return "group/groupList";
  }

  @GetMapping(value = "/groups/sort")
  public String sortGroups() {
    if (sort.equals("asc")) {
      sort = "desc";
    } else {
      sort = "asc";
    }
    return "redirect:/groups";
  }

  @GetMapping(value = "/groups/{groupName}/edit")
  public String getEdit(@PathVariable String groupName, Model model) {
    if (!isCurrentUser()) {
      return "redirect:/";
    }
    model.addAttribute("userName", getCurrentUserName());
    model.addAttribute("nameMessage", nameMessage);
    model.addAttribute("title", "edit " + groupName + " group");
    model.addAttribute("group", groupService.getGroupByName(groupName));
    nameMessage = "";
    return "group/editGroupForm";
  }

  @PostMapping(value = "/groups/{groupName}/edit")
//  public String putEdit(@ModelAttribute("groupToPut") String groupToPut, @PathVariable String groupName,
  //  public String putEdit(HttpServletRequest request, @PathVariable String groupName ) {
//    String newNameOfGroup = request.getParameter("newNameOfGroup");
  public String putEdit(@RequestParam String newNameOfGroup, @PathVariable String groupName,
      Model model) {

    try {
      if ((!newNameOfGroup.equalsIgnoreCase("null")) && (newNameOfGroup.trim().length() != 0)) {
        if (newNameOfGroup.trim().length() > 30) {
          nameMessage = "name should be less than 30 characters!";
          model.addAttribute("eMessage", nameMessage);
          return "redirect:/groups/{groupName}/edit";
        }
        Integer id = groupService.getGroupByName(groupName).getId();
        Group group = new Group(newNameOfGroup);
        group.setId(id);
        groupService.updateGroup(group);
      } else {
        nameMessage = "name should not be empty!";
        model.addAttribute("eMessage", nameMessage);
        return "redirect:/groups/{groupName}/edit";
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
    infoMessage = "group was edited successful";
    return "redirect:/groups";
  }


  //  ??????????????????/
  @GetMapping(value = "/groups/{groupId}")
  public String deleteGroup(@PathVariable Integer groupId) {
    if (!isCurrentUser()) {
      return "redirect:/";
    }
    String groupName = groupService.getGroupById(groupId).getName();

    for (Student s : groupService.getGroupById(groupId).getStudents()) {
      studentService.deleteByid(s.getId());
    }
    groupService.deleteById(groupId);
    infoMessage = "group " + groupName + " was deleted";
    return "redirect:/groups";
  }

  @GetMapping(value = "/groups/add")
  public String getAddGroup(Model model) {
    if (!isCurrentUser()) {
      return "redirect:/";
    }
    model.addAttribute("userName", getCurrentUserName());
    model.addAttribute("title", "add new group");
    model.addAttribute("eMessage", nameMessage);
    nameMessage = "";
    return "group/addGroup";
  }

  @PostMapping(value = "/groups/add")
  public String addGroup(@RequestParam String newGroupName) {

    if ((!newGroupName.equalsIgnoreCase("null")) && (newGroupName.trim().length() != 0)) {
      if (newGroupName.trim().length() > 30) {
        nameMessage = "name should be less than 30 characters!";
        return "redirect:/groups/add";
      }
      groupService.postGroup(newGroupName);
    } else {
      nameMessage = "name should not be empty!";
      return "redirect:/groups/add";
    }

    infoMessage = "Group " + newGroupName + " was added";
    return "redirect:/groups";

  }


}
