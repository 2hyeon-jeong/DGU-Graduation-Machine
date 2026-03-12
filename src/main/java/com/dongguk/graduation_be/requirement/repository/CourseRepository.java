package com.dongguk.graduation_be.requirement.repository;

import com.dongguk.graduation_be.requirement.entity.Course;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CourseRepository extends JpaRepository<Course, Long> {
    boolean existsByCourseCode(String courseCode);
    boolean existsByCourseCodeAndIdNot(String courseCode, Long id);
    Optional<Course> findByCourseCode(String courseCode);
}
