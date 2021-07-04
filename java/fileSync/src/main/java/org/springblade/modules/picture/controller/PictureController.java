package org.springblade.modules.picture.controller;


import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import lombok.AllArgsConstructor;
import org.springblade.core.tool.api.R;
import org.springblade.modules.picture.entity.Picture;
import org.springblade.modules.picture.service.IPictureService;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@RestController
@RequestMapping("picture")
@AllArgsConstructor
public class PictureController {

    private IPictureService pictureService;

    @GetMapping("list")
    public R list(){
        return R.data(pictureService.list());
    }


    @PostMapping("remove")
	@Transactional
    public R remove(@RequestParam("url")String url) throws IOException{
        String path = "/project/portal/"+url;
        File f= new File(path);
        if(!f.exists()){
            return R.fail("文件不存在");
        }else{
            f.delete();
            Picture picture = new Picture();
            picture.setUrl(url);
            return R.status(pictureService.remove(Wrappers.query(picture)));
        }
    }

    @PostMapping("upload")
    public R upload(@RequestParam("file") MultipartFile file,@RequestParam("name")String uname) throws IOException{
        if (file.isEmpty()) {
            return R.fail("你没有选择上传文件");
        } else {
            String name = file.getOriginalFilename();
            int index = name.indexOf(".");
            if (index > -1) {
                String extendsName = name.substring(index);
                if (extendsName.equals(".jpg")||extendsName.equals(".png")||extendsName.equals(".jpeg")) {
                    File f = new File("/project/portal/file/picture/"+uname);
                    file.transferTo(f);
					f.setExecutable(true); //设置可执行权限  
					f.setReadable(true); //设置可读权限  
					f.setWritable(true); //设置可写权限 
					String command = "chmod 775 " + "/project/portal/file/picture/"+uname;
					Runtime runtime = Runtime.getRuntime();
					Process proc = runtime.exec(command);
                    Picture picture = new Picture();
                    picture.setUrl("file/picture/"+uname);
                    return R.status(pictureService.save(picture));
                } else {
                    return R.fail("文件格式有误");
                }
            } else {
                return R.fail("文件命名有误");
            }
        }
    }
}
