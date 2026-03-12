package com.dongguk.graduation_be.requirement.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateCourseRequest {
    private Long id;
    private String courseName;
    private String courseCode;
}
