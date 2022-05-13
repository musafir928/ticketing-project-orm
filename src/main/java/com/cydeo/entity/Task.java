package com.cydeo.entity;

import com.cydeo.enums.Status;
import com.fasterxml.jackson.databind.ser.Serializers;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Where;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@NoArgsConstructor
@Getter
@Setter
@Where(clause = "is_deleted=false")
@Table(name = "tasks")
public class Task extends BaseEntity {
    private String taskSubject;
    private String taskDetail;

    @Enumerated(EnumType.STRING)
    private Status taskStatus;

    @Column(columnDefinition = "DATE")
    private LocalDate assignedDate;

    @ManyToOne(fetch = FetchType.LAZY)
    private User assignedEmployee;

    @ManyToOne(fetch = FetchType.LAZY)
    private Project project;

}
