package com.itheima.reggie.controller;

import com.itheima.reggie.common.R;
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

/**
 * 文件上传和下载
 */
@RestController
@RequestMapping("/common")
@Slf4j
public class CommonController {
    @Value("${reggie.path}")
    private String basePath;

    /**
     * 文件上传
     *
     * @param file file
     * @return R
     */
    @PostMapping("/upload")
    public R<String> upload(MultipartFile file) {
        // file是一个临时文件，需要转存到指定位置，否则本次请求完成后临时文件会删除
        log.info(file.toString());

        // 原始文件名
        String originalFilename = file.getOriginalFilename();

        if (originalFilename != null) {
            String suffix = originalFilename.substring(originalFilename.lastIndexOf("."));
            // 使用UUID重新生成文件名，防止文件名称重复造成文件覆盖
            String fileName = UUID.randomUUID().toString() + suffix;

            //创建一个目录对象
            File dir = new File(basePath);

            // 判断当前目录是否存在
            if (!dir.exists()) {
                //目录不存在，需要创建
                boolean mkdirs = dir.mkdirs();
                if (mkdirs) {
                    log.info("文件新建成功");
                }
            }

            try {
                file.transferTo(new File(basePath + fileName));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            return R.success(fileName);
        } else {
            log.error("File missing suffix");
            return R.success("");
        }
    }

    /**
     * 文件下载
     *
     * @param name name
     * @param response res
     */
    @GetMapping("/download")
    public void download(String name, HttpServletResponse response) {
        // TODO: 2023/11/30 java IO
        try (FileInputStream fileInputStream = new FileInputStream(new File(basePath + name));
             ServletOutputStream outputStream = response.getOutputStream()) {
            // 输入流，通过输入流读取文件内容
            // 输出流，通过输出流将文件写回浏览器，在浏览器展示图片

            response.setContentType("image/jpeg");

            int len = 0;
            byte[] bytes = new byte[1024];
            while ((len = fileInputStream.read(bytes)) != -1) {
                outputStream.write(bytes, 0, len);
                outputStream.flush();
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
