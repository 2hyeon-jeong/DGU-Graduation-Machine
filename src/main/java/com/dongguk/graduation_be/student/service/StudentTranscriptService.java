package com.dongguk.graduation_be.student.service;

import com.dongguk.graduation_be.student.dto.TranscriptParseResponse;
import com.dongguk.graduation_be.student.dto.TranscriptRowErrorResponse;
import com.dongguk.graduation_be.student.dto.TranscriptRowResponse;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;

@Service
public class StudentTranscriptService {

    private static final String YEAR = "년도";
    private static final String SEMESTER = "학기";
    private static final String COURSE_CODE = "학수번호";
    private static final String SECTION = "분반";
    private static final String COURSE_NAME = "교과목명";
    private static final String CREDITS = "학점";
    private static final String GRADE = "등급";
    private static final String DELETED_GRADE_NAME = "성적삭제명";

    private static final List<String> REQUIRED_HEADERS = List.of(
            YEAR, SEMESTER, COURSE_CODE, SECTION, COURSE_NAME, CREDITS, GRADE
    );

    public TranscriptParseResponse parse(MultipartFile file) {
        validateFile(file);

        List<TranscriptRowResponse> parsedRows = new ArrayList<>();
        List<TranscriptRowErrorResponse> errors = new ArrayList<>();

        try (InputStream inputStream = file.getInputStream();
             Workbook workbook = new XSSFWorkbook(inputStream)) {
            Sheet sheet = workbook.getSheetAt(0);
            Row headerRow = sheet.getRow(0);
            if (headerRow == null) {
                throw new IllegalArgumentException("Header row is missing");
            }

            Map<String, Integer> headerIndex = buildHeaderIndex(headerRow);
            validateRequiredHeaders(headerIndex);

            int totalRows = 0;
            for (int rowNum = 1; rowNum <= sheet.getLastRowNum(); rowNum++) {
                Row row = sheet.getRow(rowNum);
                if (row == null || isRowBlank(row, headerIndex)) {
                    continue;
                }

                totalRows++;
                try {
                    parsedRows.add(parseRow(row, headerIndex));
                } catch (IllegalArgumentException e) {
                    errors.add(TranscriptRowErrorResponse.builder()
                            .rowNumber(rowNum + 1)
                            .reason(e.getMessage())
                            .build());
                }
            }

            return TranscriptParseResponse.builder()
                    .totalRows(totalRows)
                    .parsedRows(parsedRows.size())
                    .errorRows(errors.size())
                    .rows(parsedRows)
                    .errors(errors)
                    .build();
        } catch (IOException e) {
            throw new IllegalArgumentException("Failed to read excel file");
        }
    }

    private void validateFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("File is empty");
        }
        String filename = file.getOriginalFilename();
        if (filename == null || !filename.toLowerCase().endsWith(".xlsx")) {
            throw new IllegalArgumentException("Only .xlsx is supported");
        }
    }

    private Map<String, Integer> buildHeaderIndex(Row headerRow) {
        Map<String, Integer> headerIndex = new HashMap<>();
        for (Cell cell : headerRow) {
            String value = normalizeHeader(getStringCellValue(cell));
            if (!value.isBlank()) {
                headerIndex.put(value, cell.getColumnIndex());
            }
        }
        return headerIndex;
    }

    private void validateRequiredHeaders(Map<String, Integer> headerIndex) {
        List<String> missingHeaders = REQUIRED_HEADERS.stream()
                .filter(required -> !headerIndex.containsKey(normalizeHeader(required)))
                .toList();

        if (!missingHeaders.isEmpty()) {
            throw new IllegalArgumentException("Missing required headers: " + String.join(", ", missingHeaders));
        }
    }

    private TranscriptRowResponse parseRow(Row row, Map<String, Integer> headerIndex) {
        String yearValue = getRequiredValue(row, headerIndex, YEAR);
        String semester = getRequiredValue(row, headerIndex, SEMESTER);
        String courseCode = getRequiredValue(row, headerIndex, COURSE_CODE);
        String section = getRequiredValue(row, headerIndex, SECTION);
        String courseName = getRequiredValue(row, headerIndex, COURSE_NAME);
        String creditsValue = getRequiredValue(row, headerIndex, CREDITS);
        String grade = getRequiredValue(row, headerIndex, GRADE);
        String deletedGradeName = getOptionalValue(row, headerIndex, DELETED_GRADE_NAME);
        if (deletedGradeName.isBlank()) {
            deletedGradeName = null;
        }

        return TranscriptRowResponse.builder()
                .year(parseInteger(yearValue, YEAR))
                .semester(semester)
                .courseCode(courseCode)
                .section(section)
                .courseName(courseName)
                .credits(parseDouble(creditsValue, CREDITS))
                .grade(grade)
                .deletedGradeName(deletedGradeName)
                .build();
    }

    private String getRequiredValue(Row row, Map<String, Integer> headerIndex, String headerName) {
        Integer cellIndex = headerIndex.get(normalizeHeader(headerName));
        if (cellIndex == null) {
            throw new IllegalArgumentException("Header not found: " + headerName);
        }

        Cell cell = row.getCell(cellIndex, Row.MissingCellPolicy.RETURN_BLANK_AS_NULL);
        if (cell == null) {
            throw new IllegalArgumentException("Required value is missing: " + headerName);
        }

        String value = getStringCellValue(cell).trim();
        if (value.isBlank()) {
            throw new IllegalArgumentException("Required value is missing: " + headerName);
        }
        return value;
    }

    private String getOptionalValue(Row row, Map<String, Integer> headerIndex, String headerName) {
        Integer cellIndex = headerIndex.get(normalizeHeader(headerName));
        if (cellIndex == null) {
            return "";
        }

        Cell cell = row.getCell(cellIndex, Row.MissingCellPolicy.RETURN_BLANK_AS_NULL);
        if (cell == null) {
            return "";
        }
        return getStringCellValue(cell).trim();
    }

    private String getStringCellValue(Cell cell) {
        return switch (cell.getCellType()) {
            case STRING -> cell.getStringCellValue();
            case NUMERIC -> {
                double value = cell.getNumericCellValue();
                if (value == Math.rint(value)) {
                    yield String.valueOf((long) value);
                }
                yield String.valueOf(value);
            }
            case BOOLEAN -> String.valueOf(cell.getBooleanCellValue());
            case FORMULA -> {
                try {
                    yield cell.getStringCellValue();
                } catch (IllegalStateException e) {
                    yield String.valueOf(cell.getNumericCellValue());
                }
            }
            default -> "";
        };
    }

    private Integer parseInteger(String value, String columnName) {
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid integer in " + columnName + ": " + value);
        }
    }

    private Double parseDouble(String value, String columnName) {
        try {
            return Double.parseDouble(value);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid number in " + columnName + ": " + value);
        }
    }

    private String normalizeHeader(String header) {
        return header.replace("\uFEFF", "").replace(" ", "").trim();
    }

    private boolean isRowBlank(Row row, Map<String, Integer> headerIndex) {
        for (String header : REQUIRED_HEADERS) {
            Integer idx = headerIndex.get(normalizeHeader(header));
            if (idx == null) {
                continue;
            }

            Cell cell = row.getCell(idx, Row.MissingCellPolicy.RETURN_BLANK_AS_NULL);
            if (cell != null && !getStringCellValue(cell).trim().isBlank()) {
                return false;
            }
        }
        return true;
    }
}
