package com.dongguk.graduation_be.requirement.service;

import com.dongguk.graduation_be.requirement.dto.request.CreateCourseRequest;
import com.dongguk.graduation_be.requirement.dto.request.UpdateCourseRequest;
import com.dongguk.graduation_be.requirement.dto.response.CourseCsvImportResponse;
import com.dongguk.graduation_be.requirement.dto.response.CourseResponse;
import com.dongguk.graduation_be.requirement.entity.Course;
import com.dongguk.graduation_be.requirement.repository.CourseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CourseService {
    private final CourseRepository courseRepository;

    @Transactional(readOnly = true)
    public List<CourseResponse> getAllCourses() {
        return courseRepository.findAll().stream()
                .map(CourseResponse::from)
                .toList();
    }

    @Transactional
    public Long createCourse(CreateCourseRequest request) {
        if (courseRepository.existsByCourseCode(request.getCourseCode())) {
            throw new IllegalStateException("Course code already exists");
        }

        Course course = Course.builder()
                .courseName(request.getCourseName())
                .courseCode(request.getCourseCode())
                .build();

        return courseRepository.save(course).getId();
    }

    @Transactional
    public void updateCourse(UpdateCourseRequest request) {
        Course course = courseRepository.findById(request.getId())
                .orElseThrow(() -> new IllegalArgumentException("Course not found"));

        if (courseRepository.existsByCourseCodeAndIdNot(request.getCourseCode(), request.getId())) {
            throw new IllegalStateException("Course code already exists");
        }

        course.update(request.getCourseName(), request.getCourseCode());
    }

    @Transactional
    public void deleteCourse(Long id) {
        Course course = courseRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Course not found"));
        courseRepository.delete(course);
    }

    @Transactional
    public CourseCsvImportResponse importCoursesFromCsv(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("CSV file is empty");
        }

        int totalRows = 0;
        int createdRows = 0;
        int updatedRows = 0;
        int skippedRows = 0;
        int errorRows = 0;
        List<String> errors = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(file.getInputStream(), StandardCharsets.UTF_8))) {

            String headerLine = reader.readLine();
            if (headerLine == null) {
                throw new IllegalArgumentException("CSV file has no header");
            }

            String normalizedHeader = normalizeHeader(headerLine);
            if (!"course_code,course_name".equals(normalizedHeader)) {
                throw new IllegalArgumentException("CSV header must be: course_code,course_name");
            }

            String line;
            int rowNumber = 1;
            while ((line = reader.readLine()) != null) {
                rowNumber++;
                if (line.isBlank()) {
                    continue;
                }

                totalRows++;

                String[] columns = line.split(",", 2);
                if (columns.length < 2) {
                    errorRows++;
                    errors.add("Row " + rowNumber + ": invalid column count");
                    continue;
                }

                String courseCode = clean(columns[0]);
                String courseName = clean(columns[1]);

                if (courseCode.isBlank() || courseName.isBlank()) {
                    errorRows++;
                    errors.add("Row " + rowNumber + ": course_code or course_name is blank");
                    continue;
                }

                Course existing = courseRepository.findByCourseCode(courseCode).orElse(null);
                if (existing == null) {
                    Course course = Course.builder()
                            .courseCode(courseCode)
                            .courseName(courseName)
                            .build();
                    courseRepository.save(course);
                    createdRows++;
                    continue;
                }

                if (existing.getCourseName().equals(courseName)) {
                    skippedRows++;
                    continue;
                }

                existing.update(courseName, existing.getCourseCode());
                updatedRows++;
            }
        } catch (IOException e) {
            throw new IllegalStateException("Failed to read CSV file", e);
        }

        return CourseCsvImportResponse.builder()
                .totalRows(totalRows)
                .createdRows(createdRows)
                .updatedRows(updatedRows)
                .skippedRows(skippedRows)
                .errorRows(errorRows)
                .errors(errors)
                .build();
    }

    private String normalizeHeader(String headerLine) {
        return clean(headerLine).toLowerCase();
    }

    private String clean(String value) {
        if (value == null) {
            return "";
        }
        String trimmed = value.trim();
        if (!trimmed.isEmpty() && trimmed.charAt(0) == '\uFEFF') {
            return trimmed.substring(1).trim();
        }
        return trimmed;
    }
}
