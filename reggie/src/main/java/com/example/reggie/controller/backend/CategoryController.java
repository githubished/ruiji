package com.example.reggie.controller.backend;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.reggie.commmon.R;
import com.example.reggie.entity.Category;
import com.example.reggie.entity.Employee;
import com.example.reggie.service.CategoryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequestMapping("/category")
@Slf4j
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    //查询菜品处理
    @GetMapping("/page")
    public R<Page> page(int page, int pageSize, String name){
        log.info("page={},pagesize={},name={}",page,pageSize,name);

        //构造分页构造器
        Page pageInfo=new Page(page,pageSize);

        //构造条件构造器
        LambdaQueryWrapper<Category> queryWrapper=new LambdaQueryWrapper<>();

        //调价排序条件
        queryWrapper.orderByAsc(Category::getSort);

        //执行查询
        categoryService.page(pageInfo,queryWrapper);
        log.info(pageInfo.toString());
        return R.success(pageInfo);

    }




    //新增菜品处理
    @PostMapping
    public R<String> save(@RequestBody Category category){

        //调用mybatisplus的添加功能
        log.info("新增菜品：{}",category.toString());
        categoryService.save(category);
        return R.success("新增菜品成功");
    }


    //修改菜品
    @PutMapping
    public R<String> update(@RequestBody Category category){
        log.info("修改菜品成功：{}",category.toString());
        categoryService.updateById(category);
        return R.success("修改成功");
    }

    //删除菜品
    @DeleteMapping
    public R<String> delete(Long ids){
        log.info("删除菜品成功：{}",ids);
        categoryService.remove(ids);
        return R.success("删除成功");
    }



    /**
     * 根据条件查询分类数据
     * @param category
     * @return
     */
    @GetMapping("/list")
    public R<List<Category>> list(Category category){
        //条件构造器
        LambdaQueryWrapper<Category> queryWrapper = new LambdaQueryWrapper<>();
        //添加条件
        queryWrapper.eq(category.getType() != null,Category::getType,category.getType());
        //添加排序条件
        queryWrapper.orderByAsc(Category::getSort).orderByDesc(Category::getUpdateTime);

        List<Category> list = categoryService.list(queryWrapper);
        return R.success(list);
    }

}
