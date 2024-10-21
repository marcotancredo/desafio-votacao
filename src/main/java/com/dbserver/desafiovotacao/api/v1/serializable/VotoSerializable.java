package com.dbserver.desafiovotacao.api.v1.serializable;

import com.dbserver.desafiovotacao.api.v1.model.dto.VotoDto;
import com.dbserver.desafiovotacao.domain.model.Voto;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class VotoSerializable implements MapperToDto<Voto, VotoDto> {
    private final ModelMapper modelMapper;

    public VotoSerializable(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
    }

    @Override
    public VotoDto toDto(Voto voto) {
        return modelMapper.map(voto, VotoDto.class);
    }

    @Override
    public List<VotoDto> toDtoList(List<Voto> votos) {
        return votos.stream().map(this::toDto).toList();
    }
}
