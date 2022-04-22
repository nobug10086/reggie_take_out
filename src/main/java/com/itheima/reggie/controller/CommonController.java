package com.itheima.reggie.controller;

import com.itheima.reggie.common.R;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.UUID;

/**
 * @author LJM
 * @create 2022/4/16
 * 文件上传和下载
 */
@RestController
@RequestMapping("/common")
public class CommonController {

    @Value("${reggie.path}")
    private String basePath;


    /**
     * 文件的上传
     * @param file
     * @return
     */
    @PostMapping("/upload")
    public R<String> upload(MultipartFile file){
        //这个file是一个临时文件，需要转存到指定位置，否则本次请求完成后临时文件会删除

        //拿到文件的原始名
        String originalFilename = file.getOriginalFilename();
        //拿到文件的后缀名 比如 .png  .jpg
        String suffix = originalFilename.substring(originalFilename.lastIndexOf("."));
        //使用uuid生成的作为文件名的一部分，这样可以防止文件名相同造成的文件覆盖
        String fileName = UUID.randomUUID().toString() + suffix;

        //创建一个目录对象，看传文件的时候，接收文件的目录存不存在
        File dir = new File(basePath);
        if (!dir.exists()){
            //文件目录不存在，直接创建一个目录
            dir.mkdirs();
        }

        try {
            //把前端传过来的文件进行转存
            file.transferTo(new File(basePath + fileName));
        }catch (IOException e){
            e.printStackTrace();
        }

        return R.success(fileName);
    }


    @GetMapping("/download")
    public void download(String name, HttpServletResponse response){
        try {
            //输入流，通过输入流读取文件内容  这里的name是前台用户需要下载的文件的文件名
            //new File(basePath + name) 是为了从存储图片的地方获取用户需要的图片对象
            FileInputStream fileInputStream = new FileInputStream(new File(basePath + name));
            //输出流，通过输出流将文件写回浏览器
            ServletOutputStream outputStream = response.getOutputStream();

            //设置写回去的文件类型
            response.setContentType("image/jpeg");

            //定义缓存区，准备读写文件
            int len  = 0 ;
            byte[] buff = new byte[1024];
            while ((len = fileInputStream.read(buff)) != -1){
                outputStream.write(buff,0,len);
                outputStream.flush();
            }
            //关流
            outputStream.close();
            fileInputStream.close();

        }catch (Exception e){
            e.printStackTrace();
        }

    }

}
