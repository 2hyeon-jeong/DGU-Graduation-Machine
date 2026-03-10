package com.dongguk.graduation_be.requirement.dto.response;

import com.dongguk.graduation_be.requirement.entity.Curriculum;
import com.dongguk.graduation_be.requirement.entity.GraduationRequirement;
import com.dongguk.graduation_be.requirement.entity.MajorType;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class GraduationRequirementResponse {
    private Long id;
    private Integer entranceYear;
    private Long departmentId;
    private String departmentName;
    private Curriculum curriculum;
    private MajorType majorType;
    private Integer minimumCredits;

    public static GraduationRequirementResponse from(GraduationRequirement graduationRequirement) {
        return GraduationRequirementResponse.builder()
                .id(graduationRequirement.getId())
                .entranceYear(graduationRequirement.getEntranceYear())
                .departmentId(graduationRequirement.getDepartment().getId())
                .departmentName(graduationRequirement.getDepartment().getName())
                .curriculum(graduationRequirement.getCurriculum())
                .majorType(graduationRequirement.getMajorType())
                .minimumCredits(graduationRequirement.getMinimumCredits())
                .build();
    }
}
