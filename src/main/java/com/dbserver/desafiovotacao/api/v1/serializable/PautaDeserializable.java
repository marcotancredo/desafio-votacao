package com.dbserver.desafiovotacao.api.v1.serializable;

import com.dbserver.desafiovotacao.api.v1.model.input.PautaInput;
import com.dbserver.desafiovotacao.domain.model.Pauta;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class PautaDeserializable implements MapperToEntity<PautaInput, Pauta> {
    private final ModelMapper modelMapper;

    public PautaDeserializable(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
    }

    @Override
    public Pauta toEntity(PautaInput pautaInput) {
        return modelMapper.map(pautaInput, Pauta.class);
    }

    @Override
    public List<Pauta> toEntityList(List<PautaInput> pautas) {
        return pautas.stream().map(this::toEntity).toList();
    }
}
