package com.dbserver.desafiovotacao.api.v1.serializable;

import java.util.List;

public interface MapperToEntity<I, O> {
    O toEntity(I i);
    List<O> toEntityList(List<I> i);
}
