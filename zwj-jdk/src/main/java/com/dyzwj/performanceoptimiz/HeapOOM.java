package com.dyzwj.performanceoptimiz;

import java.util.ArrayList;
import java.util.List;

/**
 * JVM配置参数
 * -Xms20m    JVM初始分配的内存20m
 * -Xmx20m   JVM最大可用内存为20m
 * -XX:+HeapDumpOnOutOfMemoryError 当JVM发生OOM时，自动生成DUMP文件
 * -XX:HeapDumpPath=D:\dump\  生成DUMP文件的路径
 *
 * MAT分许堆转储文件
 */
public class HeapOOM {
    static class OOMObject {
    }
    public static void main(String[] args) {
        List<OOMObject> list = new ArrayList<OOMObject>();
        //在堆中无限创建对象
        while (true) {
            list.add(new OOMObject());
        }
    }
}
