package com.xxxx.server;

import com.xxxx.server.mapper.AdminMapper;
import com.xxxx.server.pojo.Admin;
import com.xxxx.server.util.MinioUtil;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.io.*;
import java.util.List;

@SpringBootTest
class YebServerApplicationTests {

    @Resource
    private AdminMapper adminMapper;
    @Resource
    private MinioUtil minioUtil;

    /**
     * 测试连接数据库，以及获取所有的用户信息
     */
    @Test
    void contextLoads() throws IOException {
//        final List<Admin> admins = adminMapper.selectList(null);
//        admins.forEach(System.out::println);
//        System.out.println(minioUtil.putObject("lwf", "test.md", new FileInputStream(new File("C:\\Users\\lwf\\Desktop\\云e办\\git\\cloud-e-office\\yeb\\README.md"))));
       minioUtil.listObjectNames("lwf").forEach(System.out::println);
    //    InputStream object = minioUtil.getObject("lwf", "test.md");
        //System.out.println(minioUtil.getObjectUrl("lwf", "test.md"));
     //   System.out.println(IOUtils.toString(object));
        System.out.println(minioUtil.getObjectUrl("lwf", "204221-1580474541f2d1.jpg"));
    }

}
