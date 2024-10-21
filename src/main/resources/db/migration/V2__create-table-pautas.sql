CREATE TABLE public.pautas
(
    id        bigserial    NOT NULL,
    descricao varchar(255) NOT NULL,
    situacao  varchar(30)  NOT NULL,
    dh_inicio timestamp(6) NULL,
    dh_fim    timestamp(6) NULL,
    CONSTRAINT pk_pauta PRIMARY KEY (id)
);