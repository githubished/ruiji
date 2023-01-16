package com.example.reggie.controller.front;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.reggie.commmon.R;
import com.example.reggie.entity.Employee;
import com.example.reggie.entity.OrderDetail;
import com.example.reggie.entity.Orders;
import com.example.reggie.service.OrderDetailService;
import com.example.reggie.service.OrderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/order")
@Slf4j
public class OrderDetailController {


    @Autowired
    private OrderService orderService;

    @Autowired
    private OrderDetailService orderDetailService;

    /**
     * 用户下单
     * @param orders
     * @return
     */
    @PostMapping("/submit")
    public R<String> submit(@RequestBody Orders orders){
        log.info("订单数据：{}",orders);
        orderService.submit(orders);
        return R.success("下单成功");
    }



    @GetMapping("/page")
    public  R<Page> page(int page, int pageSize){

        //构造分页构造器
        Page pageInfo=new Page(page,pageSize);

        //构造条件构造器
        LambdaQueryWrapper<OrderDetail> queryWrapper=new LambdaQueryWrapper<>();


        //执行查询
        orderDetailService.page(pageInfo,queryWrapper);

        return R.success(pageInfo);

    }
}
