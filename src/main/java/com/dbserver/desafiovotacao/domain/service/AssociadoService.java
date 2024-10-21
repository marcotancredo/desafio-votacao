package com.dbserver.desafiovotacao.domain.service;

import com.dbserver.desafiovotacao.domain.exception.RegistroInvalidoException;
import com.dbserver.desafiovotacao.domain.exception.RegistroNaoEncontradoException;
import com.dbserver.desafiovotacao.domain.model.Associado;
import com.dbserver.desafiovotacao.domain.repository.AssociadoRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.MessageFormat;

@Service
public class AssociadoService {
    private static final Logger logger = LoggerFactory.getLogger(AssociadoService.class);
    public static final String ASSOCIADO_JA_CADASTRADO = "Associado já cadastrado com o CPF: [{0}].";
    private final AssociadoRepository associadoRepository;

    public AssociadoService(AssociadoRepository associadoRepository) {
        this.associadoRepository = associadoRepository;
    }

    public Associado buscarPorId(Long id) throws RegistroNaoEncontradoException {
        return associadoRepository.findById(id).orElseThrow(() -> {
            logger.error("Associado não encontrado com o id [{}]", id);
            return new RegistroNaoEncontradoException(Associado.class, id);
        });
    }

    @Transactional
    public Associado salvar(final Associado associado) throws RegistroInvalidoException {
        associado.setCpf(associado.getCpf().replaceAll("[^0-9]", ""));
        if (associadoRepository.existsByCpf(associado.getCpf())) {
            String msg = MessageFormat.format(ASSOCIADO_JA_CADASTRADO, associado.getCpf());
            logger.error(msg);
            throw new RegistroInvalidoException(msg);
        }
        return associadoRepository.save(associado);
    }

    public Page<Associado> listar(int limit, int offset) {
        PageRequest pageRequest = PageRequest.of(offset, limit);
        return associadoRepository.findAll(pageRequest);
    }
}
