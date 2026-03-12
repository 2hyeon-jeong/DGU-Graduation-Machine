package com.dongguk.graduation_be.requirement.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class UpdateDepartmentRequest {
    private Long id;
    private String newName;
}
