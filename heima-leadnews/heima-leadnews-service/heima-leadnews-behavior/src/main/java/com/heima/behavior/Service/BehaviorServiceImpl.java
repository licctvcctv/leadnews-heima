package com.heima.behavior.Service;

import com.alibaba.fastjson.JSON;
import com.heima.common.constants.BehaviorConstants;
import com.heima.common.constants.HotArticleConstants;
import com.heima.common.constants.UpdateArticleMess;
import com.heima.model.behavior.dtos.LikesBehaviorDto;
import com.heima.model.behavior.dtos.ReadDto;
import com.heima.model.common.enums.UserRedisKeyEnum;
import com.heima.utils.thread.WmThreadLocalUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
@Slf4j
public class BehaviorServiceImpl implements BehaviorService{
    @Autowired
    private KafkaTemplate<String,String> kafkaTemplate;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Override
    public void saveLikeBehavior(LikesBehaviorDto dto) {

        int isLiked = dto.getOperation() == 0 ? 1 : -1 ;
        UpdateArticleMess updateArticleMess = new UpdateArticleMess();
        updateArticleMess.setType(UpdateArticleMess.UpdateArticleType.LIKES);
        updateArticleMess.setArticleId(( dto).getArticleId());
        updateArticleMess.setAdd(isLiked);
        log.info("saveLikeBehavior" + updateArticleMess);

        Integer userId = WmThreadLocalUtils.getUser().getId();
        String setKey = UserRedisKeyEnum.USERLOVE.getValus() + userId;  // Redis Set 键是 "user:likes:userId"
        Long articleId = dto.getArticleId();
        if (isLiked == 1) {
            // 将用户的文章 ID 添加到 Set 中
            redisTemplate.opsForSet().add(setKey, articleId);
            // 设置过期时间，例如 1 小时
            //redisTemplate.expire(setKey, 1, TimeUnit.HOURS);
        } else {
            // 取消点赞，移除 Redis Set 中的文章 ID
            redisTemplate.opsForSet().remove(setKey, articleId);
        }

        kafkaTemplate.send(HotArticleConstants.HOT_ARTICLE_SCORE_TOPIC,JSON.toJSONString(updateArticleMess));
    }


    @Override
    public void ReadCount(ReadDto dto) {

        UpdateArticleMess updateArticleMess = new UpdateArticleMess();
        updateArticleMess.setType(UpdateArticleMess.UpdateArticleType.VIEWS);
        updateArticleMess.setArticleId((dto).getArticleId());
        updateArticleMess.setAdd(1);
        Integer userId = WmThreadLocalUtils.getUser().getId();
        Long articleId = dto.getArticleId();
        String setKey = UserRedisKeyEnum.USERREADCOUNT.getValus() + articleId.toString();

        redisTemplate.opsForValue().increment(setKey, 1);
        log.info("ReadCount" + updateArticleMess);
        // 设置过期时间，例如 1 小时
            //redisTemplate.expire(setKey, 1, TimeUnit.HOURS);
        kafkaTemplate.send(HotArticleConstants.HOT_ARTICLE_SCORE_TOPIC,JSON.toJSONString(updateArticleMess));
    }


    @Override
    public void noLoveArticle(LikesBehaviorDto dto) {
        String setKey = UserRedisKeyEnum.USERNOLOVE.getValus() + WmThreadLocalUtils.getUser().getId();

        if (dto.getType() == 0){
            redisTemplate.opsForSet().add(setKey,dto.getArticleId());
        }else {
            redisTemplate.opsForSet().remove(setKey,dto.getArticleId());
        }
    }

//    public void sendToKafka(String type, Object dto){
//
//        if (StringUtils.isNotBlank(type)) return;
//        if (type.equals(LikesBehaviorDto.class.getName())){
//            LikesBehaviorDto dto1 = (LikesBehaviorDto) dto;
//            int isLiked = dto1.getOperation() == 0 ? 1 : -1 ;
//            UpdateArticleMess updateArticleMess = new UpdateArticleMess();
//            updateArticleMess.setType(UpdateArticleMess.UpdateArticleType.LIKES);
//            updateArticleMess.setArticleId(((LikesBehaviorDto) dto).getArticleId());
//            updateArticleMess.setAdd(isLiked);
//
//            Integer userId = WmThreadLocalUtils.getUser().getId();
//            String setKey = UserRedisKeyEnum.USERLOVE.getValus() + userId;  // Redis Set 键是 "user:likes:userId"
//            Long articleId = dto1.getArticleId();
//            if (isLiked == 0) {
//                // 将用户的文章 ID 添加到 Set 中
//                redisTemplate.opsForSet().add(setKey, articleId);
//                // 设置过期时间，例如 1 小时
//                //redisTemplate.expire(setKey, 1, TimeUnit.HOURS);
//            } else {
//                // 取消点赞，移除 Redis Set 中的文章 ID
//                redisTemplate.opsForSet().remove(setKey, articleId);
//            }
//            kafkaTemplate.send(BehaviorConstants.Hot_Article_KafkaStream_topic,JSON.toJSONString(updateArticleMess));
//        }else {
//
//        }
//
//
//
//        Map<String, Object> objectObjectMap = new HashMap<>();
//        Integer userId = WmThreadLocalUtils.getUser().getId();
//        if (userId == null) {
//            log.warn("User ID is null, cannot send message to Kafka");
//            return;
//        }
//
//        objectObjectMap.put("userid", userId);
//        objectObjectMap.put("type",type);
//        objectObjectMap.put("value",dto);
//        String jsonString = JSON.toJSONString(objectObjectMap);
//
//    }
}
