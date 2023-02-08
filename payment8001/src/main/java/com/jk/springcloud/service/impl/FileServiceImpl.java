package com.jk.springcloud.service.impl;


import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import com.jk.springcloud.common.R;
import com.jk.springcloud.entity.Files;
import com.jk.springcloud.mapper.FileMapper;
import com.jk.springcloud.service.FileService;
import org.apache.commons.io.FileUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class FileServiceImpl extends ServiceImpl<FileMapper, Files> implements FileService {
    public String uploadFile(MultipartFile file, Files files) throws IOException {

        String fileName = file.getOriginalFilename();

        //重构输出文件名，保证不重名
        //substring(int beginIndex);   (int beginIndex,int endIndex)
        String suffix = fileName.substring(fileName.lastIndexOf("."));
        //获取全局唯一的串
        String uuid = UUID.randomUUID().toString();

        String path = uuid+suffix;

        List<String> list = new ArrayList<>();
        list.add(".jpg");
        list.add(".png");
        list.add(".jpeg");
        list.add(".gif");
        if(list.indexOf(suffix) >= 0) {
            System.out.println(uuid + suffix);
              String p = "D:\\IDEA\\activity\\src\\main\\resources\\static\\imgs\\";
              FileUtils.copyInputStreamToFile(file.getInputStream(), new File(p + path));
//            FileUtils.copyInputStreamToFile(file.getInputStream(), new File("D:/temp/imgs/" + path));
        }else{
            System.out.println(uuid + suffix);
            String p = "D:\\IDEA\\activity\\src\\main\\resources\\static\\files\\";
            FileUtils.copyInputStreamToFile(file.getInputStream(), new File(p + path));
        }

        return path;
//        -----------------------------------------------------------------------

//        this.save(files);
    }

    @Transactional
    public R updateFile(Files file){
        try {
            this.updateById(file);
        }catch (Exception e){
            System.out.println("文件更新出错");
            throw new RuntimeException("文件更新出错!", e);
//            e.printStackTrace();
        }
        System.out.println("文件更新成功");
        return R.error("文件更新成功!");
    }


}
