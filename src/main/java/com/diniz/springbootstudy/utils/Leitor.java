package com.diniz.springbootstudy.utils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;
import java.util.Scanner;

/**
 * Biblioteca Utilitária para leitura e validação estrita de dados via Console (Scanner).
 * Desenvolvida para evitar quebras de buffer e tratar entradas inválidas elegantemente.
 */
public final class Leitor {

    // Impede a criação de objetos desta classe.
    // Como todos os métodos são estáticos, ela funciona apenas como biblioteca.
    private Leitor() {
    }

    // Quantidade máxima de tentativas permitidas para cada leitura.
    private static final int MAX_TENTATIVAS = 2;

    // Mensagens padrões utilizadas na biblioteca.
    private static final String MSG_INTEIRO = "Valor inválido! Digite um número inteiro.";
    private static final String MSG_LONG = "Valor inválido! Digite um número inteiro longo válido.";
    private static final String MSG_DOUBLE = "Valor inválido! Digite um número decimal (ex: 10.5).";
    private static final String MSG_TEXTO = "Valor inválido! O campo não pode ficar em branco.";
    private static final String MSG_BOOLEAN = "Resposta inválida! Digite apenas 'S' para Sim ou 'N' para Não.";

    // FORMATADOR DE DATA ESTRITO
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter
            .ofPattern("dd/MM/uuuu")
            .withResolverStyle(ResolverStyle.STRICT);


    // FORMATADOR DE DATA E HORA ESTRITO
    private static final DateTimeFormatter DATE_TIME_FORMAT = DateTimeFormatter
            .ofPattern("dd/MM/uuuu HH:mm")
            .withResolverStyle(ResolverStyle.STRICT);

    // 1. VALIDAÇÃO DE INTEIROS (int)
    public static int lerNumeroInteiro(Scanner sc, String mensagem) {

        int tentativas = 0;

        while (tentativas < MAX_TENTATIVAS) {

            System.out.print(mensagem);

            String entrada = sc.nextLine().trim();

            try {
                return Integer.parseInt(entrada);
            } catch (NumberFormatException e) {
                System.out.println(MSG_INTEIRO);
                tentativas++;
            }
        }

        throw new LeituraInvalidaException("Entrada de número inteiro inválida após "
                + MAX_TENTATIVAS + " tentativas.");
    }

    // 2. VALIDAÇÃO DE INTEIROS LONGOS (long) - Ideal para IDs grandes ou CNPJs numéricos
    public static long lerNumeroLong(Scanner sc, String mensagem) {

        int tentativas = 0;

        while (tentativas < MAX_TENTATIVAS) {

            System.out.print(mensagem);

            String entrada = sc.nextLine().trim();

            try {
                return Long.parseLong(entrada);
            } catch (NumberFormatException e) {
                System.out.println(MSG_LONG);
                tentativas++;
            }
        }

        throw new LeituraInvalidaException("Entrada de número longo inválida após "
                + MAX_TENTATIVAS + " tentativas.");
    }

    // 3. VALIDAÇÃO DE DOUBLES (double)
    public static double lerNumeroDouble(Scanner sc, String mensagem) {

        int tentativas = 0;

        while (tentativas < MAX_TENTATIVAS) {

            System.out.print(mensagem);

            String entrada = sc.nextLine().trim();

            try {
                return Double.parseDouble(entrada);
            } catch (NumberFormatException e) {
                System.out.println(MSG_DOUBLE);
                tentativas++;
            }
        }

        throw new LeituraInvalidaException("Entrada de número decimal inválida após "
                + MAX_TENTATIVAS + " tentativas.");
    }

    // 4. VALIDAÇÃO DE TEXTO (String) - Evita campos vazios
    public static String lerTexto(Scanner sc, String mensagem) {

        int tentativas = 0;

        while (tentativas < MAX_TENTATIVAS) {

            System.out.print(mensagem);

            String entrada = sc.nextLine().trim();

            if (!entrada.isEmpty()) {
                return entrada;
            }

            System.out.println(MSG_TEXTO);
            tentativas++;
        }

        throw new LeituraInvalidaException("Entrada de texto inválida após "
                + MAX_TENTATIVAS + " tentativas.");
    }

    // 5. VALIDAÇÃO DE DATAS (LocalDate)
    public static LocalDate lerData(Scanner sc, String mensagem) {

        int tentativas = 0;

        while (tentativas < MAX_TENTATIVAS) {

            System.out.print(mensagem);

            String entrada = sc.nextLine().trim();

            try {
                return LocalDate.parse(entrada, DATE_FORMAT);

            } catch (DateTimeParseException e) {

                System.out.println("Data inválida! Use o formato dd/MM/uuuu (ex: 25/12/2026).");
                tentativas++;
            }
        }

        throw new LeituraInvalidaException("Entrada de data inválida após "
                + MAX_TENTATIVAS + " tentativas.");
    }

    // 6. VALIDAÇÃO DE DATA E HORA (LocalDateTime)
    public static LocalDateTime lerDataHora(Scanner sc, String mensagem) {

        int tentativas = 0;

        while (tentativas < MAX_TENTATIVAS) {

            System.out.print(mensagem);

            String entrada = sc.nextLine().trim();

            try {
                return LocalDateTime.parse(entrada, DATE_TIME_FORMAT);

            } catch (DateTimeParseException e) {

                System.out.println("Data e hora inválidas! Use o formato dd/MM/uuuu HH:mm (ex: 25/12/2026 14:30).");
                tentativas++;
            }
        }

        throw new LeituraInvalidaException("Entrada de data e hora inválida após "
                + MAX_TENTATIVAS + " tentativas.");
    }

    // 6. VALIDAÇÃO DE BOOLEANOS (boolean) - Aceita S/N ou Sim/Não (independente de maiúscula)
    public static boolean lerConfirmacao(Scanner sc, String mensagem) {

        int tentativas = 0;

        while (tentativas < MAX_TENTATIVAS) {

            System.out.print(mensagem + " (S/N): ");

            String entrada = sc.nextLine().trim().toUpperCase();

            if (entrada.equals("S") || entrada.equals("SIM")) {
                return true;
            }

            if (entrada.equals("N") || entrada.equals("NÃO") || entrada.equals("NAO")) {
                return false;
            }

            System.out.println(MSG_BOOLEAN);
            tentativas++;
        }

        throw new LeituraInvalidaException("Entrada de confirmação inválida após "
                + MAX_TENTATIVAS + " tentativas.");
    }

    /*
     * 8. VALIDAÇÃO DE ENUM (qualquer enum)
     * Permite ler valores de um enum de forma segura via console.
     * Exemplo: StatusPedido, TipoUsuario, etc.
     */
    public static <T extends Enum<T>> T lerEnum(Scanner sc, Class<T> enumType, String mensagem) {

        int tentativas = 0;

        while (tentativas < MAX_TENTATIVAS) {

            System.out.print(mensagem);

            String entrada = sc.nextLine().trim().toUpperCase();

            try {
                return Enum.valueOf(enumType, entrada);
            } catch (IllegalArgumentException e) {

                System.out.println("Valor inválido! Opções válidas: ");

                for (T constant : enumType.getEnumConstants()) {
                    System.out.println(" - " + constant);
                }

                tentativas++;
            }
        }

        throw new LeituraInvalidaException("Entrada de enum inválida após "
                + MAX_TENTATIVAS + " tentativas.");
    }

    /**
     * Exception de domínio para falhas de leitura/validação via console,
     * usada quando o número máximo de tentativas é excedido.
     */
    public static class LeituraInvalidaException extends RuntimeException {
        public LeituraInvalidaException(String message) {
            super(message);
        }
    }
}


/*
    COMO USAR O METODO LER ENUM
    Suponha um enum:

    public enum StatusPedido {
        PENDENTE,
        PAGO,
        CANCELADO
    }

    StatusPedido status = Leitor.lerEnum(
        sc,
        StatusPedido.class,
        "Status do pedido (PENDENTE, PAGO, CANCELADO): "
);
 */
