package com.dbserver.desafiovotacao.domain.model;

import com.dbserver.desafiovotacao.domain.model.enums.Situacao;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@Entity
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Table(name = "pautas")
public class Pauta {

    @EqualsAndHashCode.Include
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Long id;

    @Column(nullable = false)
    private String descricao;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Situacao situacao = Situacao.AGUARDANDO_ABERTURA;

    @Column(name = "dh_inicio")
    private LocalDateTime dataHoraInicio;

    @Column(name = "dh_fim")
    private LocalDateTime dataHoraFim;

    @OneToMany(mappedBy = "pauta")
    private List<Voto> votos = new ArrayList<>();

    @Override
    public String toString() {
        return "Pauta{" +
                "id=" + id +
                ", descricao='" + descricao + '\'' +
                ", situacao=" + situacao +
                ", dataHoraInicio=" + dataHoraInicio +
                ", dataHoraFim=" + dataHoraFim +
                '}';
    }
}
