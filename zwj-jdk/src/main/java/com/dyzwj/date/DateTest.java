package com.dyzwj.date;

import javax.xml.ws.spi.http.HttpHandler;
import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class DateTest {


    public static void main(String[] args) {
        DateTimeFormatter yyyyMMdd = DateTimeFormatter.ofPattern("yyyyMMdd");
        DateTimeFormatter hHmmss = DateTimeFormatter.ofPattern("HHmmss");
        System.out.println(LocalDateTime.now().format(yyyyMMdd));
        System.out.println(LocalDateTime.now().format(hHmmss));

    }
}

