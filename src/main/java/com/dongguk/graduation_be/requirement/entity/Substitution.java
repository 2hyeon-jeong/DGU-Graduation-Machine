package com.dongguk.graduation_be.requirement.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@ToString(exclude = {"targetCourse", "takenCourse"})
@Table(name = "substitution")
    public class Substitution {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 대체하는 과목
    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "target_course_id", nullable = false)
    private Course targetCourse;

    // 대체되는 과목, 이미 들은 과목
    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "taken_course_id", nullable = false)
    private Course takenCourse;

    @NotNull
    @Column(name = "start_year", nullable = false)
    private Integer startYear;

    @NotNull
    @Column(name = "end_year", nullable = false)
    private Integer endYear;
}

