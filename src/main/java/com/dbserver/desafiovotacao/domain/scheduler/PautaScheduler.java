package com.dbserver.desafiovotacao.domain.scheduler;

import com.dbserver.desafiovotacao.domain.service.PautaService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@EnableScheduling
@RequiredArgsConstructor
public class PautaScheduler {

    private final PautaService pautaService;

    @Scheduled(fixedRate = 30000)
    public void encerrarVotacao() {
        pautaService.encerrarVotacao();
    }
}
