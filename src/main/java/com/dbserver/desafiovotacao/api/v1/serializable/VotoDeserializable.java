package com.dbserver.desafiovotacao.api.v1.serializable;

import com.dbserver.desafiovotacao.api.v1.model.input.VotoInput;
import com.dbserver.desafiovotacao.domain.model.Voto;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class VotoDeserializable implements MapperToEntity<VotoInput, Voto> {
    private final ModelMapper modelMapper;

    public VotoDeserializable(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
    }

    @Override
    public Voto toEntity(VotoInput votoInput) {
        return modelMapper.map(votoInput, Voto.class);
    }

    @Override
    public List<Voto> toEntityList(List<VotoInput> votos) {
        return votos.stream().map(this::toEntity).toList();
    }
}
