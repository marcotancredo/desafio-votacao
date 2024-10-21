CREATE TABLE public.votos
(
    id           bigserial    NOT NULL,
    dh_voto      timestamp(6) NULL,
    voto         varchar(3)   NULL,
    associado_id int8         NULL,
    pauta_id     int8         NULL,
    CONSTRAINT pk_voto PRIMARY KEY (id),
    CONSTRAINT un_voto UNIQUE (associado_id, pauta_id),
    CONSTRAINT fk_associado_id FOREIGN KEY (associado_id) REFERENCES public.associados (id),
    CONSTRAINT fk_pauta_id FOREIGN KEY (pauta_id) REFERENCES public.pautas (id)
);