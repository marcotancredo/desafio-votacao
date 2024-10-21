package com.dbserver.desafiovotacao.domain.model;

import com.dbserver.desafiovotacao.domain.model.enums.TipoVoto;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Objects;

@Data
@Entity
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@NoArgsConstructor
@Table(name = "votos")
public class Voto {

    @EqualsAndHashCode.Include
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    private Associado associado;

    @Column(name = "dh_voto", nullable = false)
    private LocalDateTime dataHoraVoto;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private TipoVoto voto;

    @ManyToOne
    private Pauta pauta;

    public Voto(Associado associado, TipoVoto voto, Pauta pauta) {
        this.associado = associado;
        this.voto = voto;
        this.pauta = pauta;
    }

    @PrePersist
    protected void onCreate() {
        this.dataHoraVoto = LocalDateTime.now();
    }

    public boolean getVotoSim() {
        return Objects.equals(voto, TipoVoto.SIM);
    }

    public boolean getVotoNao() {
        return Objects.equals(voto, TipoVoto.NAO);
    }

    @Override
    public String toString() {
        return "Voto{" +
                "id=" + id +
                ", dataHoraVoto=" + dataHoraVoto +
                ", voto=" + voto +
                '}';
    }
}
