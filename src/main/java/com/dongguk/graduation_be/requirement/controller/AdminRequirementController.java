package com.dongguk.graduation_be.requirement.controller;

import com.dongguk.graduation_be.requirement.dto.request.CreateDepartmentRequest;
import com.dongguk.graduation_be.requirement.dto.request.CreateGraduationRequirementRequest;
import com.dongguk.graduation_be.requirement.dto.request.UpdateDepartmentRequest;
import com.dongguk.graduation_be.requirement.service.DepartmentService;
import com.dongguk.graduation_be.requirement.service.GraduationRequirementService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin")
public class AdminRequirementController {
    private final DepartmentService departmentService;
    private final GraduationRequirementService graduationRequirementService;

    @GetMapping("/departments")
    public ResponseEntity<?> getAllDepartments() {
        try {
            return ResponseEntity.ok(departmentService.getAllDepartments());
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error fetching departments: " + e.getMessage());
        }
    }

    @PostMapping("/departments")
    public ResponseEntity<String> createDepartment(@RequestBody CreateDepartmentRequest createDepartmentRequest) {
        try {
            Long newId = departmentService.createDepartment(createDepartmentRequest);
            return ResponseEntity.ok("Department created with ID: " + newId);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error creating department: " + e.getMessage());
        }
    }

    @PutMapping("/departments")
    public ResponseEntity<String> updateDepartment(@RequestBody UpdateDepartmentRequest request) {
        try {
            departmentService.updateDepartment(request);
            return ResponseEntity.ok("Department updated successfully");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error updating department: " + e.getMessage());
        }
    }

    @DeleteMapping("/departments/{id}")
    public ResponseEntity<String> deleteDepartment(@PathVariable Long id) {
        try {
            departmentService.deleteDepartment(id);
            return ResponseEntity.ok("Department deleted successfully");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error deleting department: " + e.getMessage());
        }
    }

    @PostMapping("/graduation-requirements")
    public ResponseEntity<String> createGraduationRequirement(@RequestBody CreateGraduationRequirementRequest createGraduationRequirementRequest) {
        try {
            Long newId = graduationRequirementService
                    .createGraduationRequirement(createGraduationRequirementRequest);
            return ResponseEntity.ok("GraduationRequirement created with ID: " + newId);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error creating graduation requirement: " + e.getMessage());
        }
    }

}
