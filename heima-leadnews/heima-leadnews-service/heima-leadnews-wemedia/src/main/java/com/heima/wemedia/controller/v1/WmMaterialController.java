package com.heima.wemedia.controller.v1;

import com.heima.model.common.dtos.ResponseResult;
import com.heima.model.wemedia.dtos.WmMaterialDto;
import com.heima.wemedia.service.WmMaterialService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/v1/material")
public class WmMaterialController {
    @Autowired
    private WmMaterialService wmMaterialService;

    @RequestMapping("/upload_picture")
    public ResponseResult uploadPicture(MultipartFile multipartFile){
        return wmMaterialService.uploadPicture(multipartFile);
    }

    @RequestMapping("/list")
    public ResponseResult findList(@RequestBody WmMaterialDto wmMaterialDto){
        return wmMaterialService.findList(wmMaterialDto);
    }
    @GetMapping("/cancel_collect/{id}")
    public ResponseResult  cancelcollect(@PathVariable("id") int id){
        return wmMaterialService.cancelcollect(id);
    }

    @GetMapping("/collect/{id}")
    public ResponseResult collect(@PathVariable("id") int id) {
        return wmMaterialService.collect(id);
    }
}
