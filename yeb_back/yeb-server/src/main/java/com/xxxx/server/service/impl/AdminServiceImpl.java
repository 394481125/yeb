package com.xxxx.server.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.xxxx.server.mapper.SysMsgMapper;
import com.xxxx.server.pojo.Role;
import com.xxxx.server.util.AssertUtil;
import com.xxxx.server.util.JwtTokenUtil;
import com.xxxx.server.pojo.Admin;
import com.xxxx.server.mapper.AdminMapper;
import com.xxxx.server.pojo.RespBean;
import com.xxxx.server.service.IAdminService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author ppl
 * @since 2020-12-19
 */
@Service
public class AdminServiceImpl extends ServiceImpl<AdminMapper, Admin> implements IAdminService {

    @Resource
    private AdminMapper adminMapper;
    @Resource
    private SysMsgMapper sysMsgMapper;
    @Resource
    private UserDetailsService userDetailsService;
    @Resource
    private PasswordEncoder passwordEncoder;
    @Resource
    private JwtTokenUtil jwtTokenUtil;
    @Value("${jwt.tokenHead}")
    private String tokenHead;



    @Override
    public Admin getAdminByUserName(String username) {
        return adminMapper.selectOne(new QueryWrapper<Admin>().eq("username", username));
    }

    @Override
    public RespBean  login(String username, String password, String code, HttpServletRequest request) {
        String captcha = (String) request.getSession().getAttribute("captcha");
        if (StringUtils.isBlank(code) || !captcha.equals(code)) {
            return RespBean.error("验证码填写错误！");
        }
        UserDetails userDetails = userDetailsService.loadUserByUsername(username);
        if (null == userDetails || !passwordEncoder.matches(password, userDetails.getPassword())) {
            return RespBean.error("用户名或密码不正确!");
        }
        if (!userDetails.isEnabled()){
            return RespBean.error("账号被禁用，请联系管理员!");
        }
        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authentication);
        String token = jwtTokenUtil.generateToken(userDetails);
        Map<String, String> tokenMap = new HashMap<>();
        tokenMap.put("token", token);
        tokenMap.put("tokenHead", tokenHead);
        return RespBean.success("登录成功", tokenMap);
    }

    /**
     * 删除管理员
     * @param id
     * @return
     */
    @Override
    @Transactional
    public RespBean deleteAdminById(Integer id) {
        RespBean respBean = RespBean.success("成功");
        AssertUtil.isTrue(id==null,"id为空");
        //外键依赖删除
        //删除t_sys_msg表的数据
        sysMsgMapper.deleteSysMagByAdminId(id);
        AssertUtil.isTrue(adminMapper.deleteAdminById(id)<1,"删除失败");
        return respBean;
    }

    /**
     * 查询全部用户及角色
     * @return
     */
    @Override
    public List<Admin> selectAdminAll(String name,String userName) {

        return adminMapper.selectAdminAll(name,userName);
    }

    /**
     * 更新管理员是否可用
     * @param admin
     * @return
     */
    @Override
    public RespBean updateAdminEnable(Admin admin) {
        RespBean respBean = new RespBean(200, "修改成功",null);
        Boolean enabled = admin.isEnabled();
        Integer id=admin.getId();
        AssertUtil.isTrue(adminMapper.updateAdminEnable(id,enabled)<1,"修改失败");
        return respBean;
    }

    /**
     * 修改管理员角色
     * @param adminId
     * @param rids
     * @return
     */
    @Override
    @Transactional
    public RespBean updateAdminRole(Integer adminId, String rids) {
        RespBean respBean = new RespBean(200, "修改成功",null);
        AssertUtil.isTrue(adminId==null,"id不存在");
        //先删除管理员所拥有的的角色
        adminMapper.deleteAdminRolesByAdminId(adminId);
        //添加管理员的角色
        String[] sRids = rids.split(",");
        for (String sRid : sRids) {
            if (!"null".equals(sRid)){
                Integer rid=Integer.parseInt(sRid);
                AssertUtil.isTrue(adminMapper.addAdminRole(adminId,rid)<1,"添加失败");
            }
        }
        return respBean;
    }

    /**
     *      修改信息
     */
    @Override
    public Integer updateByAdmin(Admin admin) {

        return adminMapper.updateByAdmin(admin);
    }

    @Override
    public Integer updatePassword(Integer id, String newPass) {
        return adminMapper.updatePassword(id,newPass);
    }

}
