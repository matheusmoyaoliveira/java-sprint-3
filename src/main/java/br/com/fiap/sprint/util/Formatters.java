package br.com.fiap.sprint.util;

import java.time.format.DateTimeFormatter;


public final class Formatters {

    private Formatters() {}

    public static final DateTimeFormatter DATE     = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    public static final DateTimeFormatter DATETIME = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    public static String onlyDigits(String s) {
        return s == null ? null : s.replaceAll("\\D", "");
    }

    public static String cpf(String digits11) {
        String d = onlyDigits(digits11);
        if (d == null || d.length() != 11) return digits11;
        return d.replaceFirst("(\\d{3})(\\d{3})(\\d{3})(\\d{2})", "$1.$2.$3-$4");
    }

    public static String phoneBR(String digits) {
        String d = onlyDigits(digits);
        if (d == null) return digits;
        if (d.length() == 11) return d.replaceFirst("(\\d{2})(\\d{5})(\\d{4})", "($1)$2-$3");
        if (d.length() == 10) return d.replaceFirst("(\\d{2})(\\d{4})(\\d{4})", "($1)$2-$3");
        return digits;
    }
}
