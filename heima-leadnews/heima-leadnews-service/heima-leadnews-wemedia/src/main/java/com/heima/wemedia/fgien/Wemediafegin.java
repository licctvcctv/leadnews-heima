package com.heima.wemedia.fgien;

import com.heima.apis.wemedia.IwemediaClient;
import com.heima.model.common.dtos.ResponseResult;
import com.heima.wemedia.service.WmChannelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class Wemediafegin implements IwemediaClient {
    @Autowired
    private WmChannelService wmChannelService;

    @PostMapping("/api/v1/wemedia/getlist")
    @Override
    public ResponseResult getChannelList() {
        return ResponseResult.okResult(wmChannelService.list());
    }
}
