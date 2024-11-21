package com.heima.minio.test;

import com.heima.file.service.FileStorageService;
import com.heima.minio.MinIOApplication;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

//注解的作用是告诉 Spring 容器加载应用程序上下文，这样就可以使用依赖注入等 Spring 特性。
// 如果没有这个注解，Spring 的上下文不会被加载，你将无法使用被 @Autowired 注解注入的任何 Spring Bean，
// 包括 FileStorageService。
@SpringBootTest(classes = MinIOApplication.class)
//注解是告诉 JUnit 使用 Spring 的测试支持库来运行测试。
// 没有这个注解，JUnit 将使用默认的测试运行器，而不是 Spring 提供的上下文支持，
// 这可能导致测试无法执行或者测试用例中无法访问到 Spring 管理的 bean。
@RunWith(SpringRunner.class)
public class MinIOTest {

    @Autowired
    private FileStorageService fileStorageService;

    @Test
    public void test() throws FileNotFoundException {

        FileInputStream fileInputStream = new FileInputStream("D:\\IT\\css\\index.css");
        String s = fileStorageService.uploadHtmlFile("plugins//js//", "index.css", fileInputStream);
        System.out.println(s);
    }


    public static void main(String[] args) {

        FileInputStream fileInputStream = null;
        try {
            // 1. 创建文件输入流，用于读取本地文件
            File file = new File("D:\\IT\\js\\index.js");
            fileInputStream = new FileInputStream(file);

            // 2. 创建 MinIO 客户端，使用指定的访问密钥和端点进行连接
            MinioClient minioClient = MinioClient.builder()
                    .credentials("minio", "minio123") // 访问密钥和秘密密钥
                    .endpoint("http://192.168.200.128:9000") // MinIO 服务的地址
                    .build();

            // 3. 设置上传对象的参数
            PutObjectArgs putObjectArgs = PutObjectArgs.builder()
                    .object("plugins/js/index.js") // 上传后在桶中的文件名，使用正斜杠
                    .contentType("application/javascript")// 设置文件的内容类型
                    .bucket("leadnews") // 指定的桶名称
                    .stream(fileInputStream, file.length(), -1) // 文件流及其大小
                    .build();

            // 4. 上传文件到 MinIO
            minioClient.putObject(putObjectArgs);

            // 5. 输出文件在 MinIO 中的访问链接
            System.out.println("文件上传成功！访问链接：");
            System.out.println("http://192.168.200.128:9000/leadnews/plugins/js/axios.min.js");

        } catch (Exception ex) {
            // 6. 捕获并打印异常信息
            ex.printStackTrace();
        } finally {
            // 7. 关闭文件输入流以释放资源
            if (fileInputStream != null) {
                try {
                    fileInputStream.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

}
