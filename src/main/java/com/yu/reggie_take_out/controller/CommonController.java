package com.yu.reggie_take_out.controller;

import com.yu.reggie_take_out.common.R;
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
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.UUID;

/**
 * 文件上传，下载处理
 */
@Slf4j
@RestController
@RequestMapping("/common")
public class CommonController {
    //读入properties的配置
    @Value("${reggie.path}")
    private String basePath;


    @PostMapping("/upload")
    //参数名要与web提交的name=“file”一致
    public R<String> upload(MultipartFile file) {

        //获取原文件名的文件类型
        String originalFilename = file.getOriginalFilename();
        String suffix = originalFilename.substring(originalFilename.lastIndexOf("."));
        log.info(file.toString());
        log.info(suffix);
        //UUID随机生成文件名
        String fileName = UUID.randomUUID().toString() + suffix;
        // 判断当前目录是否存在,不存在，新建
        File dir = new File(basePath);
        if(!dir.exists()){
            dir.mkdir();
        }
        try {
            //此处file为临时文件，需要转存
            file.transferTo(new File(basePath + fileName));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return R.success(fileName);
    }

    /**
     * 文件下载
     */
    @GetMapping("/download")
    public void download(String name, HttpServletResponse response){

        try {
            //1. 通过输入流读取文件
            FileInputStream fileInputStream = new FileInputStream(new File(basePath + name));
            //2. response输出流，返回给页面
            ServletOutputStream outputStream = response.getOutputStream();
            //设置返回文件类型
            response.setContentType("image/jpeg");
            int len = 0;
            byte[] bytes = new byte[1024];
            while ((len = fileInputStream.read(bytes)) != -1){
                outputStream.write(bytes, 0, len);
                outputStream.flush();
            }
            outputStream.close();
            fileInputStream.close();


        } catch (Exception e) {
            throw new RuntimeException(e);
        }


    }
}
