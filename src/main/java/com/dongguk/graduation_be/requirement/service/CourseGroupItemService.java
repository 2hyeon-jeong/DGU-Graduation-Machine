package com.dongguk.graduation_be.requirement.service;

import com.dongguk.graduation_be.requirement.dto.request.CreateCourseGroupItemRequest;
import com.dongguk.graduation_be.requirement.dto.request.UpdateCourseGroupItemRequest;
import com.dongguk.graduation_be.requirement.dto.response.CourseGroupItemCsvImportResponse;
import com.dongguk.graduation_be.requirement.dto.response.CourseGroupItemResponse;
import com.dongguk.graduation_be.requirement.entity.Course;
import com.dongguk.graduation_be.requirement.entity.CourseGroup;
import com.dongguk.graduation_be.requirement.entity.CourseGroupItem;
import com.dongguk.graduation_be.requirement.repository.CourseGroupItemRepository;
import com.dongguk.graduation_be.requirement.repository.CourseGroupRepository;
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
public class CourseGroupItemService {
    private final CourseGroupItemRepository courseGroupItemRepository;
    private final CourseGroupRepository courseGroupRepository;
    private final CourseRepository courseRepository;

    @Transactional(readOnly = true)
    public List<CourseGroupItemResponse> getAllCourseGroupItems() {
        return courseGroupItemRepository.findAll().stream()
                .map(CourseGroupItemResponse::from)
                .toList();
    }

    @Transactional
    public Long createCourseGroupItem(CreateCourseGroupItemRequest request) {
        CourseGroup courseGroup = courseGroupRepository.findById(request.getCourseGroupId())
                .orElseThrow(() -> new IllegalArgumentException("CourseGroup not found"));
        Course course = courseRepository.findById(request.getCourseId())
                .orElseThrow(() -> new IllegalArgumentException("Course not found"));

        if (courseGroupItemRepository.existsByCourseGroupIdAndCourseId(request.getCourseGroupId(), request.getCourseId())) {
            throw new IllegalStateException("CourseGroupItem mapping already exists");
        }

        CourseGroupItem item = CourseGroupItem.builder()
                .courseGroup(courseGroup)
                .course(course)
                .isEssential(request.getIsEssential())
                .build();

        return courseGroupItemRepository.save(item).getId();
    }

    @Transactional
    public void updateCourseGroupItem(UpdateCourseGroupItemRequest request) {
        CourseGroupItem item = courseGroupItemRepository.findById(request.getId())
                .orElseThrow(() -> new IllegalArgumentException("CourseGroupItem not found"));
        CourseGroup courseGroup = courseGroupRepository.findById(request.getCourseGroupId())
                .orElseThrow(() -> new IllegalArgumentException("CourseGroup not found"));
        Course course = courseRepository.findById(request.getCourseId())
                .orElseThrow(() -> new IllegalArgumentException("Course not found"));

        if (courseGroupItemRepository.existsByCourseGroupIdAndCourseIdAndIdNot(
                request.getCourseGroupId(), request.getCourseId(), request.getId())) {
            throw new IllegalStateException("CourseGroupItem mapping already exists");
        }

        item.update(courseGroup, course, request.getIsEssential());
    }

    @Transactional
    public void deleteCourseGroupItem(Long id) {
        CourseGroupItem item = courseGroupItemRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("CourseGroupItem not found"));
        courseGroupItemRepository.delete(item);
    }

    @Transactional
    public CourseGroupItemCsvImportResponse importFromCsv(MultipartFile file) {
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

            if (!"group_name,course_code,is_essential".equals(normalizeHeader(headerLine))) {
                throw new IllegalArgumentException("CSV header must be: group_name,course_code,is_essential");
            }

            String line;
            int rowNumber = 1;
            while ((line = reader.readLine()) != null) {
                rowNumber++;
                if (line.isBlank()) {
                    continue;
                }

                totalRows++;
                String[] cols = line.split(",", 3);
                if (cols.length < 3) {
                    errorRows++;
                    errors.add("Row " + rowNumber + ": invalid column count");
                    continue;
                }

                String groupName = clean(cols[0]);
                String courseCode = clean(cols[1]).toUpperCase();
                String essentialRaw = clean(cols[2]).toLowerCase();
                Boolean isEssential = parseBoolean(essentialRaw);
                if (isEssential == null) {
                    errorRows++;
                    errors.add("Row " + rowNumber + ": is_essential must be true/false");
                    continue;
                }

                CourseGroup courseGroup = courseGroupRepository.findByGroupName(groupName).orElse(null);
                if (courseGroup == null) {
                    errorRows++;
                    errors.add("Row " + rowNumber + ": CourseGroup not found -> " + groupName);
                    continue;
                }

                Course course = courseRepository.findByCourseCode(courseCode).orElse(null);
                if (course == null) {
                    errorRows++;
                    errors.add("Row " + rowNumber + ": Course not found -> " + courseCode);
                    continue;
                }

                CourseGroupItem existing = courseGroupItemRepository
                        .findByCourseGroupIdAndCourseId(courseGroup.getId(), course.getId())
                        .orElse(null);

                if (existing == null) {
                    CourseGroupItem item = CourseGroupItem.builder()
                            .courseGroup(courseGroup)
                            .course(course)
                            .isEssential(isEssential)
                            .build();
                    courseGroupItemRepository.save(item);
                    createdRows++;
                    continue;
                }

                if (existing.getIsEssential().equals(isEssential)) {
                    skippedRows++;
                    continue;
                }

                existing.update(existing.getCourseGroup(), existing.getCourse(), isEssential);
                updatedRows++;
            }
        } catch (IOException e) {
            throw new IllegalStateException("Failed to read CSV file", e);
        }

        return CourseGroupItemCsvImportResponse.builder()
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

    private Boolean parseBoolean(String value) {
        if ("true".equals(value)) {
            return true;
        }
        if ("false".equals(value)) {
            return false;
        }
        return null;
    }
}
