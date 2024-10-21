package com.dbserver.desafiovotacao.api.v1.serializable;

import com.dbserver.desafiovotacao.api.v1.model.dto.AssociadoDto;
import com.dbserver.desafiovotacao.domain.model.Associado;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class AssociadoSerializable implements MapperToDto<Associado, AssociadoDto> {
    private final ModelMapper modelMapper;

    public AssociadoSerializable(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
    }

    @Override
    public AssociadoDto toDto(Associado associado) {
        return modelMapper.map(associado, AssociadoDto.class);
    }

    @Override
    public List<AssociadoDto> toDtoList(List<Associado> associados) {
        return associados.stream().map(this::toDto).toList();
    }
}
