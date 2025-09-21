create table stock
(
    id           int auto_increment
        primary key,
    market       varchar(10)                         not null comment '시장구분 (KOSPI, KOSDAQ, NAS, NYSE)',
    code         varchar(20)                         not null comment '종목코드',
    company_name varchar(200)                        not null comment '회사명',
    created_at   timestamp default CURRENT_TIMESTAMP null,
    constraint uk_market_code
        unique (market, code)
)
    comment '주식 종목 마스터' collate = utf8mb4_general_ci;

create index idx_code
    on stock (code);

create index idx_company_name
    on stock (company_name);

create index idx_market
    on stock (market);