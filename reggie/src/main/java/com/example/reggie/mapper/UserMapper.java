package com.example.reggie.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.reggie.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface UserMapper extends BaseMapper<User> {

}
