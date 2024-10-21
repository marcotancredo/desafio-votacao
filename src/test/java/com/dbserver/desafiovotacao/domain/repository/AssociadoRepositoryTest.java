package com.dbserver.desafiovotacao.domain.repository;

import com.dbserver.desafiovotacao.domain.model.Associado;
import jakarta.persistence.EntityManager;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

@DataJpaTest
@ActiveProfiles("test")
public class AssociadoRepositoryTest {

    @Autowired
    private EntityManager entityManager;

    @Autowired
    private AssociadoRepository associadoRepository;

    @BeforeEach
    void setUp() {
        Associado associado = new Associado();
        associado.setNome("João");
        associado.setCpf("56515689055");

        entityManager.persist(associado);
    }

    @Test
    @DisplayName("Deve retornar se existe associado com cpf informado")
    public void existsByCpfCase1() {
        String cpf = "56515689055";
        boolean exists = this.associadoRepository.existsByCpf(cpf);
        Assertions.assertThat(exists).isTrue();
    }

    @Test
    @DisplayName("Deve retornar se não existe associado com cpf informado")
    public void existsByCpfCase2() {
        String cpf = "87981175054";
        boolean exists = this.associadoRepository.existsByCpf(cpf);
        Assertions.assertThat(exists).isFalse();
    }
}
