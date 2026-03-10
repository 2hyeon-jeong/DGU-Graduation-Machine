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

    @NotNull(message = "entranceYear is required")
    @Column(nullable = false)
    private Integer entranceYear;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "department_id", nullable = false)
    private Department department;

    @NotNull(message = "curriculum is required")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Curriculum curriculum;

    @NotNull(message = "majorType is required")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MajorType majorType;

    @NotNull(message = "minimumCredits is required")
    @Positive
    @Column(nullable = false)
    private Integer minimumCredits;

    @OneToMany(mappedBy = "graduationRequirement", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<AreaRequirement> areaRequirements = new ArrayList<>();

    public void update(
            Integer entranceYear,
            Department department,
            Curriculum curriculum,
            MajorType majorType,
            Integer minimumCredits
    ) {
        this.entranceYear = entranceYear;
        this.department = department;
        this.curriculum = curriculum;
        this.majorType = majorType;
        this.minimumCredits = minimumCredits;
    }

    /* TODO: add optional fields later
       private Integer minEnglishScore;
       private Boolean needsGraduationExam;
       private Boolean needsThesis;
    */
}
