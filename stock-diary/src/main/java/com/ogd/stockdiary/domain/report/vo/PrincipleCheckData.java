package com.ogd.stockdiary.domain.report.vo;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Report 도메인에서 사용하는 투자원칙 체크 데이터
 * AI에 전달할 정보만 포함 (읽기 전용 VO)
 */
public record PrincipleCheckData(
    String principleName, // 투자원칙 내용
    String status, // 체크 상태 (CHECKED, NOT_CHECKED, VIOLATED)
    String reason, // 체크 이유/설명
    List<String> imageUrls, // 관련 이미지 다운로드 URL 목록
    List<String> links // 관련 링크 목록
) {

    /**
     * 빈 데이터 생성 헬퍼
     */
    public static PrincipleCheckData empty() {
        return new PrincipleCheckData("", "", "", List.of(), List.of());
    }

    /**
     * AI 프롬프트용 포맷팅
     */
    public String toPromptFormat() {
        StringBuilder sb = new StringBuilder();
        sb.append("- 원칙: ").append(principleName).append("\n");
        sb.append("  준수 여부: ").append(status).append("\n");

        if (reason != null && !reason.isBlank()) {
            sb.append("  사유: ").append(reason).append("\n");
        }

        if (!imageUrls.isEmpty()) {
            sb.append("  첨부 이미지:\n");
            imageUrls.forEach(url -> sb.append("    - ").append(url).append("\n"));
        }

        if (!links.isEmpty()) {
            sb.append("  참고 링크:\n");
            links.forEach(link -> sb.append("    - ").append(link).append("\n"));
        }

        return sb.toString();
    }

    /**
     * 여러 체크 데이터를 프롬프트 형식으로 변환
     */
    public static String toPromptFormat(List<PrincipleCheckData> checks) {
        if (checks == null || checks.isEmpty()) {
            return "투자원칙 체크 데이터 없음";
        }

        return checks.stream()
            .map(PrincipleCheckData::toPromptFormat)
            .collect(Collectors.joining("\n"));
    }
}
