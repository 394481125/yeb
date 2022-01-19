package com.xxxx.server.controller;


import com.baomidou.mybatisplus.extension.conditions.update.UpdateChainWrapper;
import com.xxxx.server.pojo.Admin;
import com.xxxx.server.pojo.RespBean;
import com.xxxx.server.service.IAdminService;
import com.xxxx.server.util.AssertUtil;
import com.xxxx.server.util.MinioUtil;
import com.xxxx.server.vo.ChangeHeadImg;
import com.xxxx.server.vo.Password;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.security.Principal;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author ppl
 * @since 2020-12-19
 */
@RestController
@RequestMapping("/admin")
@Slf4j
public class AdminController {
   @Resource
    private IAdminService adminService;
    @Resource
    private PasswordEncoder passwordEncoder;
   @Resource
   private MinioUtil minioUtil;

   @Value("${minio.bucketName}")
   private String bucketName;

   @PostMapping("/userface")
    public RespBean upload(ChangeHeadImg img){
       log.info("上传文件"+img.getFile().getOriginalFilename());
       String fileName = img.getUsername()+System.currentTimeMillis();
       minioUtil.putObject(bucketName, img.getFile(),fileName );
       //文件路径
       String url=minioUtil.getObjectUrl(bucketName, fileName);
       Admin admin=new Admin();
       admin.setEnabled(img.getEnabled());
       admin.setId(img.getId());
       admin.setUserFace(url);
       String userFace = img.getUserFace();
       boolean b = adminService.updateById(admin);
       if (b){
           minioUtil.removeObject(bucketName,userFace.substring(userFace.lastIndexOf("/")+1));
           return RespBean.success("修改成功");
       }

       return RespBean.error(300,"修改失败");
   }

    /**
     *      修改信息
     *          1.非空校验
     *
     *          2.执行更新操作
     * @param admin
     * @return
     */
   @PutMapping("info")
    public RespBean  updateAdmin(@RequestBody  Admin admin){
       System.out.println(admin.toString());
       //非空校验
       AssertUtil.isTrue(admin.getName()==null||admin.getPhone()==null||admin.getTelephone()==null||admin.getAddress()==null,"数据不能为空");
       //执行操作
       AssertUtil.isTrue(adminService.updateByAdmin(admin)<1,"修改信息失败");
       return RespBean.success("修改信息成功");
   }
    /**
     *      修改密码  参数(id ,新密码，旧密码，确密码)
     *          非空校验
     *          1.根据id查询密码
     *          2.将新数据库密码和旧密码进行校验
     *          3.新密码和旧密码校验，不能相同
     *          4.将新密码和确认密码校验，密码必须相同
     *          4.执行更新操作
     */
    @PutMapping("pass")
    public RespBean updatePass(@RequestBody Password password){
        Integer adminId=password.getAdminId();
        String oldPass=password.getOldPass();
        String pass=password.getPass();
        String checkPass=password.getCheckPass();

        System.out.println(adminId);
        System.out.println(oldPass);
        //非空校验
        AssertUtil.isTrue(adminId==null,"数据不存在");
        AssertUtil.isTrue(oldPass==null,"旧密码不能为空");
        AssertUtil.isTrue(pass==null,"新密码不能为空");
        AssertUtil.isTrue(checkPass==null,"确认密码不能为空");
        //根据id查询数据库
        Admin dbAdmin = adminService.getById(adminId);
        //将前台旧密码加密
        String encodeOldPass = passwordEncoder.encode(oldPass);
        System.out.println(encodeOldPass);
        //$10$ogvUqZZAxrBwrmVI/e7.SuFYyx8my8d.9zJ6bs9lPKWvbD9eefyCe
        //将前台密码加密后与数据库查询密码校验
        AssertUtil.isTrue(!passwordEncoder.matches(oldPass, dbAdmin.getPassword()),"旧密码不正确");
        //新密码和旧密码校验
        AssertUtil.isTrue(pass.equals(oldPass),"新密码不能和旧密码一致");
        //新密码和确密码一致
        AssertUtil.isTrue(!pass.equals(checkPass),"新密码和确认密码不一致");
        //加密更新后的数据库
        String encode = passwordEncoder.encode(pass);
        //执行更新操作
        AssertUtil.isTrue(adminService.updatePassword(adminId,encode)<1,"密码修改失败");
        return RespBean.success("修改密码成功");
    }
}
