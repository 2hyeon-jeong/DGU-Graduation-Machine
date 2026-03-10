package com.dongguk.graduation_be.requirement.dto.request;

import com.dongguk.graduation_be.requirement.entity.Curriculum;
import com.dongguk.graduation_be.requirement.entity.MajorType;
import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateGraduationRequirementRequest {
    private Integer entranceYear;
    private Long departmentId;
    private Curriculum curriculum;
    private MajorType majorType;
    private Integer minimumCredits;
}