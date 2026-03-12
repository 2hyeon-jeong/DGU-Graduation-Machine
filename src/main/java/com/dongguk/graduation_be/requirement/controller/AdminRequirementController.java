package com.dongguk.graduation_be.requirement.controller;

import com.dongguk.graduation_be.requirement.dto.request.CreateDepartmentRequest;
import com.dongguk.graduation_be.requirement.dto.request.CreateAreaRequirementRequest;
import com.dongguk.graduation_be.requirement.dto.request.CreateAreaCourseGroupRequest;
import com.dongguk.graduation_be.requirement.dto.request.CreateCourseRequest;
import com.dongguk.graduation_be.requirement.dto.request.CreateCourseGroupRequest;
import com.dongguk.graduation_be.requirement.dto.request.CreateCourseGroupItemRequest;
import com.dongguk.graduation_be.requirement.dto.request.CreateGraduationRequirementRequest;
import com.dongguk.graduation_be.requirement.dto.response.CourseCsvImportResponse;
import com.dongguk.graduation_be.requirement.dto.response.CourseGroupItemCsvImportResponse;
import com.dongguk.graduation_be.requirement.dto.request.UpdateDepartmentRequest;
import com.dongguk.graduation_be.requirement.dto.request.UpdateAreaCourseGroupRequest;
import com.dongguk.graduation_be.requirement.dto.request.UpdateCourseRequest;
import com.dongguk.graduation_be.requirement.dto.request.UpdateCourseGroupRequest;
import com.dongguk.graduation_be.requirement.dto.request.UpdateCourseGroupItemRequest;
import com.dongguk.graduation_be.requirement.dto.request.UpdateGraduationRequirementRequest;
import com.dongguk.graduation_be.requirement.service.AreaCourseGroupService;
import com.dongguk.graduation_be.requirement.service.AreaRequirementService;
import com.dongguk.graduation_be.requirement.service.CourseService;
import com.dongguk.graduation_be.requirement.service.CourseGroupService;
import com.dongguk.graduation_be.requirement.service.CourseGroupItemService;
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
    private final AreaCourseGroupService areaCourseGroupService;
    private final AreaRequirementService areaRequirementService;
    private final CourseService courseService;
    private final CourseGroupService courseGroupService;
    private final CourseGroupItemService courseGroupItemService;
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

    @GetMapping("/area-course-groups")
    public ResponseEntity<?> getAllAreaCourseGroups() {
        try {
            return ResponseEntity.ok(areaCourseGroupService.getAllAreaCourseGroups());
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error fetching area-course-group mappings: " + e.getMessage());
        }
    }

    @PostMapping("/area-course-groups")
    public ResponseEntity<String> createAreaCourseGroup(@RequestBody CreateAreaCourseGroupRequest request) {
        try {
            Long newId = areaCourseGroupService.createAreaCourseGroup(request);
            return ResponseEntity.ok("AreaCourseGroup created with ID: " + newId);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error creating area-course-group mapping: " + e.getMessage());
        }
    }

    @PutMapping("/area-course-groups")
    public ResponseEntity<String> updateAreaCourseGroup(@RequestBody UpdateAreaCourseGroupRequest request) {
        try {
            areaCourseGroupService.updateAreaCourseGroup(request);
            return ResponseEntity.ok("AreaCourseGroup updated successfully");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error updating area-course-group mapping: " + e.getMessage());
        }
    }

    @DeleteMapping("/area-course-groups/{id}")
    public ResponseEntity<String> deleteAreaCourseGroup(@PathVariable Long id) {
        try {
            areaCourseGroupService.deleteAreaCourseGroup(id);
            return ResponseEntity.ok("AreaCourseGroup deleted successfully");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error deleting area-course-group mapping: " + e.getMessage());
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

    @GetMapping("/course-groups")
    public ResponseEntity<?> getAllCourseGroups() {
        try {
            return ResponseEntity.ok(courseGroupService.getAllCourseGroups());
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error fetching course groups: " + e.getMessage());
        }
    }

    @PostMapping("/course-groups")
    public ResponseEntity<String> createCourseGroup(@RequestBody CreateCourseGroupRequest request) {
        try {
            Long newId = courseGroupService.createCourseGroup(request);
            return ResponseEntity.ok("CourseGroup created with ID: " + newId);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error creating course group: " + e.getMessage());
        }
    }

    @PutMapping("/course-groups")
    public ResponseEntity<String> updateCourseGroup(@RequestBody UpdateCourseGroupRequest request) {
        try {
            courseGroupService.updateCourseGroup(request);
            return ResponseEntity.ok("CourseGroup updated successfully");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error updating course group: " + e.getMessage());
        }
    }

    @DeleteMapping("/course-groups/{id}")
    public ResponseEntity<String> deleteCourseGroup(@PathVariable Long id) {
        try {
            courseGroupService.deleteCourseGroup(id);
            return ResponseEntity.ok("CourseGroup deleted successfully");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error deleting course group: " + e.getMessage());
        }
    }

    @GetMapping("/course-group-items")
    public ResponseEntity<?> getAllCourseGroupItems() {
        try {
            return ResponseEntity.ok(courseGroupItemService.getAllCourseGroupItems());
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error fetching course group items: " + e.getMessage());
        }
    }

    @PostMapping("/course-group-items")
    public ResponseEntity<String> createCourseGroupItem(@RequestBody CreateCourseGroupItemRequest request) {
        try {
            Long newId = courseGroupItemService.createCourseGroupItem(request);
            return ResponseEntity.ok("CourseGroupItem created with ID: " + newId);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error creating course group item: " + e.getMessage());
        }
    }

    @PutMapping("/course-group-items")
    public ResponseEntity<String> updateCourseGroupItem(@RequestBody UpdateCourseGroupItemRequest request) {
        try {
            courseGroupItemService.updateCourseGroupItem(request);
            return ResponseEntity.ok("CourseGroupItem updated successfully");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error updating course group item: " + e.getMessage());
        }
    }

    @DeleteMapping("/course-group-items/{id}")
    public ResponseEntity<String> deleteCourseGroupItem(@PathVariable Long id) {
        try {
            courseGroupItemService.deleteCourseGroupItem(id);
            return ResponseEntity.ok("CourseGroupItem deleted successfully");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error deleting course group item: " + e.getMessage());
        }
    }

    @PostMapping(value = "/course-group-items/import-csv", consumes = "multipart/form-data")
    public ResponseEntity<?> importCourseGroupItemsFromCsv(@RequestParam("file") MultipartFile file) {
        try {
            CourseGroupItemCsvImportResponse response = courseGroupItemService.importFromCsv(file);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error importing course group items from CSV: " + e.getMessage());
        }
    }

}
