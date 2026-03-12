package com.dongguk.graduation_be.requirement.dto.response;

import com.dongguk.graduation_be.requirement.entity.CourseGroupItem;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CourseGroupItemResponse {
    private Long id;
    private Long courseGroupId;
    private String courseGroupName;
    private Long courseId;
    private String courseCode;
    private String courseName;
    private Boolean isEssential;

    public static CourseGroupItemResponse from(CourseGroupItem item) {
        return CourseGroupItemResponse.builder()
                .id(item.getId())
                .courseGroupId(item.getCourseGroup().getId())
                .courseGroupName(item.getCourseGroup().getGroupName())
                .courseId(item.getCourse().getId())
                .courseCode(item.getCourse().getCourseCode())
                .courseName(item.getCourse().getCourseName())
                .isEssential(item.getIsEssential())
                .build();
    }
}
