package com.heima.wemedia.controller.v1;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.heima.model.common.dtos.ResponseResult;
import com.heima.model.common.enums.AppHttpCodeEnum;
import com.heima.model.user.dtos.PdDto;
import com.heima.model.user.dtos.UserDto;
import com.heima.model.wemedia.pojos.ChannelDto;
import com.heima.model.wemedia.pojos.WmChannel;

import com.heima.wemedia.mapper.WmChannelMapper;
import com.heima.wemedia.service.WmChannelService;
import com.heima.wemedia.service.WmNewsService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/api/v1/channel")
public class WmchannelController {

    @Autowired
    private WmChannelService wmChannelService;

    @Autowired
    private WmNewsService wmNewsService;

    @Autowired
    private WmChannelService wmChannelMapper;



    /**
     * 分页查询频道列表
     */
    @PostMapping("/list")
    public ResponseResult getAllChannels(@RequestBody PdDto Dto) {
        int page = Dto.getPage(); // 当前页
        int size = Dto.getSize(); // 每页显示条数
        String name = Dto.getName();

        // 创建 Page 对象，指定当前页和每页大小
        Page<WmChannel> channelPage = new Page<>(page, size);

        QueryWrapper<WmChannel> objectQueryWrapper = new QueryWrapper<>();
        if (StringUtils.isNotBlank(name)){
            objectQueryWrapper.like("name", name);

        }

        // 使用 selectPage 方法进行分页查询
        // 查询所有记录，传入空的 QueryWrapper 表示没有查询条件
        wmChannelMapper.page(channelPage, objectQueryWrapper);

        // 获取分页结果的记录
        List<WmChannel> wmChannels = channelPage.getRecords();

        // 返回分页结果，包括分页数据和分页信息
        // 返回的 List 类型会被包装成统一的 ResponseResult 格式
        return ResponseResult.okResult(wmChannels);
    }

    @PostMapping("/save")
    public ResponseResult save(@RequestBody ChannelDto Dto){

        if (Dto == null || !StringUtils.isNotBlank(Dto.getDescription())
            || !StringUtils.isNotBlank(Dto.getName())){
            return ResponseResult.errorResult(AppHttpCodeEnum.PARAM_INVALID);
        }
        WmChannel wmChannel = new WmChannel();
        BeanUtils.copyProperties(Dto,wmChannel);

        wmChannel.setIsDefault(true);
        wmChannel.setCreatedTime(new Date());
        wmChannelService.save(wmChannel);

        return ResponseResult.okResult(AppHttpCodeEnum.SUCCESS);

    }

    @PostMapping("/update")
    public ResponseResult update(@RequestBody ChannelDto Dto){
        if (Dto == null || Dto.getId() == null) return ResponseResult.errorResult(AppHttpCodeEnum.PARAM_INVALID);

        WmChannel wmChannel = wmChannelService.getById(Dto.getId());

        BeanUtils.copyProperties(Dto,wmChannel);
        wmChannelService.updateById(wmChannel);

        return ResponseResult.okResult(AppHttpCodeEnum.SUCCESS);

    }

    @GetMapping("/del/{id}")
    public ResponseResult del(@PathVariable Integer id){
        if (id == null) return ResponseResult.errorResult(AppHttpCodeEnum.PARAM_INVALID);
        wmChannelService.removeById(id);
        return ResponseResult.okResult(AppHttpCodeEnum.SUCCESS);
    }

    @GetMapping("/channels")
    public ResponseResult channelsList(){
        List<WmChannel> list = wmChannelService.list();
        return ResponseResult.okResult(list);
    }

}



