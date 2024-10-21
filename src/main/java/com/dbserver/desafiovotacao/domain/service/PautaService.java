package com.dbserver.desafiovotacao.domain.service;

import com.dbserver.desafiovotacao.api.v1.model.ContagemVotos;
import com.dbserver.desafiovotacao.domain.exception.RegistroInvalidoException;
import com.dbserver.desafiovotacao.domain.exception.RegistroNaoEncontradoException;
import com.dbserver.desafiovotacao.domain.model.Pauta;
import com.dbserver.desafiovotacao.domain.model.Voto;
import com.dbserver.desafiovotacao.domain.model.enums.Situacao;
import com.dbserver.desafiovotacao.domain.repository.PautaRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import java.text.MessageFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;

import static org.springframework.data.domain.ExampleMatcher.StringMatcher.CONTAINING;

@Service
@RequiredArgsConstructor
public class PautaService {
    private static final Logger logger = LoggerFactory.getLogger(PautaService.class);

    public static final String DATA_HORA_FIM_INVALIDA = "Data/hora de finalização da sessão não pode ser anterior a data/hora de início.";
    public static final String JA_EXISTE_SESSAO_ABERTA = "Já existe uma sessão com votação aberta para a pauta de id [{0}].";
    public static final String VOTACAO_JA_ENCERRADA = "A sessão de votação para a pauta [{0}], já está encerrada.";
    public static final String INICIANDO_ENCERRAMENTO = "Iniciando encerramento da votação da pauta de id [{}]";
    public static final String VOTACAO_ENCERRADA_COM_SUCESSO = "Votação da pauta encerrada com sucesso.";
    public static final String VOTACAO_ENCERRARA_EM = "Votação da pauta encerrará somente em [{}]";

    private final PautaRepository pautaRepository;

    @Transactional
    public Pauta salvar(final Pauta pauta) {
        return pautaRepository.save(pauta);
    }

    public Pauta buscarPorId(Long id) throws RegistroNaoEncontradoException {
        return pautaRepository.findById(id).orElseThrow(() -> {
            logger.error("Pauta não encontrado com o id [{}]", id);
            return new RegistroNaoEncontradoException(Pauta.class, id);
        });
    }

    public Page<Pauta> listar(Situacao situacao, int limit, int offset) {
        PageRequest pageRequest = PageRequest.of(offset, limit);
        if (Objects.isNull(situacao)) {
            return pautaRepository.findAll(pageRequest);
        }

        Pauta filtro = new Pauta();
        filtro.setSituacao(situacao);
        Example<Pauta> example = Example.of(filtro,
                ExampleMatcher.matching()
                        .withIgnoreCase()
                        .withStringMatcher(CONTAINING));

        return pautaRepository.findAll(example, pageRequest);
    }

    @Transactional
    public Pauta abrirVotacao(Long id, LocalDateTime dataHoraFim)
            throws RegistroNaoEncontradoException, RegistroInvalidoException {
        try {
            Pauta pauta = buscarPorId(id);
            validarSituacao(pauta);
            pauta.setSituacao(Situacao.VOTACAO_ABERTA);
            pauta.setDataHoraInicio(LocalDateTime.now());
            if (Objects.isNull(dataHoraFim)) {
                dataHoraFim = pauta.getDataHoraInicio().plusMinutes(1);
            }
            validarDataHoraFim(pauta, dataHoraFim);
            pauta.setDataHoraFim(dataHoraFim);

            return salvar(pauta);
        } catch (Exception e) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            throw e;
        }
    }

    private void validarDataHoraFim(Pauta pauta, LocalDateTime dataHoraFim) throws RegistroInvalidoException {
        if (Objects.nonNull(dataHoraFim) && dataHoraFim.isBefore(pauta.getDataHoraInicio())) {
            logger.error(DATA_HORA_FIM_INVALIDA);
            throw new RegistroInvalidoException(DATA_HORA_FIM_INVALIDA);
        }
    }

    private void validarSituacao(Pauta pauta) throws RegistroInvalidoException {
        if (Objects.equals(pauta.getSituacao(), Situacao.VOTACAO_ABERTA)) {
            String msg = MessageFormat.format(JA_EXISTE_SESSAO_ABERTA, pauta.getId());
            logger.error(msg);
            throw new RegistroInvalidoException(msg);
        }

        if (Objects.equals(pauta.getSituacao(), Situacao.VOTACAO_ENCERRADA)) {
            String msg = MessageFormat.format(VOTACAO_JA_ENCERRADA, pauta.getId());
            logger.error(msg);
            throw new RegistroInvalidoException(msg);
        }
    }

    public ContagemVotos contagemVotos(Long id) throws RegistroNaoEncontradoException {
        Pauta pauta = buscarPorId(id);
        List<Voto> votos = pauta.getVotos();
        long totalVotosSim = votos.stream().filter(Voto::getVotoSim).count();
        long totalVotosNao = votos.stream().filter(Voto::getVotoNao).count();

        return new ContagemVotos(totalVotosSim, totalVotosNao);
    }

    @Transactional
    public void encerrarVotacao() {
        List<Pauta> pautas = pautaRepository.findBySituacao(Situacao.VOTACAO_ABERTA);
        pautas.forEach(pauta -> {
            logger.info(INICIANDO_ENCERRAMENTO, pauta.getId());
            if (pauta.getDataHoraFim().isBefore(LocalDateTime.now())) {
                pauta.setSituacao(Situacao.VOTACAO_ENCERRADA);
                salvar(pauta);
                logger.info(VOTACAO_ENCERRADA_COM_SUCESSO);
                return;
            }
            logger.info(VOTACAO_ENCERRARA_EM, pauta.getDataHoraFim().format(DateTimeFormatter.ofPattern("dd/MM/yyyy 'às' HH:mm:ss")));
        });
    }
}
