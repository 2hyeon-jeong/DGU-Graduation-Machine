package com.dongguk.graduation_be.requirement.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "course_groups", uniqueConstraints = {
        @UniqueConstraint(name = "uk_course_group_name", columnNames = {"group_name"})
})
public class CourseGroup {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "과목 그룹 이름은 필수입니다.")
    @Column(nullable = false)
    private String groupName;
}
