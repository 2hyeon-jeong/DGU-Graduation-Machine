package com.dongguk.graduation_be.requirement.repository;

import com.dongguk.graduation_be.requirement.entity.CourseGroup;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CourseGroupRepository extends JpaRepository<CourseGroup, Long> {
    boolean existsByGroupName(String groupName);
    boolean existsByGroupNameAndIdNot(String groupName, Long id);
    Optional<CourseGroup> findByGroupName(String groupName);
}
