package com.dongguk.graduation_be.requirement.dto.response;

import com.dongguk.graduation_be.requirement.entity.CourseGroup;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CourseGroupResponse {
    private Long id;
    private String groupName;

    public static CourseGroupResponse from(CourseGroup courseGroup) {
        return CourseGroupResponse.builder()
                .id(courseGroup.getId())
                .groupName(courseGroup.getGroupName())
                .build();
    }
}
