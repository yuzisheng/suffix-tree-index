# Suffix Tree Index
> This repo is based on [jsinghc's suffix tree index](https://github.com/jsinghc/SuffixTreeIndex).

## 1. Introduction
A Java implementation of an in-memory suffix tree index for quick in search.

This index allows for adding texts (`List<String>`) to the index with match ids for each string. And you can search for any string list, and it returns an array of matching ids.

## 2. Example
```java
public static void main(String[] args) {
    SuffixTreeIndex stIndex = new SuffixTreeIndex();
    stIndex.addText(Arrays.asList("e1", "e2", "e3"), "t1");
    stIndex.addText(Arrays.asList("e2", "e3", "e4"), "t2");

    List<String> path1 = Arrays.asList("e2", "e3");
    ArrayList<String> res1 = stIndex.search(path1);
    System.out.println("st index search " + path1 + " => result: " + res1);  // [t1, t2]

    List<String> path2 = Arrays.asList("e1", "e2", "e3");
    ArrayList<String> res2 = stIndex.search(path2);
    System.out.println("st index search " + path2 + " => result: " + res2);  // [t1]

    List<String> path3 = Collections.singletonList("e4");
    ArrayList<String> res3 = stIndex.search(path3);
    System.out.println("st index search " + path3 + " => result: " + res3);  // [t2]

    List<String> path4 = Arrays.asList("e4", "e5");
    ArrayList<String> res4 = stIndex.search(path4);
    System.out.println("st index search " + path4 + " => result: " + res4);  // []

    List<String> path5 = Arrays.asList("e1", "e3", "e2");
    ArrayList<String> res5 = stIndex.search(path5);
    System.out.println("st index search " + path5 + " => result: " + res5);  // []
}
```