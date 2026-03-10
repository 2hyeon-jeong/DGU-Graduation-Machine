package com.dongguk.graduation_be.requirement.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(
        name = "graduation_requirements",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_graduation_req_keys",
                        columnNames = {"entrance_year", "department_id", "curriculum", "major_type"}
                )
        }
)
@ToString(exclude = "areaRequirements")
public class GraduationRequirement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "입학 연도는 필수입니다.")
    @Column(nullable = false)
    private Integer entranceYear;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "department_id", nullable = false)
    private Department department;

    @NotNull(message = "교육과정은 필수입니다.")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Curriculum curriculum;

    @NotNull(message = "전공 유형은 필수입니다.")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MajorType majorType;

    @NotNull(message = "최소 학점은 필수입니다.")
    @Positive
    @Column(nullable = false)
    private Integer minimumCredits;

    @OneToMany(mappedBy = "graduationRequirement", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<AreaRequirement> areaRequirements = new ArrayList<>();

    /* TODO: 추후 확정 시 추가될 필드들
       private Integer minEnglishScore;   // 영어 성적
       private Boolean needsGraduationExam; // 졸업시험 여부
       private Boolean needsThesis;         // 졸업논문 여부
    */

}
