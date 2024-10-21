package com.dbserver.desafiovotacao.api.v1.serializable;

import java.util.List;

public interface MapperToDto<I, O> {
    O toDto(I i);
    List<O> toDtoList(List<I> i);
}
