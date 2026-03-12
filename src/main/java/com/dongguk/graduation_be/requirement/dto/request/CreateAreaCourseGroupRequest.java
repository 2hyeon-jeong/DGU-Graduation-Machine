package com.dongguk.graduation_be.requirement.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateAreaCourseGroupRequest {
    private Long areaRequirementId;
    private Long courseGroupId;
    private Boolean isEssential;
    private Integer minCount;
}
