package com.dongguk.graduation_be.requirement.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Entity
@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "prerequisites")
@ToString(exclude = "department")
public class Prerequisite {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "department_id", nullable = false)
    private Department department;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "prerequisite_course_group_id", nullable = false)
    private CourseGroup prerequisiteGroup;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "subsequent_course_group_id", nullable = false)
    private CourseGroup subsequentGroup;

    @NotNull
    @Column(name = "start_year", nullable = false)
    private Integer startYear;

    @NotNull
    @Column(name = "end_year", nullable = false)
    private Integer endYear;
}