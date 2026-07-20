package stringdemo;

public final class MyStringDemo {
    public static void main(String[] args) {
        String oldString = new String("aabac");
        String newString = oldString.replace('a', 'x');
        System.out.println(newString);
    }
}
