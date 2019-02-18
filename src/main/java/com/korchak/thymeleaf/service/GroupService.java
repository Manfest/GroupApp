package com.korchak.thymeleaf.service;

import com.korchak.thymeleaf.model.Group;
import com.korchak.thymeleaf.model.Student;
import com.korchak.thymeleaf.repository.GroupRepository;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.domain.Sort.Order;
import org.springframework.stereotype.Service;

@Service
public class GroupService {

  @Autowired
  private GroupRepository groupRepository;


  public List<Group> getAllGroupsAsc(){
    return groupRepository.findAll(Sort.by(Direction.ASC, "name") );
  }
  public List<Group> getAllGroupsDesc(){
    return groupRepository.findAll(Sort.by(Direction.DESC, "name"));
  }

  public void postGroup(String  newGroupName) {
    Group group = new Group(newGroupName);
    groupRepository.save(group);
  }

  public Group getGroupByName(String name){
    return groupRepository.findByName(name);
  }

  public void updateGroup(Group group){
    groupRepository.save(group);
  }

  public List<Group> getGroupsByStudentName(String name){
    return groupRepository.findGroupsByStudentsName(name);
  }

  public void deleteGroupByName(String groupName) {
    groupRepository.deleteByName(groupName);

  }

  public void deleteById(Integer id){
    groupRepository.deleteById(id);
  }

  public String getGroupNameById(Integer id){
    return groupRepository.findGroupNameById(id);
  }

  public Group getGroupById(Integer id){
    return groupRepository.findGroupById(id);
  }
}
