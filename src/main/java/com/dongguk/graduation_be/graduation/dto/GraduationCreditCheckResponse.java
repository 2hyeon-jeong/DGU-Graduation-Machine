package com.dongguk.graduation_be.graduation.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class GraduationCreditCheckResponse {
    @JsonProperty("total_credit")
    private Double totalCredit;

    @JsonProperty("minimum_credit")
    private Integer minimumCredit;

    @JsonProperty("passed_minimum_credit")
    private Boolean passedMinimumCredit;
}
