#### 简单介绍
**Immutability（不可变性）模式**是一种在并发编程和软件设计中及其重要的设计模式。它的核心理念十分简单：**一个对象一旦被创建，其状态（内部数据）就永远不能被修改**。

#### 核心特征
1. 所有字段都是`final`的：保证字段在构造函数结束后不能再被赋值
2. 所有字段都是`private`的：防止外部直接访问
3. 没有`Setter方法`：不提供任何可以修改内部状态的方法
4. 防止引用逃逸（深拷贝）：如果字段包含可变对象（如`Date`、`List`），在构造函数和`Getter`方法中必须进行防御性拷贝（返回副本而非原始引用）

#### 注意
1. 对象的所有属性都是`final`的，并不能保证不可变性
2. 不可变对象也需要正确发布

关于第一点，可以参考以下代码
````Java
class Foo {
    int age;
}

final class Bar {
    final Foo foo;
    void setAge(int a) {
        foo.age = a;
    }
}
````

所以在使用 `Immutability`模式时一定要确认保持不变性的边界在哪里，是否要求属性对象也具备不可变性。

关于第二点，参考以下代码
````Java
// Foo线程安全
final class Foo {
    final int a = 0;
    final String name = "abc";
}

// Bar线程不安全
class Bar {
    Foo foo;
    void setFoo(Foo foo) {
        this.foo = foo;
    }
}
````

上述代码中，Foo具备不可变性，线程安全，但是类Bar不是线程安全的，类Bar中持有对Foo的引用foo，对foo的引用的修改在多线程中并不能保证可见性和原子性。

自Java16后，可以通过record关键字创建不可变类，如
````Java
record(String name, int age);
````

但是，record只保证浅层不可变性，如果record的字段是可变对象，举例如下
````Java
// 错误示范：外部传入的List直接赋值给了tags
public record  BadUser(String name, List<String> tags) {}

// 测试代码
List<String> myTags = new ArrayList<>();
myTags.add("Java");

BadUser user = new BadUser("张三", myTags);

// 外部修改了原始列表
myTags.add("Python");

// user 内部的tags也会跟着改变
System.out.println(user.tags); //[Java, Python] 
````

正确示范

````Java
public record GoodUser(String name, List<String> tags) {
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
````
