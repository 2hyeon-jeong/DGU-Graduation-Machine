package com.dongguk.graduation_be.requirement.dto.request;

import com.dongguk.graduation_be.requirement.entity.Curriculum;
import com.dongguk.graduation_be.requirement.entity.MajorType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateGraduationRequirementRequest {
    private Long id;
    private Integer entranceYear;
    private Long departmentId;
    private Curriculum curriculum;
    private MajorType majorType;
    private Integer minimumCredits;
}
