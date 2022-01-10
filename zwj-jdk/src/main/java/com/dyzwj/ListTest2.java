package com.dyzwj;

import java.util.ArrayList;

public class ListTest2 {

    public static void main(String[] args){
        ArrayList<String> list = new ArrayList<String>();
        list.add("Java");
        list.add("Hello");
        list.add("World");

        //如果不是迭代至倒数第二个元素时删除元素同样会导致异常的产生
        /**
         * 原因解释：
         *
         * 集合中倒数第二个元素的索引为size - 2，当迭代器取出集合倒数第二个元素的时候，cursor指向的位置会向右移动一位，值会变为size - 1；
         * 如果此时通过集合去删除一个元素，集合中元素个数会减一，所以size值会变为size - 1；
         * 当迭代器试图去获取最后一个元素的时候，会先判断是否还有元素，调用hasNext()方法，上文中已经分析过，hasNext()方法会返回cursor!=size，但是此时的cursor和此时的size值都等于删除之前的size - 1，两者相等，那么hasNext()方法就会返回false，迭代器就不会再调用next方法获取元素了。
         *
         */
        for (String s : list) {
//            if(s.equals("World")){ //并发修改异常
            if(s.equals("Hello")){
                list.remove("Java");
            }
        }
        System.out.println(list);//控制台输出：[Hello, World]
    }
}
