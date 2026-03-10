package com.dongguk.graduation_be.requirement.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "area_requirements")
public class AreaRequirement {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NonNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "graduation_requirement_id", nullable = false)
    private GraduationRequirement graduationRequirement;

    @NotBlank(message = "영역 이름은 필수입니다.")
    @Column(nullable = false)
    private String areaName;

    @NotNull(message = "최소 학점은 필수입니다.")
    @PositiveOrZero(message = "학점은 0 이상이어야 합니다.")
    @Column(nullable = false)
    private Integer minimumCredits;

    @OneToMany(mappedBy = "areaRequirement", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<AreaCourseGroup> groupMappings = new ArrayList<>();

}

