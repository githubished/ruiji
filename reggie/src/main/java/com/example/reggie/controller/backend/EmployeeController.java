package com.example.reggie.controller.backend;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.reggie.commmon.R;
import com.example.reggie.entity.Employee;
import com.example.reggie.service.EmployeeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.DigestUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.security.SecureRandom;
import java.time.LocalDateTime;

@Slf4j
@RestController
@RequestMapping("/employee")
public class EmployeeController {

    @Autowired
    private EmployeeService employeeService;


    //员工登录处理

    @PostMapping("/login")
    public R<Employee> login(HttpServletRequest request, @RequestBody Employee employee){
        //将页面提交的密码进行md5加密处理
        String password =employee.getPassword();
        password= DigestUtils.md5DigestAsHex(password.getBytes());

        //将页面提交的用户名查询数据库
        LambdaQueryWrapper<Employee> queryWrapper=new LambdaQueryWrapper<>();
        queryWrapper.eq(Employee::getUsername,employee.getUsername());
        Employee emp=employeeService.getOne(queryWrapper);

        //如果没有查询到用户名,
        if(emp==null ){
            return R.error("登录失败1");
        }

        //如果密码不一样
        if(!emp.getPassword().equals(password)){
            return R.error("登录失败2");
        }

        //查看员工是否被封号
        if(emp.getStatus()==0){
            return R.error("账号被禁用");
        }

        //登录成功，将员工id存入session并返回登录成功的结果
        request.getSession().setAttribute("employee",emp.getId());
        return R.success(emp);
    }

    //员工退出处理

    @PostMapping("/logout")
    public R<String> logout(HttpServletRequest request){
        request.getSession().removeAttribute("employee");
        return R.success("退出成功");
    }

    //新增员工处理

    @PostMapping
    public R<String> save(HttpServletRequest request,@RequestBody Employee employee){

        //设置初始密码123456，进行md5加密
        employee.setPassword(DigestUtils.md5DigestAsHex("123456".getBytes()));

//        employee.setCreateTime(LocalDateTime.now());
//        employee.setUpdateTime(LocalDateTime.now());
//
//        //获取当前登录用户的id
//        Long empId=(Long)request.getSession().getAttribute("employee");
//
//        employee.setCreateUser(empId);
//        employee.setUpdateUser(empId);

        //调用mybatisplus的添加功能
        log.info("新增员工：{}",employee.toString());
        employeeService.save(employee);
        return R.success("新增员工成功");
    }


    //查询员工处理

    @GetMapping("/page")
    public  R<Page> page(int page, int pageSize, String name){
        log.info("page={},pagesize={},name={}",page,pageSize,name);

        //构造分页构造器
        Page pageInfo=new Page(page,pageSize);

        //构造条件构造器
        LambdaQueryWrapper<Employee> queryWrapper=new LambdaQueryWrapper<>();
        //添加过滤条件
        queryWrapper.like(!StringUtils.isEmpty(name),Employee::getName,name);
        //调价排序条件
        queryWrapper.orderByDesc(Employee::getUpdateTime);

        //执行查询
        employeeService.page(pageInfo,queryWrapper);
        log.info("查询员工{}",pageInfo.toString());
        return R.success(pageInfo);

    }

    //修改员工状态处理

    @PutMapping
    public R<String> update(@RequestBody Employee employee){
        //获取当前登录用户的id
        //Long empId=(Long)request.getSession().getAttribute("employee");
        //employee.setUpdateTime(LocalDateTime.now());
       // employee.setUpdateUser(empId);
        employeeService.updateById(employee);

        return R.success("修改成功");
    }

    //数据回显处理
    @GetMapping("/{id}")
    public R<Employee> getById(@PathVariable Long id){
        log.info("根据id：{}，查询员工信息",id);
        Employee employee =employeeService.getById(id);
        if(employee!=null){
            return R.success(employee);
        }
        return R.error("没有查询到该员工");
    }
}
