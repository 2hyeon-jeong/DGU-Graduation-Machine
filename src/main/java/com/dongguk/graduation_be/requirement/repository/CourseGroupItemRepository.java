package com.dongguk.graduation_be.requirement.repository;

import com.dongguk.graduation_be.requirement.entity.CourseGroupItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CourseGroupItemRepository extends JpaRepository<CourseGroupItem, Long> {
    boolean existsByCourseGroupIdAndCourseId(Long courseGroupId, Long courseId);
    boolean existsByCourseGroupIdAndCourseIdAndIdNot(Long courseGroupId, Long courseId, Long id);
    Optional<CourseGroupItem> findByCourseGroupIdAndCourseId(Long courseGroupId, Long courseId);
    List<CourseGroupItem> findByCourseGroupIdIn(List<Long> courseGroupIds);
    List<CourseGroupItem> findByCourseGroupIdInAndIsEssentialTrue(List<Long> courseGroupIds);
}
