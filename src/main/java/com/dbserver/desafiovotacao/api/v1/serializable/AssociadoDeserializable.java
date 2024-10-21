package com.dbserver.desafiovotacao.api.v1.serializable;

import com.dbserver.desafiovotacao.api.v1.model.input.AssociadoInput;
import com.dbserver.desafiovotacao.domain.model.Associado;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class AssociadoDeserializable implements MapperToEntity<AssociadoInput, Associado> {
    private final ModelMapper modelMapper;

    public AssociadoDeserializable(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
    }

    @Override
    public Associado toEntity(AssociadoInput associadoInput) {
        return modelMapper.map(associadoInput, Associado.class);
    }

    @Override
    public List<Associado> toEntityList(List<AssociadoInput> associados) {
        return associados.stream().map(this::toEntity).toList();
    }
}
