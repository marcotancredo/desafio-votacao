package com.dbserver.desafiovotacao.domain.exception;

public class RegistroNaoEncontradoException extends Exception {

    public static final String NAO_FOI_POSSIVEL_LOCALIZAR = "Não foi possível localizar %s com o id: [%s].";

    public RegistroNaoEncontradoException(Class<?> clazz, Long id) {
        super(String.format(NAO_FOI_POSSIVEL_LOCALIZAR, clazz.getSimpleName().toLowerCase(), id));
    }

}
