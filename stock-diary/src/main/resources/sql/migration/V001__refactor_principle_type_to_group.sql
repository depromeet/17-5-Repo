-- 투자원칙 BUY/SELL 구분을 투자그룹으로 이동하는 마이그레이션
-- 작성일: 2025-10-28
-- 설명:
--   - principle_groups 테이블에 principle_type 컬럼 추가
--   - investment_principles 테이블에서 principle_type 컬럼 제거
--   - investment_principles 테이블의 group_id를 NOT NULL로 변경

-- Step 1: principle_groups 테이블에 principle_type 컬럼 추가
ALTER TABLE principle_groups
ADD COLUMN principle_type VARCHAR(10) NOT NULL DEFAULT 'BUY';

-- Step 2: investment_principles 테이블에서 principle_type 컬럼 제거
-- 주의: 데이터 마이그레이션은 수동으로 처리해야 합니다.
-- 각 그룹에 속한 원칙들의 principle_type을 그룹의 principle_type으로 이동시킨 후 실행하세요.
ALTER TABLE investment_principles
DROP COLUMN principle_type;

-- Step 3: investment_principles 테이블의 group_id를 NOT NULL로 변경
-- 주의: 이 명령을 실행하기 전에 모든 원칙이 그룹에 속해있는지 확인하세요.
ALTER TABLE investment_principles
MODIFY COLUMN group_id BIGINT NOT NULL;

-- 참고사항:
-- 1. 이 스크립트를 실행하기 전에 반드시 데이터 백업을 수행하세요.
-- 2. 데이터 마이그레이션 절차:
--    a. 각 그룹에 속한 원칙들의 principle_type을 확인
--    b. 그룹이 없는 원칙들은 적절한 그룹에 할당하거나 새 그룹 생성
--    c. 같은 그룹 내 원칙들의 principle_type이 다를 경우 충돌 해결 필요
--    d. 각 그룹의 principle_type 값을 설정
-- 3. ALTER TABLE 명령 실행 전에 데이터 정합성을 확인하세요.
