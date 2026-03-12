package com.dongguk.graduation_be.student.controller;

import com.dongguk.graduation_be.requirement.entity.Curriculum;
import com.dongguk.graduation_be.requirement.entity.MajorType;
import com.dongguk.graduation_be.graduation.dto.GraduationCreditCheckResponse;
import com.dongguk.graduation_be.graduation.service.GraduationCreditCheckService;
import com.dongguk.graduation_be.student.dto.TranscriptParseDebugResponse;
import com.dongguk.graduation_be.student.dto.TranscriptParseResponse;
import com.dongguk.graduation_be.student.service.StudentTranscriptService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/students")
public class StudentController {

    private final StudentTranscriptService studentTranscriptService;
    private final GraduationCreditCheckService graduationCreditCheckService;

    @PostMapping("/transcripts/parse")
    public ResponseEntity<?> parseTranscript(@RequestParam("file") MultipartFile file) {
        try {
            TranscriptParseResponse result = studentTranscriptService.parse(file);
            return ResponseEntity.ok(
                    TranscriptParseDebugResponse.builder()
                            .message("Transcript parsed successfully (debug response)")
                            .data(result)
                            .build()
            );
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error parsing transcript: " + e.getMessage());
        }
    }

    @PostMapping("/graduation/check-minimum-credits")
    public ResponseEntity<?> checkMinimumCredits(
            @RequestParam("file") MultipartFile file,
            @RequestParam("entranceYear") Integer entranceYear,
            @RequestParam("departmentId") Long departmentId,
            @RequestParam("curriculum") Curriculum curriculum,
            @RequestParam("majorType") MajorType majorType
    ) {
        try {
            GraduationCreditCheckResponse result = graduationCreditCheckService.checkMinimumCredits(
                    file,
                    entranceYear,
                    departmentId,
                    curriculum,
                    majorType
            );
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error checking minimum credits: " + e.getMessage());
        }
    }
}
