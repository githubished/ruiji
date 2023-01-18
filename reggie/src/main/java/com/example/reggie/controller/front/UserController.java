package com.example.reggie.controller.front;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.reggie.commmon.R;
import com.example.reggie.commmon.SendEmail;
import com.example.reggie.dto.UserDto;
import com.example.reggie.entity.User;
import com.example.reggie.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/user")
@Slf4j
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private RedisTemplate redisTemplate;

    @PostMapping("/sendEmail")
    public R<String> sendEmail(HttpServletRequest request, @RequestBody User user){

        String email=user.getPhone();
        log.info("发送邮件至：{}",email);
        //创造发送邮箱实体类
        SendEmail sendEmail=new SendEmail();
        //获取验证码
        String code= sendEmail.achieveCode();
        log.info("邮箱验证码：{}",code);
        //发送邮箱
        sendEmail.sendAuthCodeEmail(email,code);

        //将验证码和手机号存入session中
//        request.getSession().setAttribute("code",code);
//        request.getSession().setAttribute("email",email);

        //将验证码和手机号存入redis中
        redisTemplate.opsForValue().set("code",code,5,TimeUnit.MINUTES);
        redisTemplate.opsForValue().set("email",email,5, TimeUnit.MINUTES);

        return R.success("发送邮箱成功");
    }

    @PostMapping("/login")
    public R<String> login(HttpServletRequest request, @RequestBody UserDto user){

        //从表单域中取出email和code
        String email=user.getPhone();
        String code=user.getCode();

        log.info("你的："+code+"--"+email);

        //从session获取code和email
//        String codeInSession =(String) request.getSession().getAttribute("code");
//        String emailInSession=(String) request.getSession().getAttribute("email");

        //从redis中取出code和email
        Object emailInSession =redisTemplate.opsForValue().get(email);
        Object codeInSession= redisTemplate.opsForValue().get(code);

        log.info("正确的："+codeInSession+"--"+emailInSession);

        //如果邮箱和密码都正确
        if(code.equals(codeInSession) && emailInSession.equals(email)){

            LambdaQueryWrapper<User> queryWrapper=new LambdaQueryWrapper<>();
            queryWrapper.eq(User::getPhone,email);

            //如果该用户不存在数据库中
            User user1 =userService.getOne(queryWrapper);
            if(user1==null){
                User user2=new User();
                user2.setPhone(email);
                user2.setStatus(1);

                //保存到数据库中
                userService.save(user2);
            }
            //如果登录成功，删除redis中缓存的验证码
            redisTemplate.delete(email);
            redisTemplate.delete(email);

            //将该用户id存入session中
            request.getSession().setAttribute("user",user1.getId());

            return R.success("验证成功");
        }

     return R.error("登录失败");
    }
}
