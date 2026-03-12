package com.dongguk.graduation_be.requirement.dto.response;

import com.dongguk.graduation_be.requirement.entity.AreaCourseGroup;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AreaCourseGroupResponse {
    private Long id;
    private Long areaRequirementId;
    private String areaName;
    private Long courseGroupId;
    private String courseGroupName;
    private Boolean isEssential;
    private Integer minCount;

    public static AreaCourseGroupResponse from(AreaCourseGroup areaCourseGroup) {
        return AreaCourseGroupResponse.builder()
                .id(areaCourseGroup.getId())
                .areaRequirementId(areaCourseGroup.getAreaRequirement().getId())
                .areaName(areaCourseGroup.getAreaRequirement().getAreaName())
                .courseGroupId(areaCourseGroup.getCourseGroup().getId())
                .courseGroupName(areaCourseGroup.getCourseGroup().getGroupName())
                .isEssential(areaCourseGroup.getIsEssential())
                .minCount(areaCourseGroup.getMinCount())
                .build();
    }
}
