package com.dbserver.desafiovotacao.utils;

import java.util.Random;

public class CpfValidador {
    private static final Random random = new Random();

    /**
     * Valida CPF
     *
     * @param cpf - Cpf a ser validado
     * @return Retorna se o cpf informado está válido
     */
    public static boolean isValido(String cpf) {

        cpf = cpf.replaceAll("[^0-9]", "");

        if (cpf.length() != 11) {
            return false;
        }

        if (cpf.matches("(\\d)\\1{10}")) {
            return false;
        }

        int soma = 0;
        for (int i = 0; i < 9; i++) {
            soma += Character.getNumericValue(cpf.charAt(i)) * (10 - i);
        }
        int primeiroDigitoVerificador = 11 - (soma % 11);
        if (primeiroDigitoVerificador >= 10) {
            primeiroDigitoVerificador = 0;
        }

        if (primeiroDigitoVerificador != Character.getNumericValue(cpf.charAt(9))) {
            return false;
        }

        soma = 0;
        for (int i = 0; i < 10; i++) {
            soma += Character.getNumericValue(cpf.charAt(i)) * (11 - i);
        }
        int segundoDigitoVerificador = 11 - (soma % 11);
        if (segundoDigitoVerificador >= 10) {
            segundoDigitoVerificador = 0;
        }

        return segundoDigitoVerificador == Character.getNumericValue(cpf.charAt(10));
    }

    /**
     * Retorna aleatoriamente se o CPF é válido ou inválido.
     *
     * @param cpf CPF a ser validado
     * @return true se o CPF for considerado válido (aleatoriamente), false caso contrário
     */
    public static boolean fakeValid(String cpf) {
        return resultadoAleatorio();
    }

    /**
     * Simula o resultado aleatório.
     *
     * @return true ou false aleatoriamente
     */
    private static boolean resultadoAleatorio() {
        return random.nextBoolean();
    }
}
