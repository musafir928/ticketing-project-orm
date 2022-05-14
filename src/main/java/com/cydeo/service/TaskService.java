package com.cydeo.service;

import com.cydeo.dto.TaskDTO;
import com.cydeo.entity.Task;
import com.cydeo.enums.Status;

import java.util.List;

public interface TaskService {
    TaskDTO findById(Long id);
    List<TaskDTO> listAllTasks();
    void save(TaskDTO dto);
    void update(TaskDTO dto);
    void delete(Long id);
    int getCountByProjectAndStatus(String projectCode, Status status);
    void deleteByProject(String projectCode);

    void completeByProject(String code);

    List<TaskDTO> listAllTasksByStatusIsNot(Status status);
}
