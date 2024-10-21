package com.dbserver.desafiovotacao.domain.repository;

import com.dbserver.desafiovotacao.domain.model.Associado;
import com.dbserver.desafiovotacao.domain.model.Pauta;
import com.dbserver.desafiovotacao.domain.model.Voto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VotoRepository extends JpaRepository<Voto, Long> {
    boolean existsVotoByAssociadoAndPauta(Associado associado, Pauta pauta);
}
