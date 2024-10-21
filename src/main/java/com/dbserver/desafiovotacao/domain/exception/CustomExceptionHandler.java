package com.dbserver.desafiovotacao.domain.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class CustomExceptionHandler {

    @ExceptionHandler(RegistroNaoEncontradoException.class)
    public ResponseEntity<Map<String, String>> handleRegistroNaoEncontrado(RegistroNaoEncontradoException ex) {
        return getResponseEntity(ex.getMessage(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(RegistroInvalidoException.class)
    public ResponseEntity<Map<String, String>> handleRegistroNaoEncontrado(RegistroInvalidoException ex) {
        return getResponseEntity(ex.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, String>> handleGenericException(Exception ex) {
        return getResponseEntity("Ocorreu um erro interno no servidor. Tente novamente mais tarde.", HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<Map<String, String>> handleEnumValidationException(MethodArgumentTypeMismatchException ex) {
        Map<String, String> response = new HashMap<>();

        if (ex.getRequiredType() != null && ex.getRequiredType().isEnum()) {
            String valoresValidos = Arrays.toString(ex.getRequiredType().getEnumConstants());
            response.put("message", "Valor inválido para o parâmetro: " + ex.getName());
            response.put("valoresValidos", valoresValidos);
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }

        return getResponseEntity("Erro de parâmetro: " + ex.getMessage(), HttpStatus.BAD_REQUEST);
    }

    private static ResponseEntity<Map<String, String>> getResponseEntity(String ex, HttpStatus badRequest) {
        Map<String, String> response = new HashMap<>();
        response.put("message", ex);
        return new ResponseEntity<>(response, badRequest);
    }
}
