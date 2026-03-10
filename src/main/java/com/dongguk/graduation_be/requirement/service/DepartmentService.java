package com.dongguk.graduation_be.requirement.service;

import com.dongguk.graduation_be.requirement.dto.request.CreateDepartmentRequest;
import com.dongguk.graduation_be.requirement.dto.request.UpdateDepartmentRequest;
import com.dongguk.graduation_be.requirement.dto.response.DepartmentResponse;
import com.dongguk.graduation_be.requirement.entity.Department;
import com.dongguk.graduation_be.requirement.repository.DepartmentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DepartmentService {
    private final DepartmentRepository departmentRepository;

    @Transactional(readOnly = true)
    public List<DepartmentResponse> getAllDepartments() {
        return departmentRepository.findAll().stream()
                .map(DepartmentResponse::from)
                .toList();
    }

    public Long createDepartment(CreateDepartmentRequest createDepartmentRequest) {
        Department department = Department.builder()
                .name(createDepartmentRequest.getName())
                .build();
        Department savedDepartment = departmentRepository.save(department);
        return savedDepartment.getId();
    }

    @Transactional
    public void updateDepartment(UpdateDepartmentRequest request) {
        Department department = departmentRepository.findById(request.getId())
                .orElseThrow(() -> new IllegalArgumentException("Department not found"));

        if (departmentRepository.existsByName(request.getNewName())) {
            throw new IllegalStateException("Department name already exists");
        }

        department.setName(request.getNewName());
        departmentRepository.save(department);
    }

    @Transactional
    public void deleteDepartment(Long departmentId) {
        Department department = departmentRepository.findById(departmentId)
                .orElseThrow(() -> new IllegalArgumentException("Department not found"));

        departmentRepository.delete(department);
    }
}
