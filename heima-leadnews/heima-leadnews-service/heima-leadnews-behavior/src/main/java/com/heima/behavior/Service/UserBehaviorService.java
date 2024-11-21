package com.heima.behavior.Service;

import com.heima.model.behavior.dtos.CollectionBehaviorDto;
import com.heima.model.behavior.dtos.LikesBehaviorDto;
import com.heima.model.behavior.dtos.ReadDto;
import com.heima.model.behavior.dtos.UserFollowDto;
import com.heima.model.common.enums.UserRedisKeyEnum;
import com.heima.utils.thread.WmThreadLocalUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
public class UserBehaviorService {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    // 使用 Redis Set 存储用户的点赞记录
    public void saveLikeBehavior(int userId, Long articleId, Short isLiked) {
        String setKey = UserRedisKeyEnum.USERLOVE.getValus() + userId;  // Redis Set 键是 "user:likes:userId"
        
        if (isLiked == 0) {
            // 将用户的文章 ID 添加到 Set 中
            redisTemplate.opsForSet().add(setKey, articleId);
            // 设置过期时间，例如 1 小时
            //redisTemplate.expire(setKey, 1, TimeUnit.HOURS);
        } else {
            // 取消点赞，移除 Redis Set 中的文章 ID
            redisTemplate.opsForSet().remove(setKey, articleId);
        }
    }


    public void ReadCount(ReadDto Dto){
        String setKey = UserRedisKeyEnum.USERREADCOUNT.getValus() + WmThreadLocalUtils.getUser().getId();
        redisTemplate.opsForSet().add(setKey, Dto.getArticleId() + ":count" + Dto.getCount());
    }


    public void noLoveArticle(LikesBehaviorDto dto) {
        String setKey = UserRedisKeyEnum.USERNOLOVE.getValus() + WmThreadLocalUtils.getUser().getId();

        if (dto.getType() == 0){
            redisTemplate.opsForSet().add(setKey,dto.getArticleId());
        }else {
            redisTemplate.opsForSet().remove(setKey,dto.getArticleId());
        }
    }
}
