package com.example.reggie.commmon;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

//当调用sql插入和更新方法时会调用该函数

@Component
@Slf4j
public class MyMetaObjectHandler implements MetaObjectHandler {

    @Override
    public void insertFill(MetaObject metaObject) {
        log.info("公共字段自动填充[insert]:{}",metaObject.toString());

        metaObject.setValue("createTime",LocalDateTime.now());
        metaObject.setValue("updateTime",LocalDateTime.now());
        metaObject.setValue("createUser",BaseContext.getCurrentid());
        metaObject.setValue("updateUser",BaseContext.getCurrentid());


    }

    @Override
    public void updateFill(MetaObject metaObject) {
        log.info("公共字段自动填充[update]:{}",metaObject.toString());

        metaObject.setValue("updateTime",LocalDateTime.now());
        metaObject.setValue("updateUser",BaseContext.getCurrentid());
    }
}
