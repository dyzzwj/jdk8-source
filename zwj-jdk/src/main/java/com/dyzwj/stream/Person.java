package com.dyzwj.stream;

import java.util.ArrayList;
import java.util.List;

public class Person {

    private String name;
    private int age;
    private int salary;
    private String sex;
    private String area;

    public static List<Person> samples(){
        List<Person> personList = new ArrayList<>();
        personList.add(new Person("Tom",24,8900,"male","New York"));
        personList.add(new Person("Jack",30,7000,"male","Washington"));
        personList.add(new Person("Lily",20,7800,"female","Washington"));
        personList.add(new Person("Anni",40,8200,"female","New York"));
        personList.add(new Person("Owen",50,9500,"male","New York"));
        personList.add(new Person("Alisa",35,7900,"female","New York"));
        return personList;
    }

    public Person() {
    }

    public Person(String name, int age, int salary, String sex, String area) {
        this.name = name;
        this.age = age;
        this.salary = salary;
        this.sex = sex;
        this.area = area;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public int getSalary() {
        return salary;
    }

    public void setSalary(int salary) {
        this.salary = salary;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public String getArea() {
        return area;
    }

    public void setArea(String area) {
        this.area = area;
    }

    @Override
    public String toString() {
        return "Person{" +
                "name='" + name + '\'' +
                ", age=" + age +
                ", salary=" + salary +
                ", sex='" + sex + '\'' +
                ", area='" + area + '\'' +
                '}';
    }
}
