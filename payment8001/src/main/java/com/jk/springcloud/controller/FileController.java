package com.jk.springcloud.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;

import com.jk.springcloud.clients.ActivityTaskClient;
import com.jk.springcloud.clients.UserClient;
import com.jk.springcloud.common.R;
import com.jk.springcloud.dto.FileDto;
import com.jk.springcloud.entity.Files;
import com.jk.springcloud.entity.User;
import com.jk.springcloud.entity.UserActivity;
import com.jk.springcloud.service.impl.FileServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/file")
@CrossOrigin
@Slf4j
public class FileController {

    @Autowired
    private FileServiceImpl fileService;

//    @Autowired
//    private UserActivityService userActivityService;
//    @Autowired
//    private UserService userService;

    @Resource
    private UserClient userClient;

    @Autowired
    private ActivityTaskClient activityTaskClient;
    /**
     * 某任务下taskId所有的文件
     *
     * @param
     * @param
     * @return
     */
////    若要配置热点流量控制，要声明为SentinelResource的资源
//    @SentinelResource("hot")
    @GetMapping("/{fileId}")
    public R<Files> getById(@PathVariable int fileId) {

        Files files = fileService.getById(fileId);

        return R.success(files);
    }

    /**
     * 通过ActivityId和TaskId查File
     * getById
     *
     * @param
     * @return
     */
    @GetMapping("/getAll/{actId}/{taskId}")
    public R<List<FileDto>> getAll(@PathVariable int actId, @PathVariable int taskId, HttpSession session) {
        LambdaQueryWrapper<Files> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Files::getActivityId, actId);
        queryWrapper.eq(Files::getTaskId, taskId);

        List<Files> files = fileService.list(queryWrapper);


        //        想要用Stream流来操作集合，那么就需要将数组或者集合先转换成Stream流才可以操作
        List<FileDto> list = files.stream().map((item) -> {
                FileDto fileDto = new FileDto();
                BeanUtils.copyProperties(item,fileDto);

        //      获取发布者在活动中的昵称头像，如果不存在则为活动创建者，直接获取创建者用户头像名字
                R<UserActivity> userActivityR = activityTaskClient.getByUserActId(item.getUserId(),item.getActivityId());
                UserActivity userActivity = userActivityR.getData();

                if(userActivity != null ) {
                    fileDto.setNickName(userActivity.getNickName());
                    fileDto.setHeadPortrait(userActivity.getHeadPortrait());
                    fileDto.setRoleId(userActivity.getRoleId());
                }else{
                    R<User> userR = userClient.getById( item.getUserId() );
                    User user = userR.getData();

                    fileDto.setNickName(user.getName());
                    fileDto.setHeadPortrait(user.getHeadPortrait());
                    fileDto.setRoleId(2);
                }
            return fileDto;
        }).collect(Collectors.toList());

//根据roleId 升序排序
        // 使用匿名比较器排序
        Collections.sort(list, new Comparator<FileDto>() {
            @Override
            public int compare(FileDto p1, FileDto p2) {
                return p1.getRoleId() - p2.getRoleId() ;
            }
        });


        return R.success(list);
    }


    /**
     * 上传文件
     *
     * @param
     * @return
     */
    @PostMapping("/upload")
    public R uploadFile(MultipartFile file) throws IOException {

        String path = fileService.uploadFile(file, null);

        R r = new R();
        r.add("path", path);
        r.setCode(1);
        r.setData("文件上传新增成功");
        return r;
    }

    /**
     *下载文件
     * @param
     * @return
     */
    @GetMapping("/download/{fileName}")
    public void downFile(HttpServletResponse resp, HttpServletRequest req , @PathVariable String fileName) throws IOException {
//        String imgName = files.getFileName();
        String imgName =fileName;

        System.out.println(imgName);

        //设置响应头    attachment下载，filename下载时显示给用户的文件名，可自定义
        resp.setHeader("Content-Disposition","attachment;filename="+imgName);

        //输出字节流
        ServletOutputStream os = resp.getOutputStream();

//        //取到资源文件夹的完整路径       获取项目下images文件夹的完整路径
//        String path = req.getServletContext().getRealPath("static");
//        File file = new File(path,imgName);

        String path;
        String suffix = fileName.substring(fileName.lastIndexOf("."));
        List<String> list = new ArrayList<>();
        list.add(".jpg");
        list.add(".png");
        list.add(".jpeg");
        list.add(".gif");
        if(list.indexOf(suffix) >= 0)
            path = "D:\\IDEA\\activity\\src\\main\\resources\\static\\imgs\\";
        else
            path = "D:\\IDEA\\activity\\src\\main\\resources\\static\\files\\";

        File file = new File(path,imgName);

       resp.addHeader("Content-Length", String.valueOf(file.length()));

        byte[] bytes = FileUtils.readFileToByteArray(file);
        os.write(bytes);
        os.flush();
        os.close();

    }






//    /**
//     *下载文件  版本2备用 功能一样
//     * @param
//     * @return
//     */
//    @GetMapping("/download2/{fileName}")
//    public void downFile2(HttpServletResponse resp, HttpServletRequest req ,@PathVariable String fileName) throws IOException {
////        String imgName = files.getFileName();
//        String imgName =fileName;
//
//        System.out.println(imgName);
//
//        //设置响应头    attachment下载，filename下载时显示给用户的文件名，可自定义
//        resp.setHeader("Content-Disposition","attachment;filename="+imgName);
//
//        String path =  "D:\\IDEA\\activity\\src\\main\\resources\\static\\files\\";
//        File file = new File(path,imgName);
//
//        resp.addHeader("Content-Length", String.valueOf(file.length()));
//
//        byte[] buffer = new byte[1024];
//        BufferedInputStream bis = null;
//        OutputStream os = null;
//
//        os = resp.getOutputStream();
//        bis = new BufferedInputStream(new FileInputStream(file));
//        while(bis.read(buffer) != -1){
//            os.write(buffer);
//        }
//
//        os.flush();
//        os.close();
//
////        return R.success("文件下载成功");
//    }








    /**
     * 添加文件信息
     *
     * @param
     * @return
     */
    @PostMapping
    public R saveFile(@RequestBody Files file) {

        fileService.save(file);

        return R.success("新增文件成功");

    }

//    修改文件
@PutMapping
    public R updateFile(@RequestBody Files file){
        return fileService.updateFile(file);
    }

    /**
     * 文件创建者或管理员删除文件
     * @ param
     * @return
     */
    @DeleteMapping("/{id}")
    public R<String> delete(@PathVariable int id){

        fileService.removeById(id);

        return R.success("删除成功");
    }

}
