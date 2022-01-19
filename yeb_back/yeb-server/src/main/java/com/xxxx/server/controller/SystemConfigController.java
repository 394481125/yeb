package com.xxxx.server.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.xxxx.server.pojo.*;
import com.xxxx.server.service.*;
import com.xxxx.server.util.AssertUtil;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author ppliang
 * @date 2020/12/19 19:38
 */
@RestController
@RequestMapping("/system")
public class SystemConfigController {

    @Resource
    private IMenuService menuService;
    @Resource
    private IDepartmentService departmentService;
    @Resource
    private IPositionService positionService;
    @Resource
    private IJoblevelService joblevelService;
    @Resource
    private IRoleService roleService;
    @Resource
    private IMenuRoleService menuRoleService;

    @ApiOperation(value = "通过用户id获取菜单列表")
    @GetMapping("/menu")
    public List<Menu> getMenusByAdminId() {
        return menuService.getMenusByAdminId();
    }

    @ApiOperation(value = "部门管理数据接口")
    @GetMapping("/basic/department/")
    public List<Department> departments() {
        return departmentService.getAllDepartment();
    }

    @ApiOperation(value = "职位管理数据接口")
    @GetMapping("/basic/pos/")
    public List<Position> positions() {
        return positionService.list();
    }

    @ApiOperation(value = "职称管理数据接口")
    @GetMapping("/basic/joblevel/")
    public List<Joblevel> joblevels() {
        return joblevelService.list();
    }


    @ApiOperation(value = "权限组数据接口")
    @GetMapping("/basic/permiss/")
    public List<Role> roles() {
        return roleService.list();
    }

    //部门

    /**
     * 部门删除
     *      1.id非空校验，待删除数据存在
     *      2.删除时判断下面是否有子模块
     *              有，则不能删
     *              没有，则直接删除
     *      3.执行操作
     *      4.删除后判断父部门是否还有子部门
     *              有不做修改
     *              没修改isParent为false
     * @param id
     * @return
     */
    @ApiOperation(value = "删除部门")
    @DeleteMapping("/basic/department/{id}")
    public RespBean deleteDepartment(@PathVariable("id") Integer id) {
        //非空校验，待删数据存在
        AssertUtil.isTrue(id==null,"id不存在");
        //根据id查询部门
        Department dbDepartment = departmentService.selectById(id);
        //校验待查询的部门是否存在
        AssertUtil.isTrue(dbDepartment==null,"待删除部门不存在");
        //根据查询的部门得到isParent
        Boolean isParent = dbDepartment.getIsParent();
        //判断是否为上级，为上级则有子部门
        AssertUtil.isTrue(isParent,"该部门存在子部门，不能删除");
        //获取父部门id
        Integer parentId = dbDepartment.getParentId();
        //执行删除操作
        //根据待删除部门父id查询父部门
        Department pDepartment = departmentService.selectById(parentId);
        //根据父id查询数据库看该父部门是否还存在子部门
        List<Department> departments = departmentService.selectByParentId(parentId);
        System.out.println(departments.size()+"======================");
        if (departments.size()==1){
            //父部门删除之后不存在子部门了，设置isParent为false
            pDepartment.setIsParent(false);
            System.out.println(pDepartment.getIsParent()+"-------------------------");
            boolean updateById = departmentService.updateById(pDepartment);
            AssertUtil.isTrue(!updateById,"父部门isParent更新失败");
        }
        System.out.println(pDepartment.getIsParent()+"-------------------------");
        boolean remove = departmentService.removeById(id);



        return RespBean.success("删除:" + remove);
    }

    /**
     *  添加部门
     *         1.根据部门名称判断部门是否已经存在，存在不能添加
     *         2.添加子部门.
     *              设置默认值
     *                  设置父类isParent为1
     *                  更新父部门数据库
     *         3.执行操作
     *         4.更新部门depPath
     *                  父部门depPath加上本部门id
     *
     * @param department
     * @return
     */

    @ApiOperation(value = "添加部门")
    @PostMapping("/basic/department/")
    public RespBean addDepartment(@RequestBody Department department) {
        //根据部门名称判断该部门是否存在
        Department dbDepartment = departmentService.selectByName(department.getName());
        AssertUtil.isTrue(dbDepartment!=null,"该部门已存在");
        //设置默认值
        //获取父id
        Integer parentId = department.getParentId();
        //根据父id查询父部门
        Department pDepartment = departmentService.selectById(parentId);
        //设置父部门的isParent
        pDepartment.setIsParent(true);
        //更新父部门的isParent
        boolean pUpdate = departmentService.updateById(pDepartment);
        //判断父部门是否更新成功
        AssertUtil.isTrue(!pUpdate,"父部门数据更新失败");
        //执行添加操作
        boolean add = departmentService.save(department);
        //更新数据库
        //根据添加的部门名称获取部门id
        Department department1 = departmentService.selectByName(department.getName());
        //获取父部门的depPath,加上添加的部门id，拼接
        String depPath = (pDepartment.getDepPath()+"."+Integer.toString(department.getId()));
        //设置部门的depPath
        department1.setDepPath(depPath);
        //更新数据库
        boolean updateById = departmentService.updateById(department1);
        AssertUtil.isTrue(!updateById,"depPath设置失败");
        return RespBean.success("添加:" + add);
    }

    /**
     *      修改职位
     *          1.根据职位名称查询数据库
     *          2.存在此职位的数据
     *                  比较id，为自己的，可以修改
     *                  比较id，不是自己的，不能修改
     *          3.不存在此职位的数据
     *                   可以修改
     *          4.执行操作
     * @param position
     * @return
     */
    @ApiOperation(value = "修改职位")
    @PutMapping("/basic/pos/")
    public RespBean updatePosition(@RequestBody Position position) {
        //根据职位名称查询数据库
        Position dbPosition = positionService.selectByName(position.getName());
        //存在数据并且不是自己的，则不能修改
        AssertUtil.isTrue(dbPosition!=null&&dbPosition.getId()!=position.getId(),"名称已存在，不能修改");
        //执行操作
        if (position.getId() == null) {
            return RespBean.error("id空");
        }
        boolean update = positionService.updateById(position);
        return RespBean.success("修改:" + update);
    }

    /**
     *      删除职位
     *             1.参数非空校验
     *             2.根据id查询数据库，校验数据是否存在
     *             3.执行操作
     * @param id
     * @return
     */
    @ApiOperation(value = "删除职位")
    @DeleteMapping("/basic/pos/{id}")
    public RespBean deletePosition(@PathVariable("id") Integer id) {
        //id非空校验
        AssertUtil.isTrue(id==null,"职位id不存在");
        //根据id查询数据库
        Position dbPosition = positionService.getById(id);
        AssertUtil.isTrue(dbPosition==null,"待删除职称不存在");
        if (id == null) {
            return RespBean.error("id空");
        }
        if (positionService.getById(id) == null) {
            return RespBean.success("id不存在");
        }
        boolean update = positionService.removeById(id);
        return RespBean.success("添加:" + update);
    }

    /**
     *     添加职位
     *          1.根据职位名称查询数据库
     *          2.判断是否存在同名职位
     *                  存在  不能添加
     *                  不存在 直接添加
     *          3.执行操作
     *
     * @param position
     * @return
     */
    @ApiOperation(value = "添加职位")
    @PostMapping("/basic/pos/")
    public RespBean addPosition(@RequestBody Position position) {
        //根据职位名称查询数据库
        Position dbPosition = positionService.selectByName(position.getName());
        //判断是否存在同名职位
        AssertUtil.isTrue(dbPosition!=null,"部门名称已存在");
        if (position.getId() != null) {
            return RespBean.error("id不需要");
        }
        boolean update = positionService.save(position);
        return RespBean.success("添加:" + update);
    }

    /**
     *      批量删除
     *          1.待删除id不能为空
     *          2.遍历id查询数据库，必须在存在内容
     *          3.执行操作
     * @param ids
     * @return
     */
    @ApiOperation(value = "批量删除职位")
    @DeleteMapping("/basic/pos/")
    public RespBean deletePositions(Integer[] ids) {
        //待删除id数组不能为空
        if (ids == null || ids.length == 0) {
            return RespBean.error("id没有传过来");
        }
        //根据id查询数据库是否存在
        for (Integer id:ids){
            Position dbPosition = positionService.getById(id);
            AssertUtil.isTrue(dbPosition==null,"待删除数据不存在");
        }
        //执行操作
        boolean update = positionService.removeByIds(Arrays.asList(ids));
        return RespBean.success("批量删除:" + update);
    }

    //职称
    @ApiOperation(value = "添加职称")
    @PostMapping("/basic/joblevel/")
    public RespBean addJobLevel(@RequestBody Joblevel joblevel) {
        if (joblevel.getId() != null) {
            return RespBean.error("id不需要");
        }
        boolean add = joblevelService.save(joblevel);
        return RespBean.success("添加职称:" + add);
    }

    @ApiOperation(value = "删除职称")
    @DeleteMapping("/basic/joblevel/{id}")
    public RespBean deleteJobLevel(@PathVariable("id") Integer id) {
        if (id == null) {
            return RespBean.error("id没有传值");
        }
        if (joblevelService.getById(id) == null) {
            return RespBean.error("职称id不存在");
        }
        boolean del = joblevelService.removeById(id);
        return RespBean.success("删除职称:" + del);
    }

    @ApiOperation(value = "批量删除职称")
    @DeleteMapping("/basic/joblevel/")
    public RespBean deleteJobLevels(Integer[] ids) {
        if (ids == null || ids.length == 0) {
            return RespBean.error("id没有传值");
        }
        boolean del = joblevelService.removeByIds(Arrays.asList(ids));
        return RespBean.success("批量删除职称:" + del);
    }

    @ApiOperation(value = "修改职称")
    @PutMapping("/basic/joblevel/")
    public RespBean updateJobLevel(@RequestBody Joblevel joblevel) {
        if (joblevel.getId() == null) {
            return RespBean.error("id需要");
        }
        boolean add = joblevelService.updateById(joblevel);
        return RespBean.success("添加职称:" + add);
    }

    //权限组
    @ApiOperation(value = "添加权限组角色")
    @PostMapping("/basic/permiss/role")
    public RespBean updateJobLevel(@RequestBody Role role) {
        if (role.getId() != null) {
            return RespBean.error("id不需要");
        }
        boolean add = roleService.save(role);
        return RespBean.success("添加角色:" + add);
    }

    @ApiOperation(value = "所有资源菜单")
    @GetMapping("/basic/permiss/menus")
    public List<Menu> menus() {
        return menuService.getAllMenus();
    }


    @ApiOperation(value = "查询角色已选中的id")
    @GetMapping("/basic/permiss/mid/{rid}")
    public List<Integer> menuRoles(@PathVariable("rid") Integer rid) {
        return menuRoleService.list(new QueryWrapper<MenuRole>().eq("rid", rid)).stream()
                .map(MenuRole::getMid).collect(Collectors.toList());
    }

    @ApiOperation(value = "修改权限资源")
    @PutMapping("/basic/permiss/")
    public RespBean updateResources(Integer rid, Integer[] mids) {
        //删除旧权限
        Map<String, Object> map = new HashMap<>();
        map.put("rid", rid);
        boolean remove = menuRoleService.removeByMap(map);
        if (!remove) {
            return RespBean.error("更新权限，在删除旧权限出错");
        }
        //准备插入对象列表
        List<MenuRole> menuRoles = new ArrayList<>();
        for (Integer i : mids) {
            MenuRole menuRole = new MenuRole();
            menuRole.setRid(rid);
            menuRole.setMid(i);
            menuRoles.add(menuRole);
        }
        boolean batch = menuRoleService.saveBatch(menuRoles);
        return RespBean.success("权限更新:" + batch);
    }

}
