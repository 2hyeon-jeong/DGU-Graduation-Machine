package com.dongguk.graduation_be.requirement.repository;

import com.dongguk.graduation_be.requirement.entity.Department;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DepartmentRepository extends JpaRepository<Department, Long> {
    boolean existsByName(String name);
}
