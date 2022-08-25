package com.yu.reggie_take_out.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yu.reggie_take_out.common.R;
import com.yu.reggie_take_out.entity.Employee;
import com.yu.reggie_take_out.sevice.EmployeeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.DigestUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.spi.LocaleNameProvider;

@Slf4j
@RestController
@RequestMapping("/employee")
public class EmployeeController {
    @Autowired
    private EmployeeService employeeService;

    /**
     *员工登录
     * @RequestBody 页面传来JSON格式，用requestbody
     */
    @PostMapping("/login")
    public R<Employee> login(HttpServletRequest request, @RequestBody Employee employee){
        //1. 用户输入密码MD5加密
        String password = employee.getPassword();
        password = DigestUtils.md5DigestAsHex(password.getBytes());
        //2. 根据用户名查询用户
        LambdaQueryWrapper<Employee> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Employee::getUsername, employee.getUsername());
        Employee emp = employeeService.getOne(queryWrapper);

        //3.是否查到
        if(emp == null){
            return R.error("User Dose not Exist");
        }

        //4. 密码比对
        if(!emp.getPassword().equals(password)){
            return R.error("Password Error");
        }

        //5. 查看员工状态是否为禁用
        if(emp.getStatus()==0){
            return R.error("User banned");
        }
        //6. 登录成功，将ID存入Session
        request.getSession().setAttribute("employee", emp.getId());
        return R.success(emp);
    }

    @PostMapping("/logout")
    public R<String> logout(HttpServletRequest request){
        //1.清理session中的员工ID
        request.getSession().removeAttribute("employee");
        return R.success("log out success");
    }

    /**
     * 添加新员工
     */
    @PostMapping
    public R<String> save(HttpServletRequest request, @RequestBody Employee employee){
        // 1.设置初始密码123456, MD5加密
        employee.setPassword(DigestUtils.md5DigestAsHex("123456".getBytes()));
//        // 2.设置创建,修改时间
//        employee.setCreateTime(LocalDateTime.now());
//        employee.setUpdateTime(LocalDateTime.now());
        // 3.设置创建用户
        Long empID = (Long) request.getSession().getAttribute("employee");
//        employee.setCreateUser(empID);
//        employee.setUpdateUser(empID);
        // 4. 调用service保存
        employeeService.save(employee);
        // 5. 返回提示信息
        return R.success("add success");
    }

    /**
     * 分页查询
     不用@RequestBody, get方式发送请求，参数在URL内

     */
    @GetMapping("/page")
    // MP 内定义Page泛型
    public R<Page> page(int page, int pageSize, String name){
        log.info("page = {}, size = {}, name = {}", page, pageSize, name);

        //1. 构造分页构造器
        Page pageInfo = new Page<>(page, pageSize);
        //2. 构造条件构造器 按名字查找，按更新时间排序
        LambdaQueryWrapper<Employee> queryWrapper = new LambdaQueryWrapper();
        //name有输入时，才按名字查询
        queryWrapper.like(StringUtils.hasLength(name), Employee::getName, name);
        queryWrapper.orderByDesc(Employee::getUpdateTime);
        //3.执行查询
        employeeService.page(pageInfo, queryWrapper);
        return R.success(pageInfo);
    }

    /**
     * 更新员工信息
     * @param employee
     * @return
     */
    @PutMapping
    public R<String> update(HttpServletRequest request, @RequestBody Employee employee){
//        employee.setUpdateTime(LocalDateTime.now());
        log.info(employee.toString());
//        long empID = (long) request.getSession().getAttribute("employee");
//        employee.setUpdateUser(empID);
        employeeService.updateById(employee);

        return R.success("update success");
    }

    /**
     * 根据ID查员工
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public R<Employee> getById(@PathVariable Long id){
        Employee employee = employeeService.getById(id);
        if(employee!=null){
            return R.success(employee);
        }
        return R.error("没有查询到");

    }
}
