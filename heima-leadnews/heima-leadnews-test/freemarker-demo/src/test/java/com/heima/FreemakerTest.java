package com.heima;

import com.heima.freemarker.FreemarkerDemotApplication;
import com.heima.freemarker.entity.Student;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.FileWriter;
import java.io.IOException;
import java.util.*;


/**
 * 使用 @SpringBootTest 注解标记的类会启动 Spring Boot 应用的完整上下文环境，
 * 适合在需要使用实际的 Spring 容器进行依赖注入和集成测试的场景中使用。
 *
 * `classes = FreemarkerDemotApplication.class` 参数指定启动测试时所使用的主应用程序类，
 * 用于加载应用程序的上下文。
 */

/**
 * 使用 @RunWith(SpringRunner.class) 注解指定测试运行器。
 *
 * `@RunWith` 是 JUnit 提供的注解，用于指定运行测试时使用的自定义运行器。通常，JUnit 会使用默认运行器，
 * 但当需要集成其他框架或功能（例如 Spring 的依赖注入、事务管理等）时，可以指定自定义运行器。
 *
 * `SpringRunner` 是 Spring Framework 提供的运行器之一，它结合了 Spring TestContext 框架，
 * 可在测试环境中启动一个轻量级的 Spring 应用上下文，帮助开发人员在测试过程中更方便地使用
 * Spring 的依赖注入、AOP、事务管理等功能。
 *
 * `@RunWith(SpringRunner.class)` 的作用包括：
 *
 * 1. 启动 Spring 应用上下文：它加载并初始化测试所需的 Spring 应用上下文。
 * 2. 支持 Spring 的依赖注入：允许在测试类中使用 `@Autowired` 等注解注入 Spring Bean。
 * 3. 管理测试事务：在需要时自动开启和回滚事务，便于数据库操作的回滚，确保每次测试独立。
 * 4. 与 `@SpringBootTest` 等注解配合：能够与 `@SpringBootTest`、`@MockBean` 等注解配合使用，
 *    以便进行集成测试，模拟或替换组件。
 *
 * 总之，`@RunWith(SpringRunner.class)` 能够帮助将 Spring 的核心特性引入到 JUnit 测试环境中，
 * 使得编写测试时可以使用 Spring 提供的各种工具与功能。
 */
@SpringBootTest(classes = FreemarkerDemotApplication.class)
@RunWith(SpringRunner.class)
public class FreemakerTest {

    @Autowired
    private Configuration configuration;

    @Test
    public void test() throws IOException, TemplateException {
        Template template = configuration.getTemplate("02-list.ftl");
        Map data = getData();
        template.process(data,new FileWriter("d:/list.html"));
    }


    private Map getData() {
        Map<String, Object> map = new HashMap<>();

        //小强对象模型数据
        Student stu1 = new Student();
        stu1.setName("小强");
        stu1.setAge(18);
        stu1.setMoney(1000.86f);
        stu1.setBirthday(new Date());

        //小红对象模型数据
        Student stu2 = new Student();
        stu2.setName("小红");
        stu2.setMoney(200.1f);
        stu2.setAge(19);

        //将两个对象模型数据存放到List集合中
        List<Student> stus = new ArrayList<>();
        stus.add(stu1);
        stus.add(stu2);

        //向map中存放List集合数据
        map.put("stus", stus);


        //创建Map数据
        HashMap<String, Student> stuMap = new HashMap<>();
        stuMap.put("stu1", stu1);
        stuMap.put("stu2", stu2);
        //向map中存放Map数据
        map.put("stuMap", stuMap);

        //返回Map
        return map;
    }
}
