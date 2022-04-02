package com.dyzwj;


import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 并发修改异常
 */
public class ListTest {


    public static void main(String[] args) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date parse = null;
        try {
            parse = sdf.parse("2022-03-10 10:00:00");
        } catch (ParseException e) {
            e.printStackTrace();
        }
        if (parse.before(new Date())){
            System.out.println(".......");
        }


        test1();
//        test2();
//        test3();
//        test4();
//        test5();
    }


    //增强for循环（并发修改异常）
    public static void test1(){
        List<String> gameList = new ArrayList<String>();
        gameList.add("lol");
        gameList.add("dota");
        gameList.add("pubg");
        gameList.add("dnf");
        for (String s : gameList) {
            if("pubg".equals(s)){
                gameList.add("dota2");
            }
        }
        gameList.forEach(System.out::println);
    }

    //迭代器遍历（并发修改异常）
    public static void test2(){
        List<String> gameList = new ArrayList<String>();
        gameList.add("lol");
        gameList.add("dota");
        gameList.add("pubg");
        gameList.add("dnf");
        Iterator<String> iterator = gameList.iterator();
        while (iterator.hasNext()){
            String next = iterator.next();
            if("dota".equals(next)){
                gameList.add("dota2");
            }
        }
        gameList.forEach(System.out::println);
    }

    //for循环遍历（不会出现并发修改异常）
    public static void test3(){
        List<String> gameList = new ArrayList<String>();
        gameList.add("lol");
        gameList.add("dota");
        gameList.add("pubg");
        gameList.add("dnf");
        for (int i = 0; i < gameList.size(); i++) {
            String s = gameList.get(i);
            if("pubg".equals(s)){
                gameList.add("dota2");
            }
        }
        gameList.forEach(System.out::println);
    }
    //使用list特有的迭代器遍历&&使用迭代器的api添加元素（不会出现并发修改异常）
    public static void test4(){
        List<String> gameList = new ArrayList<String>();
        gameList.add("lol");
        gameList.add("dota");
        gameList.add("pubg");
        gameList.add("dnf");
        ListIterator<String> stringListIterator = gameList.listIterator();
        while (stringListIterator.hasNext()){
            String next = stringListIterator.next();
            if("dota".equals(next)){
                stringListIterator.add("dota2");
            }
        }
        gameList.forEach(System.out::println);
    }

    //使用list特有的迭代器遍历,但使用集合的api添加元素（出现并发修改异常）
    public static void test5(){
        List<String> gameList = new ArrayList<String>();
        gameList.add("lol");
        gameList.add("dota");
        gameList.add("pubg");
        gameList.add("dnf");
        ListIterator<String> stringListIterator = gameList.listIterator();
        while (stringListIterator.hasNext()){
            String next = stringListIterator.next();
            if("dota".equals(next)){
                gameList.add("dota2");
            }
        }
        gameList.forEach(System.out::println);



    }





}
