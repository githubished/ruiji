package com.example.reggie.controller.backend;

import com.example.reggie.commmon.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.UUID;

@RestController
@RequestMapping("/common")
@Slf4j
public class commonController {

    @Value("${reggie.path}")
    private String basePath;

    @PostMapping("/upload")
    //文件上传
    public R<String> upload(MultipartFile file) {  //file参数必须和表单传输的参数一致
        //file是个临时文件，需要转存
        log.info("文件上传：{}",file.toString());

        //获取文件后缀
        String originalFilename=file.getOriginalFilename();
        String suffix=originalFilename.substring((originalFilename.lastIndexOf(".")));

        //用UUID重新生成文件名，防止文件名重复
        String filename= UUID.randomUUID().toString()+suffix;

        //将临时文件转存到指定位置
        try {
            file.transferTo(new File(basePath+filename));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return R.success(filename);
    }

    /**
     * 文件下载
     * @param name
     * @param response
     */
    @GetMapping("/download")
    public void download(String name, HttpServletResponse response){

        try {
            //输入流，通过输入流读取文件内容
            FileInputStream fileInputStream = new FileInputStream(new File(basePath + name));

            //输出流，通过输出流将文件写回浏览器
            ServletOutputStream outputStream = response.getOutputStream();

            response.setContentType("image/jpeg");

            int len = 0;
            byte[] bytes = new byte[1024];
            while ((len = fileInputStream.read(bytes)) != -1){
                outputStream.write(bytes,0,len);
                outputStream.flush();
            }

            //关闭资源
            outputStream.close();
            fileInputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
    }
