package main;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Stream;

public class Main {

    public static void main(String[] args) {

        ArrayList<Foo> list = new ArrayList<>();
        for (int i = 0; i < 3; i++)
            list.add(new Foo(i));
        printListStatus(list);

        // for(el in list)
        for (Foo foo : list) {
            foo.setA("for, the old type.");
        }
        printListStatus(list);

        // stream
        Stream<Foo> st = list.stream();
        st.filter(foo -> foo.getB().equals(""))
                .forEach(foo -> foo.setB("stream, the modern type."));
        printListStatus(list);

        // CopyOnWriteArrayList
        CopyOnWriteArrayList<String> cowList = new CopyOnWriteArrayList<>();
        for (int i = 0; i < 3; i++)
            cowList.add("C.O.W List el " + i);

        // snapshot test
        Iterator<String> iter1 = cowList.iterator();
        cowList.add("C.O.W List el 3");
        Iterator<String> iter2 = cowList.iterator();
        printCowListStatus(iter1);
        printCowListStatus(iter2);

        // data remove test
        for (String str: cowList) {
            cowList.remove(0);
            cowList.add("New element!");
        }
        printCowListStatus(cowList);
    }

    public static void printListStatus(ArrayList<?> list) {
        System.out.println("--------------ArrayList---------------");
        list.forEach(System.out::println);
        System.out.println("--------------ArrayList---------------\n");
    }

    public static void printCowListStatus(CopyOnWriteArrayList<?> list) {
        System.out.println("----------CopyOnWriteArrayList--------");
        list.forEach(System.out::println);
        System.out.println("----------CopyOnWriteArrayList--------\n");
    }

    public static void printCowListStatus(Iterator<?> iter) {
        System.out.println("----------CopyOnWriteArrayList - Iterator--------");
        iter.forEachRemaining(System.out::println);
        System.out.println("----------CopyOnWriteArrayList - Iterator--------\n");
    }
}

class Foo {

    private final int id;
    private String a, b;

    public Foo(int id) {
        this.id = id;
        this.a = "";
        this.b = "";
    }

    @Override
    public String toString() {
        return "id:" + id + "\ta:" + a + "\tb:" + b;
    }

    public void setA(String str) {
        a = str;
    }

    public void setB(String str) {
        b = str;
    }

    public String getA() {
        return a;
    }

    public String getB() {
        return b;
    }

    public int getId() {
        return id;
    }

}