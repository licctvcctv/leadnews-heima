package com.heima.user.service.impl;

import com.heima.common.constants.UpdateArticleMess;
import com.heima.model.behavior.dtos.UserFollowDto;
import com.heima.model.common.enums.UserRedisKeyEnum;
import com.heima.user.service.ApUserFollow;
import com.heima.utils.thread.AppThreadLocalUtils;
import com.heima.utils.thread.WmThreadLocalUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class ApUserFollowimpl implements ApUserFollow {

    @Autowired
    private RedisTemplate<String,Object> redisTemplate;
    @Autowired
    private KafkaTemplate kafkaTemplate;
    @Override
    public void UserFollow(UserFollowDto dto){
        log.info(dto.toString());
        String setKey = UserRedisKeyEnum.USERFOLLOW.getValus() + AppThreadLocalUtils.getUser().getId();



        if (dto.getOperation() == 0){
            redisTemplate.opsForSet().add(setKey,dto.getAuthorId());

        }else {
            redisTemplate.opsForSet().remove(setKey,dto.getAuthorId());
        }

    }
}
