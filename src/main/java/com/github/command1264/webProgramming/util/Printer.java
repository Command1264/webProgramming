package com.github.command1264.webProgramming.util;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Printer {
    public static void print(String str) {
        System.out.print(LocalDateTime.now().format(DateTimeFormatter.ofPattern(DateTimeFormat.format)) + ": " + str);
    }
    public static void println(String str) {
        System.out.println(LocalDateTime.now().format(DateTimeFormatter.ofPattern(DateTimeFormat.format)) + ": " + str);
    }
}
