package com.heima.apis.wemedia;

import com.heima.model.common.dtos.ResponseResult;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;

@FeignClient("leadnews-wemedia")
public interface IwemediaClient {

    @PostMapping("/api/v1/wemedia/getlist")
    public ResponseResult getChannelList();
}
