package com.cydeo.service.impl;

import com.cydeo.dto.TaskDTO;
import com.cydeo.dto.UserDTO;
import com.cydeo.entity.Task;
import com.cydeo.entity.User;
import com.cydeo.enums.Status;
import com.cydeo.mapper.TaskMapper;
import com.cydeo.repository.TaskRepository;
import com.cydeo.repository.UserRepository;
import com.cydeo.service.TaskService;
import com.cydeo.service.UserService;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class TaskServiceImpl implements TaskService {

    private final TaskRepository taskRepository;
    private final TaskMapper taskMapper;
    private final UserRepository userRepository;

    public TaskServiceImpl(TaskRepository taskRepository, TaskMapper taskMapper, UserRepository userRepository) {
        this.taskRepository = taskRepository;
        this.taskMapper = taskMapper;
        this.userRepository = userRepository;
    }

    @Override
    public TaskDTO findById(Long id) {
        Optional<Task> taskFound = taskRepository.findById(id);
        if (taskFound.isPresent()) {
            return taskMapper.convertToDTO(taskFound.get());
        }
        return null;
    }

    @Override
    public List<TaskDTO> listAllTasks() {
        return taskRepository.findAll()
                .stream()
                .map(taskMapper::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public void save(TaskDTO dto) {
        dto.setTaskStatus(Status.OPEN);
        dto.setAssignedDate(LocalDate.now());
        taskRepository.save(taskMapper.convertToEntity(dto));
    }

    @Override
    public void update(TaskDTO dto) {
        Optional<Task> taskFound = taskRepository.findById(dto.getId());
        Task convertedTask = taskMapper.convertToEntity(dto);

        if(taskFound.isPresent()) {
            convertedTask.setId(taskFound.get().getId());
            convertedTask.setTaskStatus(dto.getTaskStatus() == null ? taskFound.get().getTaskStatus() : dto.getTaskStatus());
            convertedTask.setAssignedDate(taskFound.get().getAssignedDate());
            taskRepository.save(convertedTask);
        }
    }

    @Override
    public void delete(Long id) {
        Optional<Task> taskFound = taskRepository.findById(id);
        if (taskFound.isPresent()) {
            taskFound.get().setIsDeleted(true);
            taskRepository.save(taskFound.get());
        }
    }

    @Override
    public int getCountByProjectAndStatus(String projectCode, Status status){
       return taskRepository.countAllByProjectProjectCodeAndTaskStatus(projectCode,status);
    }

    @Override
    public void deleteByProject(String projectCode) {
        taskRepository.findAllByProjectProjectCode(projectCode).forEach(task->{
            task.setIsDeleted(true);
            taskRepository.save(task);
        });
    }

    @Override
    public void completeByProject(String code) {
        taskRepository.findAllByProjectProjectCode(code).forEach(task->{
            task.setIsDeleted(true);
            task.setTaskStatus(Status.COMPLETE);
            taskRepository.save(task);
        });
    }

    @Override
    public List<TaskDTO> listAllTasksByStatusIsNot(Status status) {
        User loggedInUser = userRepository.findByUserName("john@employee.com");
        List<Task> list = taskRepository.findAllByTaskStatusIsNotAndAssignedEmployee(status, loggedInUser);
        return list.stream().map(taskMapper::convertToDTO).collect(Collectors.toList());
    }

    @Override
    public List<TaskDTO> listAllTasksByStatus(Status status) {
        User loggedInUser = userRepository.findByUserName("john@employee.com");
        List<Task> list = taskRepository.findAllByTaskStatusAndAssignedEmployee(status, loggedInUser);
        return list.stream().map(taskMapper::convertToDTO).collect(Collectors.toList());
    }

    @Override
    public void updateStatus(TaskDTO dto) {
        Optional<Task> task  = taskRepository.findById(dto.getId());

        if(task.isPresent()) {
            task.get().setTaskStatus(dto.getTaskStatus());
            taskRepository.save(task.get());
        }

    }
}
