package com.diniz.springbootstudy.utils;


import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public final class FormatadorData {

    private FormatadorData() {
    }

    private static final DateTimeFormatter DATE_FORMAT =
            DateTimeFormatter.ofPattern("dd/MM/uuuu");

    private static final DateTimeFormatter DATE_TIME_FORMAT =
            DateTimeFormatter.ofPattern("dd/MM/uuuu HH:mm");

    public static String formatarData(LocalDate data) {
        if (data == null) {
            return "";
        }
        return data.format(DATE_FORMAT);
    }

    public static String formatarDataHora(LocalDateTime dataHora) {
        if (dataHora == null) {
            return "";
        }
        return dataHora.format(DATE_TIME_FORMAT);
    }

    // --- NOVOS MÉTODOS (PARSING) ---

    /**
     * Converte uma String no formato dd/MM/uuuu para LocalDate.
     * Retorna null se a string for inválida ou vazia.
     */
    public static LocalDate converterParaData(String dataStr) {
        if (dataStr == null || dataStr.trim().isEmpty()) {
            return null;
        }
        try {
            return LocalDate.parse(dataStr, DATE_FORMAT);
        } catch (DateTimeParseException e) {
            // Aqui você pode logar o erro se necessário
            return null;
        }
    }

    /**
     * Converte uma String no formato dd/MM/uuuu HH:mm to LocalDateTime.
     * Retorna null se a string for inválida ou vazia.
     */
    public static LocalDateTime converterParaDataHora(String dataHoraStr) {
        if (dataHoraStr == null || dataHoraStr.trim().isEmpty()) {
            return null;
        }
        try {
            return LocalDateTime.parse(dataHoraStr, DATE_TIME_FORMAT);
        } catch (DateTimeParseException e) {
            return null;
        }
    }
}