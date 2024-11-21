package com.heima.user.controller.v1;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.heima.model.common.dtos.ResponseResult;
import com.heima.model.common.enums.AppHttpCodeEnum;
import com.heima.model.user.dtos.UserDto;
import com.heima.model.user.dtos.UserMsgDto;
import com.heima.model.user.pojos.ApUserShimin;
import com.heima.user.mapper.ApUserSMMapper;
import com.heima.user.service.ApUserService;
import com.heima.user.service.ApUserSmService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/auth")
@Slf4j
public class ApUserContronller {

    @Autowired
    private ApUserSmService apUserSmService;
    @PostMapping("/list")
    public ResponseResult getUserList(@RequestBody UserDto userDto){
        // 获取分页参数和查询条件
        int page = userDto.getPage();
        int size = userDto.getSize();
        Integer status = userDto.getStatus();

        log.info(userDto.toString());
        // 构建查询条件
        QueryWrapper<ApUserShimin> queryWrapper = new QueryWrapper<>();
        if (status != null ) {
            queryWrapper.eq("status", status); // 按照 status 筛选
        }

        // 分页查询
        Page<ApUserShimin> pageResult = apUserSmService.page(
                new Page<>(page, size), // 分页参数
                queryWrapper           // 查询条件
        );
        List<ApUserShimin> records = pageResult.getRecords();

        // 返回分页结果
        return ResponseResult.okResult(records);
    }

    @PostMapping("/authPass")
    public ResponseResult authPass(@RequestBody UserMsgDto Dto){
        if (Dto == null || Dto.getId() == null) return ResponseResult.errorResult(AppHttpCodeEnum.PARAM_INVALID);

        QueryWrapper<ApUserShimin> QueryWrapper = new QueryWrapper<>();
        QueryWrapper.eq("id",Dto.getId());
        ApUserShimin one = apUserSmService.getOne(QueryWrapper);
        one.setStatus(9);

        apUserSmService.updateById(one);
        return ResponseResult.okResult(AppHttpCodeEnum.SUCCESS);
    }

    @PostMapping("/authFail")
    public ResponseResult authFail(@RequestBody UserMsgDto Dto){
        if (Dto == null || Dto.getId() == null) return ResponseResult.errorResult(AppHttpCodeEnum.PARAM_INVALID);

        QueryWrapper<ApUserShimin> QueryWrapper = new QueryWrapper<>();
        QueryWrapper.eq("id",Dto.getId());
        ApUserShimin one = apUserSmService.getOne(QueryWrapper);
        one.setStatus(2);

        apUserSmService.updateById(one);
        return ResponseResult.okResult(AppHttpCodeEnum.SUCCESS);
    }
}
