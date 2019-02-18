package com.korchak.thymeleaf.repository;

import com.korchak.thymeleaf.model.Group;
import com.korchak.thymeleaf.model.Student;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GroupRepository extends JpaRepository<Group, Integer> {
  public Group findByName(String groupName);

  public void deleteByName(String groupName);

  public List<Group> findGroupsByStudentsName(String name);

  public String findGroupNameById(Integer id);

  public Group findGroupById(Integer id);

}
