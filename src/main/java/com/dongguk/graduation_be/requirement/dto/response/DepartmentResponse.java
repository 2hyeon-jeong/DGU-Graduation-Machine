package com.dongguk.graduation_be.requirement.dto.response;

import com.dongguk.graduation_be.requirement.entity.Department;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class DepartmentResponse {
    private Long id;
    private String name;

    public static DepartmentResponse from(Department department) {
        return DepartmentResponse.builder()
                .id(department.getId())
                .name(department.getName())
                .build();
    }
}
