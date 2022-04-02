package com.dyzwj.stream;

import java.util.Arrays;
import java.util.List;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class CreateStream {

    public static void main(String[] args) {

        Stream<Integer> integerStream = Stream.of(1, 2, 3, 4, 5);
        List<Integer> integers = Arrays.asList(1, 2, 3, 5, 6);
        integers.parallelStream().forEach(System.out::println);

        int[] arr = new int[]{1,2,3,4,56};
        IntStream stream = Arrays.stream(arr);

        Stream.iterate(0,x -> x +3).limit(10);

    }
}
