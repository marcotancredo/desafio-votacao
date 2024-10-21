package com.dbserver.desafiovotacao.domain.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@Entity
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Table(name = "associados")
public class Associado {

    @EqualsAndHashCode.Include
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nome;

    @Column(unique = true, nullable = false)
    private String cpf;

    @Override
    public String toString() {
        return "Associado{" +
                "id=" + id +
                ", nome='" + nome + '\'' +
                ", cpf='" + cpf + '\'' +
                '}';
    }
}
