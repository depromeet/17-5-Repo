package com.ogd.stockdiary.domain.report.port.out;

import java.util.List;

import com.ogd.stockdiary.domain.report.vo.PrincipleCheckData;

/**
 * 투자원칙 체크 데이터 조회 Port
 * Report 도메인이 다른 도메인에 의존하지 않도록 추상화
 */
public interface PrincipleCheckPort {

    /**
     * 특정 회고에 대한 모든 투자원칙 체크 데이터 조회
     *
     * @param retrospectionId
     *            회고 ID
     * @return 투자원칙 체크 데이터 목록
     */
    List<PrincipleCheckData> findByRetrospectionId(Long retrospectionId);
}
