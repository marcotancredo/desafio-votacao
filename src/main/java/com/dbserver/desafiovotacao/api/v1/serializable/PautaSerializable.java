package com.dbserver.desafiovotacao.api.v1.serializable;

import com.dbserver.desafiovotacao.api.v1.model.dto.PautaDto;
import com.dbserver.desafiovotacao.domain.model.Pauta;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class PautaSerializable implements MapperToDto<Pauta, PautaDto> {
    private final ModelMapper modelMapper;

    public PautaSerializable(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
    }

    @Override
    public PautaDto toDto(Pauta pauta) {
        return modelMapper.map(pauta, PautaDto.class);
    }

    @Override
    public List<PautaDto> toDtoList(List<Pauta> pautas) {
        return pautas.stream().map(this::toDto).toList();
    }
}
