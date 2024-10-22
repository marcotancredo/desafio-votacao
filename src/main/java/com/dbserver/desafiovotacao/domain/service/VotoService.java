package com.dbserver.desafiovotacao.domain.service;

import com.dbserver.desafiovotacao.api.v1.model.input.VotoInput;
import com.dbserver.desafiovotacao.domain.exception.RegistroInvalidoException;
import com.dbserver.desafiovotacao.domain.exception.RegistroNaoEncontradoException;
import com.dbserver.desafiovotacao.domain.model.Associado;
import com.dbserver.desafiovotacao.domain.model.Pauta;
import com.dbserver.desafiovotacao.domain.model.Voto;
import com.dbserver.desafiovotacao.domain.model.enums.SituacaoPauta;
import com.dbserver.desafiovotacao.domain.repository.VotoRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

@Service
@RequiredArgsConstructor
public class VotoService {
    private static final Logger logger = LoggerFactory.getLogger(VotoService.class);

    public static final String SESSAO_NAO_ESTA_ABERTA = "Sessão para esta pauta não está aberta.";
    public static final String SESSAO_ENCERRADA = "Sessão para esta pauta está encerrada.";
    public static final String ASSOCIACAO_NAO_PODE_VOTAR_MESMA_PAUTA = "O associado não pode votar na mesma pauta.";
    private final VotoRepository votoRepository;
    private final PautaService pautaService;
    private final AssociadoService associadoService;

    public Page<Voto> listar(int limit, int offset) {
        PageRequest pageRequest = PageRequest.of(offset, limit);
        return votoRepository.findAll(pageRequest);
    }

    public Voto buscarPorId(Long id) throws RegistroNaoEncontradoException {
        return votoRepository.findById(id).orElseThrow(() -> {
            logger.error("Voto não encontrado com o id [{}]", id);
            return new RegistroNaoEncontradoException(Voto.class, id);
        });
    }

    @Transactional
    public Voto salvar(VotoInput votoInput) throws RegistroNaoEncontradoException, RegistroInvalidoException {
        Pauta pauta = pautaService.buscarPorId(votoInput.getPautaId());
        validarSituacao(pauta);
        Associado associado = associadoService.buscarPorId(votoInput.getAssociadoId());
        validarVotoUnico(associado, pauta);
        Voto voto = new Voto(associado, votoInput.getVoto(), pauta);
        return votoRepository.save(voto);
    }

    private void validarSituacao(Pauta pauta) throws RegistroInvalidoException {
        if (Objects.equals(pauta.getSituacao(), SituacaoPauta.AGUARDANDO_ABERTURA)) {
            logger.error(SESSAO_NAO_ESTA_ABERTA);
            throw new RegistroInvalidoException(SESSAO_NAO_ESTA_ABERTA);
        }

        if (Objects.equals(pauta.getSituacao(), SituacaoPauta.VOTACAO_ENCERRADA)) {
            logger.error(SESSAO_ENCERRADA);
            throw new RegistroInvalidoException(SESSAO_ENCERRADA);
        }
    }

    private void validarVotoUnico(Associado associado, Pauta pauta) throws RegistroInvalidoException {
        if (votoRepository.existsVotoByAssociadoAndPauta(associado, pauta)) {
            logger.error(ASSOCIACAO_NAO_PODE_VOTAR_MESMA_PAUTA);
            throw new RegistroInvalidoException(ASSOCIACAO_NAO_PODE_VOTAR_MESMA_PAUTA);
        }
    }
}
