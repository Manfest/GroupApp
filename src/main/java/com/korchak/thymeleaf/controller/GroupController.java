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

  private GroupService groupService;
  private StudentService studentService;
  private CurrentUserService currentUserService;


  private static String infoMessage = "";
  private static String nameMessage = "";
  private static String sort = "asc";

  @Autowired

  public GroupController(GroupService groupService, StudentService studentService, CurrentUserService currentUserService) {
    this.groupService = groupService;
    this.currentUserService = currentUserService;
    this.studentService = studentService;
  }





  @GetMapping(value = "/groups")
  public String getAll(Model model) {
    if(currentUserService.getUser() == null){
      return "redirect:/";
    }

    model.addAttribute("title", "groups");
    model.addAttribute("infoMessage", infoMessage);
    infoMessage = "";
    if((currentUserService.getUser().getSurname() != null)) {
      model.addAttribute("userName", (currentUserService.getUser().getName().concat(" ")
          .concat(currentUserService.getUser().getSurname())));
    } else {
      model.addAttribute("userName", currentUserService.getUser().getName());
    }
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
    model.addAttribute("title", "edit");
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
      if ((newNameOfGroup != null) && (newNameOfGroup.length() != 0)) {
        if (newNameOfGroup.length() > 30) {
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
    if(currentUserService.getUser() == null){
      return "redirect:/";
    }
    String groupName = groupService.getGroupById(groupId).getName();

    for(Student s : groupService.getGroupById(groupId).getStudents()){
      studentService.deleteByid(s.getId());
    }
    groupService.deleteById(groupId);

    infoMessage = "group " + groupName + " was deleted";
    return "redirect:/groups";
  }

  @GetMapping(value = "/groups/add")
  public String getAddGroup(Model model) {
    if(currentUserService.getUser() == null){
      return "redirect:/";
    }
    if((currentUserService.getUser().getSurname() != null)) {
      model.addAttribute("userName", (currentUserService.getUser().getName().concat(" ")
          .concat(currentUserService.getUser().getSurname())));
    } else {
      model.addAttribute("userName", currentUserService.getUser().getName());
    }
    model.addAttribute("eMessage", nameMessage);
    model.addAttribute("title", "add group");
    nameMessage = "";
    return "group/addGroup";
  }

  @PostMapping(value = "/groups/add")
  public String addGroup(@RequestParam String newGroupName, Model model) {

    if ((newGroupName != null) && (newGroupName.length() != 0)) {
      if (newGroupName.length() > 30) {
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







//  @PostMapping
//  public List<Group> postOne(@RequestParam final Group group){
//    return groupService.postGroup(group);
//  }
//
//  @GetMapping(value = "/{groupName}")
//  public Group getGroup(@PathVariable String groupName){
//    return groupService.getGroupByName(groupName);
//  }
//
//  @PutMapping(value = "/{groupName}")
//  public List<Group> updateGroup(@RequestBody Group group){
//    return groupService.updateByName(group);
//  }
//
//  @DeleteMapping(value = "/{groupName}")
//  public List<Group> deleteGroup(@PathVariable String groupName){
//    return groupService.deleteGroupByName(groupName);
//  }


}
