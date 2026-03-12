package com.dongguk.graduation_be.student.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TranscriptRowResponse {
    private Integer year;
    private String semester;
    private String completionType;
    private String courseCode;
    private String section;
    private String courseName;
    private Double credits;
    private String grade;
    private String deletedGradeName;
}
