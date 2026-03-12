package com.dongguk.graduation_be.requirement.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateCourseGroupItemRequest {
    private Long id;
    private Long courseGroupId;
    private Long courseId;
    private Boolean isEssential;
}
