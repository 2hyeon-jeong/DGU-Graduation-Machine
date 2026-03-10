package com.dongguk.graduation_be.requirement.entity;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest // 실제 DB(H2)와 연동하여 스키마를 검증함
@DisplayName("졸업 요건 DB 스키마 검증 테스트")
class GraduationSchemaTest {

    @Autowired
    private TestEntityManager em;

    private Department department;
    private GraduationRequirement graduationRequirement;
    private AreaRequirement areaRequirement;
    private CourseGroup basicLiteracyGroup;
    private List<Course> basicLiteracyCourses;

    @BeforeEach
    void setUp() {
        // 1. 학과 저장
        department = Department.builder()
                .name("컴퓨터공학과")
                .build();
        em.persist(department);

        // 2. 졸업 요건 저장 (int -> Integer 자동 박싱 지원되나 명시적으로 작성)
        graduationRequirement = GraduationRequirement.builder()
                .entranceYear(2025)
                .department(department)
                .curriculum(Curriculum.GENERAL)
                .majorType(MajorType.SINGLE_MAJOR)
                .minimumCredits(130)
                .areaRequirements(new ArrayList<>()) // 리스트 초기화 에러 방지
                .build();
        em.persist(graduationRequirement);

        // 3. 영역 요건 저장
        areaRequirement = AreaRequirement.builder()
                .graduationRequirement(graduationRequirement)
                .areaName("교양")
                .minimumCredits(30)
                .groupMappings(new ArrayList<>()) // 리스트 초기화 에러 방지
                .build();
        em.persist(areaRequirement);

        // 4. 과목들 저장
        Course engineeringEthics = Course.builder().courseName("공학 윤리").courseCode("ENG001").build();
        Course engineeringEconomics = Course.builder().courseName("공학 경제").courseCode("ENG002").build();
        Course basicCreativity = Course.builder().courseName("기창특").courseCode("ENG003").build();

        em.persist(engineeringEthics);
        em.persist(engineeringEconomics);
        em.persist(basicCreativity);

        basicLiteracyCourses = Arrays.asList(engineeringEthics, engineeringEconomics, basicCreativity);

        // 5. 과목 그룹 저장
        basicLiteracyGroup = CourseGroup.builder()
                .groupName("기초 소양")
                .build();
        em.persist(basicLiteracyGroup);

        em.flush(); // DB에 즉시 반영
    }

    @Test
    @DisplayName("전체 졸업 요건 스키마 및 제약조건 저장 검증")
    void testCompleteGraduationSchema() {
        // [Given] 영역-그룹 매핑 생성
        AreaCourseGroup mapping = AreaCourseGroup.builder()
                .areaRequirement(areaRequirement)
                .courseGroup(basicLiteracyGroup)
                .isEssential(true)
                .minCount(2)
                .build();
        em.persist(mapping);

        // [When] 영속성 컨텍스트 비우고 다시 조회 (실제 DB에 갔다오는지 확인)
        em.flush();
        em.clear();

        // [Then] 조회 및 검증
        GraduationRequirement found = em.find(GraduationRequirement.class, graduationRequirement.getId());

        assertThat(found.getEntranceYear()).isEqualTo(2025);
        assertThat(found.getDepartment().getName()).isEqualTo("컴퓨터공학과");

        // 영역 확인
        assertThat(found.getAreaRequirements()).hasSize(1);
        AreaRequirement savedArea = found.getAreaRequirements().get(0);
        assertThat(savedArea.getAreaName()).isEqualTo("교양");

        // 매핑 확인 (영역 -> 그룹)
        // AreaRequirement 엔티티에 @OneToMany(mappedBy = "areaRequirement")가 있어야 함
        assertThat(savedArea.getGroupMappings()).hasSize(1);
        AreaCourseGroup savedMapping = savedArea.getGroupMappings().get(0);
        assertThat(savedMapping.getIsEssential()).isTrue();
        assertThat(savedMapping.getMinCount()).isEqualTo(2);
    }
}