package com.dongguk.graduation_be.graduation.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

import java.util.Map;

@Data
@Builder
public class GraduationCreditCheckResponse {
    @JsonProperty("total_credit")
    private Double totalCredit;

    @JsonProperty("major_credit")
    private Double majorCredit;

    @JsonProperty("liberal_credit")
    private Double liberalCredit;

    @JsonProperty("overall_gpa")
    private Double overallGpa;

    @JsonProperty("major_gpa")
    private Double majorGpa;

    @JsonProperty("liberal_gpa")
    private Double liberalGpa;

    @JsonProperty("passed")
    private Boolean passed;

    @JsonProperty("missed")
    private Map<String, Object> missed;
}
