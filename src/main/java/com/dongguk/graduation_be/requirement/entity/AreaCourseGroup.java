package com.dongguk.graduation_be.requirement.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.*;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "area_course_groups")
@ToString(exclude = {"areaRequirement", "courseGroup"})
public class AreaCourseGroup {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "area_requirement_id", nullable = false)
    private AreaRequirement areaRequirement;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_group_id", nullable = false)
    private CourseGroup courseGroup;

    @NotNull
    @Column(nullable = false)
    private Boolean isEssential;

    @NotNull
    @PositiveOrZero
    @Column(nullable = false)
    private Integer minCount;
}


