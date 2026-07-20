import java.util.ArrayList;
import java.util.List;

public class TestRecord {
    public static void main(String[] args) {
        // 测试代码
        List<String> myTags = new ArrayList<>();
        myTags.add("Java");

        BadUser user = new BadUser("张三", myTags);

        GoodUser goodUser = new GoodUser("李四", myTags);

        // 外部修改了原始列表
        myTags.add("Python");

        // user 内部的tags也会跟着改变
        System.out.println(user.tags());
        // 此处的tags不会改变
        System.out.println("GoodUser tags" + goodUser.tags());
    }
}

record BadUser(String name, List<String> tags) {
}

record GoodUser(String name, List<String> tags) {
    // 使用紧凑构造器:没有参数列表，没有显示赋值
    public GoodUser {
        // 1.参数校验
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("姓名不能为空");
        }

        // 防御性拷贝:将传入的可变列表拷贝成一个不可变对象列表副本
        // List.copyOf() 是Java10引入的，它会创建一个绝对不可变的列表副本
        tags = List.copyOf(tags);
    }
}
