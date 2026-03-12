package com.dongguk.graduation_be.requirement.dto.response;

import com.dongguk.graduation_be.requirement.entity.Course;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CourseResponse {
    private Long id;
    private String courseName;
    private String courseCode;

    public static CourseResponse from(Course course) {
        return CourseResponse.builder()
                .id(course.getId())
                .courseName(course.getCourseName())
                .courseCode(course.getCourseCode())
                .build();
    }
}
