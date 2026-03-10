package com.dongguk.graduation_be.requirement.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Curriculum {
    ADVANCED("심화"), GENERAL("일반");
    // todo: 심화, 일반 외에 다른 교과과정이 필요하면 추가

    private final String description;
}

