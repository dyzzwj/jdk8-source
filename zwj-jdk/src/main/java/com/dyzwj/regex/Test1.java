package com.dyzwj.regex;

import java.util.regex.Pattern;

public class Test1 {
    public static void main(String[] args) {
        Pattern pattern = Pattern.compile("^[A-Za-z0-9]+$");
        System.out.println(pattern.matcher("aaa1111").matches());
    }
}
