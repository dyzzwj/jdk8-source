package com.dyzwj;

public class StringTest {

    public static void main(String[] args) {

        String a = "a";
        String b = "a";
        System.out.println(a == b);

        String c =  (a + b).intern();
        System.out.println(c == "aa ");

        String s = new String("1");
        s.intern();
        String s2 = "1";
        System.out.println( s == s2);


        String s3 = new String("1") + new String("1");
        s3.intern();
        String s4 = "11";
        System.out.println(s3 == s4);


    }

}
