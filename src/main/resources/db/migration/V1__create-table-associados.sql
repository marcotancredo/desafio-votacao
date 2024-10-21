CREATE TABLE public.associados
(
    id   bigserial    NOT NULL,
    cpf  varchar(11)  NOT NULL,
    nome varchar(255) NOT NULL,
    CONSTRAINT pk_associado PRIMARY KEY (id),
    CONSTRAINT un_cpf UNIQUE (cpf)
);