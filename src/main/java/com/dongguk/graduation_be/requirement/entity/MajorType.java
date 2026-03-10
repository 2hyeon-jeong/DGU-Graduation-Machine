package com.dongguk.graduation_be.requirement.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum MajorType {

    // 1. 순수 단일 전공 테크
    SINGLE_MAJOR("단일전공"),

    // 2. 복수 전공 테크
    PRIMARY_DOUBLE_MAJOR("주전공(복수전공 진행)"),
    DOUBLE_MAJOR("복수전공"),

    // 3. 부전공 테크
    PRIMARY_MINOR("주전공(부전공 진행)"),
    MINOR("부전공");

    // todo: 연계전공, 융합전공이 필요하면 추가 (예: LINKED_MAJOR)

    private final String description;
}
