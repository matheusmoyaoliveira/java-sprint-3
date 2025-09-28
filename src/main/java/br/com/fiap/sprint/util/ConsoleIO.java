package br.com.fiap.sprint.util;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Scanner;

public class ConsoleIO {
    private static final Scanner in = new Scanner(System.in);

    public static String line(String prompt) {
        System.out.print(prompt);
        return in.nextLine().trim();
    }

    public static long long_(String prompt) {
        while (true) {
            try { return Long.parseLong(line(prompt)); }
            catch (NumberFormatException e) { System.out.println("Valor inválido."); }
        }
    }

    public static LocalDate date(String prompt) {
        var f = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        while (true) {
            try { return LocalDate.parse(line(prompt), f); }
            catch (Exception e) { System.out.println("Data inválida (dd/MM/yyyy)."); }
        }
    }

    public static LocalDateTime dateTime(String prompt) {
        var f = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        while (true) {
            try { return LocalDateTime.parse(line(prompt), f); }
            catch (Exception e) { System.out.println("Data/hora inválida (dd/MM/yyyy HH:mm)."); }
        }
    }

    public static void pause() {
        System.out.print("\n<enter> para continuar...");
        in.nextLine();
        System.out.println();
    }
}