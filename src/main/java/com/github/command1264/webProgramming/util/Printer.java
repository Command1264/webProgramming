package com.github.command1264.webProgramming.util;

import java.time.LocalDateTime;

public class Printer {
    public static void print(String str) {
        print(str, false);
    }
    public static void print(String str, boolean newLine) {
        if (newLine) System.out.println();
        System.out.print(LocalDateTime.now().format(DateTimeFormat.formatter) + ": " + str);
    }

    public static void println(String str) {
        println(str, false);
    }
    public static void println(String str, boolean newLine) {
        if (newLine) System.out.println();
        System.out.println(LocalDateTime.now().format(DateTimeFormat.formatter) + ": " + str);
    }
}
