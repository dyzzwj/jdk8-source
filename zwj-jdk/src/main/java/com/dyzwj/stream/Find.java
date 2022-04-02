package com.dyzwj.stream;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

/**
 * Steam API查找
 */
public class Find {


    public static void main(String[] args) {

        List<Integer> list = Arrays.asList(7, 6, 9, 3, 8, 2, 1);
        // 遍历输出符合条件的元素
        list.stream().filter(x -> x > 6).forEach(System.out::println);
        System.out.println();
        //匹配第一个
        Optional<Integer> first = list.stream().filter(x -> x > 6).findFirst();
        if(first.isPresent()){
            System.out.println(first.get());
        }

        System.out.println();
        //匹配任意一个(适用于并行流)
        Optional<Integer> any = list.parallelStream().filter(x -> x > 6).findAny();
        if(any.isPresent()){
            System.out.println(any.get());
        }

        //是否匹配
        System.out.println();
        boolean b = list.stream().anyMatch(x -> x < 6);
        System.out.println(b);


    }

}
