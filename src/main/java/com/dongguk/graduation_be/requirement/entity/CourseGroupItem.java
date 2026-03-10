package com.dongguk.graduation_be.requirement.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor  // Builder용
@ToString(exclude = {"courseGroup", "course"})
@Table(
        name = "course_code_group",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_course_group_code",
                        columnNames = {"course_group_id", "course_code_id"}
                )
        }
)
public class CourseGroupItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_group_id", nullable = false)
    private CourseGroup courseGroup;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_code_id", nullable = false)
    private Course course;
}
