package com.dyzwj.stream;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

public class Map {


    /**
     * map：接收一个函数作为参数，该函数会被应用到每个元素上，并将其映射成一个新的元素。
     * flatMap：接收一个函数作为参数，将流中的每个值都换成另一个流，然后把所有流连接成一个流。
     * @param args
     */
    public static void main(String[] args) {

        //英文字符串数组的元素全部改为大写
        String[] strArr = { "abcd", "bcdd", "defde", "fTr" };
        List<String> stringList = Arrays.stream(strArr).map(str -> str.toUpperCase(Locale.ROOT)).collect(Collectors.toList());
        stringList.stream().forEach(System.out::println);

        System.out.println();
        //整数数组每个元素+3。
        List<Integer> intList = Arrays.asList(1, 3, 5, 7, 9, 11);
        List<Integer> integerList = intList.stream().map(num -> num + 3).collect(Collectors.toList());
        integerList.stream().forEach(System.out::println);
        System.out.println();



        List<Person> samples = Person.samples();
        //也会修改原集合
        List<Person> personList = samples.stream().map(person -> {
            person.setSalary(person.getSalary() + 10000);
            return person;
        }).collect(Collectors.toList());

        for (int i = 0; i < samples.size(); i++) {
            System.out.println(samples.get(i) + ":" + personList.get(i));
        }


    }
}
