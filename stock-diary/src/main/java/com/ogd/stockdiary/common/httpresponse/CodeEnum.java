package com.ogd.stockdiary.common.httpresponse;

public enum CodeEnum {
  RS_001("실행 성공"),
  FRS_001("실행 실패"),
  FRS_002("권한 없음"),
  FRS_003("데이터 없음"),
  FRS_004("서버 오류"),
  FRS_005("잘못된 요청"),
  ;

  private final String description;

  public String getDescription() {
    return description;
  }

  CodeEnum(String description) {
    this.description = description;
  }
}
