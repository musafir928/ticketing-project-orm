package com.cydeo.service.impl;

import com.cydeo.dto.ProjectDTO;
import com.cydeo.entity.Project;
import com.cydeo.entity.User;
import com.cydeo.enums.Status;
import com.cydeo.mapper.ProjectMapper;
import com.cydeo.repository.ProjectRepository;
import com.cydeo.repository.TaskRepository;
import com.cydeo.service.ProjectService;
import com.cydeo.service.TaskService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProjectServiceImpl implements ProjectService {
    private final ProjectRepository projectRepository;
    private final ProjectMapper projectMapper;
    private final TaskService taskService;

    public ProjectServiceImpl(ProjectRepository projectRepository, ProjectMapper projectMapper, TaskService taskService) {
        this.projectRepository = projectRepository;
        this.projectMapper = projectMapper;
        this.taskService = taskService;
    }

    @Override
    public ProjectDTO getByProjectCode(String code) {
        return projectMapper.convertToDTO(projectRepository.findByProjectCode(code));
    }

    @Override
    public List<ProjectDTO> listAllProjects() {
        return projectRepository.findAll().stream().map(projectMapper::convertToDTO).collect(Collectors.toList());
    }

    @Override
    public void save(ProjectDTO dto) {
        dto.setProjectStatus(Status.OPEN);
        projectRepository.save(projectMapper.convertToEntity(dto));
    }

    @Override
    public void update(ProjectDTO dto) {
        //Find current project
        Project project = projectRepository.findByProjectCode(dto.getProjectCode());
        //Map updated project dto to entity object
        Project convertedProject = projectMapper.convertToEntity(dto);
        //set id to converted object
        convertedProject.setId(project.getId());
        convertedProject.setProjectStatus(project.getProjectStatus());
        //save updated project
        projectRepository.save(convertedProject);
    }

    @Override
    public void delete(String code) {
        Project project = projectRepository.findByProjectCode(code);
        project.setIsDeleted(true);
        project.setProjectCode(project.getProjectCode() + '_' + project.getId());
        taskService.deleteByProject(code);
        projectRepository.save(project);
    }

    @Override
    public void complete(String code) {
        Project project = projectRepository.findByProjectCode(code);
        project.setProjectStatus(Status.COMPLETE);
        projectRepository.save(project);
    }

    @Override
    public List<ProjectDTO> listAllProjectDetails() {
        // harold@manager.com
        return projectRepository.findAllByAssignedManagerUserName("harold@manager.com")
                .stream()
                .map(project -> {
                    ProjectDTO dto = projectMapper.convertToDTO(project);
                    dto.setUnfinishedTaskCounts(
                            taskService.getCountByProjectAndStatus(
                                    project.getProjectCode(), Status.OPEN
                            ) + taskService.getCountByProjectAndStatus(
                                    project.getProjectCode(), Status.IN_PROGRESS
                            ));
                    dto.setCompleteTaskCounts(
                            taskService.getCountByProjectAndStatus(
                                    project.getProjectCode(), Status.COMPLETE
                            ));
                    return dto;
                })
                .collect(Collectors.toList());
    }
}
