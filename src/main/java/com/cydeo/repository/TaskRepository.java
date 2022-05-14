package com.cydeo.repository;

import com.cydeo.entity.Task;
import com.cydeo.enums.Status;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TaskRepository extends JpaRepository<Task, Long> {
    int countAllByProjectProjectCodeAndTaskStatus(String projectCode,Status status);

    List<Task> findAllByProjectCode(String projectCode);
}
