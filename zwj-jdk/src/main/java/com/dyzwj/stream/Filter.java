package com.dyzwj.stream;


import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Stream 过滤
 */
public class Filter {

    public static void main(String[] args) {
        List<Integer> list = Arrays.asList(6, 7, 3, 8, 1, 2, 9);
        Stream<Integer> stream = list.stream();
        stream.filter(x -> x > 7).forEach(System.out::println);

        System.out.println();

        //筛选员工中工资高于8000的人，并形成新的集合
        List<Person> collect = Person.samples().stream().filter(person -> person.getSalary() > 8000).collect(Collectors.toList());
        collect.forEach(System.out::println);
    }
}
