package com.dongguk.graduation_be.requirement.controller;

import com.dongguk.graduation_be.requirement.dto.request.CreateDepartmentRequest;
import com.dongguk.graduation_be.requirement.dto.request.CreateAreaRequirementRequest;
import com.dongguk.graduation_be.requirement.dto.request.CreateCourseRequest;
import com.dongguk.graduation_be.requirement.dto.request.CreateGraduationRequirementRequest;
import com.dongguk.graduation_be.requirement.dto.response.CourseCsvImportResponse;
import com.dongguk.graduation_be.requirement.dto.request.UpdateDepartmentRequest;
import com.dongguk.graduation_be.requirement.dto.request.UpdateCourseRequest;
import com.dongguk.graduation_be.requirement.dto.request.UpdateGraduationRequirementRequest;
import com.dongguk.graduation_be.requirement.service.AreaRequirementService;
import com.dongguk.graduation_be.requirement.service.CourseService;
import com.dongguk.graduation_be.requirement.service.DepartmentService;
import com.dongguk.graduation_be.requirement.service.GraduationRequirementService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin")
public class AdminRequirementController {
    private final AreaRequirementService areaRequirementService;
    private final CourseService courseService;
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

    @GetMapping("/graduation-requirements")
    public ResponseEntity<?> getAllGraduationRequirements() {
        try {
            return ResponseEntity.ok(graduationRequirementService.getAllGraduationRequirements());
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error fetching graduation requirements: " + e.getMessage());
        }
    }

    @PutMapping("/graduation-requirements")
    public ResponseEntity<String> updateGraduationRequirement(@RequestBody UpdateGraduationRequirementRequest request) {
        try {
            graduationRequirementService.updateGraduationRequirement(request);
            return ResponseEntity.ok("Graduation requirement updated successfully");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error updating graduation requirement: " + e.getMessage());
        }
    }

    @DeleteMapping("/graduation-requirements/{id}")
    public ResponseEntity<String> deleteGraduationRequirement(@PathVariable Long id) {
        try {
            graduationRequirementService.deleteGraduationRequirement(id);
            return ResponseEntity.ok("Graduation requirement deleted successfully");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error deleting graduation requirement: " + e.getMessage());
        }
    }

    @GetMapping("/area-requirements")
    public ResponseEntity<?> getAllAreaRequirements() {
        try {
            return ResponseEntity.ok(areaRequirementService.getAllAreaRequirements());
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error fetching area requirements: " + e.getMessage());
        }
    }

    @PostMapping("/area-requirements")
    public ResponseEntity<String> createAreaRequirement(@RequestBody CreateAreaRequirementRequest request) {
        try {
            Long newId = areaRequirementService.createAreaRequirement(request);
            return ResponseEntity.ok("AreaRequirement created with ID: " + newId);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error creating area requirement: " + e.getMessage());
        }
    }

    @GetMapping("/courses")
    public ResponseEntity<?> getAllCourses() {
        try {
            return ResponseEntity.ok(courseService.getAllCourses());
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error fetching courses: " + e.getMessage());
        }
    }

    @PostMapping("/courses")
    public ResponseEntity<String> createCourse(@RequestBody CreateCourseRequest request) {
        try {
            Long newId = courseService.createCourse(request);
            return ResponseEntity.ok("Course created with ID: " + newId);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error creating course: " + e.getMessage());
        }
    }

    @PutMapping("/courses")
    public ResponseEntity<String> updateCourse(@RequestBody UpdateCourseRequest request) {
        try {
            courseService.updateCourse(request);
            return ResponseEntity.ok("Course updated successfully");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error updating course: " + e.getMessage());
        }
    }

    @DeleteMapping("/courses/{id}")
    public ResponseEntity<String> deleteCourse(@PathVariable Long id) {
        try {
            courseService.deleteCourse(id);
            return ResponseEntity.ok("Course deleted successfully");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error deleting course: " + e.getMessage());
        }
    }

    @PostMapping(value = "/courses/import-csv", consumes = "multipart/form-data")
    public ResponseEntity<?> importCoursesFromCsv(@RequestParam("file") MultipartFile file) {
        try {
            CourseCsvImportResponse response = courseService.importCoursesFromCsv(file);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error importing courses from CSV: " + e.getMessage());
        }
    }

}
