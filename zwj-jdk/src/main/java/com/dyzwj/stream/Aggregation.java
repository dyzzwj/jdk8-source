package com.dyzwj.stream;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

public class Aggregation {

    public static void main(String[] args) {
        List<String> list = Arrays.asList("adnm", "admmt", "pot", "xbangd", "weoujgsd");
        //最长的字符串
        String s = list.stream().max(Comparator.comparing(String::length)).get();
        System.out.println(s);

        System.out.println();

        List<Integer> integers = Arrays.asList(7, 6, 9, 4, 11, 6);
        //取最大值
        Integer integer = integers.stream().max(Comparator.comparing(Integer::valueOf)).get();
        Integer integer1 = integers.stream().max((i, j) -> {
            return i.compareTo(j);
        }).get();

        System.out.println(integer);
        System.out.println(integer1);
        System.out.println();

        //获取员工工资最高的
        Person person = Person.samples().stream().max(Comparator.comparing(Person::getSalary)).get();
        System.out.println(person);

        System.out.println();

        //计算Integer集合中大于6的元素的个数。
        List<Integer> integers1 = Arrays.asList(7, 6, 4, 8, 2, 11, 9);
        long count = integers1.stream().filter(x -> x > 6).count();
        System.out.println(count);
    }

}
