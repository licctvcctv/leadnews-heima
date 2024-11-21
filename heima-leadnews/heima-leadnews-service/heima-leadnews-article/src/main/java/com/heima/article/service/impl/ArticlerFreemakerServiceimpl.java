package com.heima.article.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;

import com.heima.article.mapper.ApArticleContentMapper;
import com.heima.article.service.ApArticleService;
import com.heima.article.service.ArticlerFreemakerService;
import com.heima.common.constants.ArticleConstants;
import com.heima.common.constants.HotArticleConstants;
import com.heima.common.constants.UpdateArticleMess;
import com.heima.common.mess.ArticleVisitStreamMess;
import com.heima.file.service.FileStorageService;
import com.heima.model.article.pojos.ApArticle;
import com.heima.model.article.pojos.ApArticleHot;
import com.heima.model.behavior.dtos.CollectionBehaviorDto;
import com.heima.model.behavior.dtos.UserFollowDto;
import com.heima.model.common.dtos.ResponseResult;
import com.heima.model.common.enums.UserRedisKeyEnum;
import com.heima.model.search.vos.SearchArticleVo;
import com.heima.utils.thread.AppThreadLocalUtils;
import freemarker.template.Configuration;
import freemarker.template.Template;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;


@Service
@Transactional
@Slf4j
public class ArticlerFreemakerServiceimpl implements ArticlerFreemakerService {
    @Autowired
    private ApArticleContentMapper apArticleContentMapper;

    @Autowired
    private Configuration configuration;

    @Autowired
    private FileStorageService fileStorageService;

    @Autowired
    private ApArticleService apArticleService;

    /**
     * 生成静态文件上传到minIO中
     * @param apArticle
     * @param content
     */
    @Async
    @Override
    public void buildArticleToMinIO(ApArticle apArticle, String content) {
        //已知文章的id
        //4.1 获取文章内容
        if(StringUtils.isNotBlank(content)){
            //4.2 文章内容通过freemarker生成html文件
            Template template = null;
            StringWriter out = new StringWriter();
            try {
                template = configuration.getTemplate("article.ftl");
                //数据模型
                Map<String,Object> contentDataModel = new HashMap<>();
                contentDataModel.put("content", JSONArray.parseArray(content));
                //合成
                template.process(contentDataModel,out);
            } catch (Exception e) {
                e.printStackTrace();
            }

            //4.3 把html文件上传到minio中
            InputStream in = new ByteArrayInputStream(out.toString().getBytes());
            String path = fileStorageService.uploadHtmlFile("", apArticle.getId() + ".html", in);


            //4.4 修改ap_article表，保存static_url字段
            apArticleService.update(Wrappers.<ApArticle>lambdaUpdate().eq(ApArticle::getId,apArticle.getId())
                    .set(ApArticle::getStaticUrl,path));

            createArticleEsIndex(apArticle ,content,path);

        }
    }

    @Override
    public ResponseResult getArticleList(UserFollowDto dto) {
        Integer id = AppThreadLocalUtils.getUser().getId();
        Boolean isfollow = redisTemplate.opsForSet().isMember(UserRedisKeyEnum.USERFOLLOW.getValus() + id, dto.getAuthorId());
        Boolean islike = redisTemplate.opsForSet().isMember(UserRedisKeyEnum.USERLOVE.getValus() + id, dto.getArticleId());
        Boolean isunlike= redisTemplate.opsForSet().isMember(UserRedisKeyEnum.USERNOLOVE.getValus() + id, dto.getArticleId());
        Boolean iscollection= redisTemplate.opsForSet().isMember(UserRedisKeyEnum.USERCOLLECTION.getValus() + id, dto.getArticleId());

        HashMap<String, Boolean> map = new HashMap<>();
        map.put("isfollow",isfollow);
        map.put("islike",islike);
        map.put("isunlike",isunlike);
        map.put("iscollection",iscollection);


        return ResponseResult.okResult(map);
    }

    @Override
    public void UserCollection(CollectionBehaviorDto dto) {
        log.info(dto.toString());
        UpdateArticleMess mess = new UpdateArticleMess();
        mess.setType(UpdateArticleMess.UpdateArticleType.COLLECTION);
        mess.setArticleId(dto.getEntryId());
        if (dto.getOperation() == 0){
            redisTemplate.opsForSet().add(UserRedisKeyEnum.USERCOLLECTION.getValus() + AppThreadLocalUtils.getUser().getId(),dto.getEntryId());
            mess.setAdd(1);

        }else {
            redisTemplate.opsForSet().remove(UserRedisKeyEnum.USERCOLLECTION.getValus() + AppThreadLocalUtils.getUser().getId(),dto.getEntryId());
            mess.setAdd(-1);
        }
        kafkaTemplate.send(HotArticleConstants.HOT_ARTICLE_SCORE_TOPIC,JSON.toJSONString(mess));
    }

    @Override

    public void UpdateHotArticle(ArticleVisitStreamMess mess) {
        log.info("UpdateHotArticle" + mess);
        ApArticle article = this.article(mess);
        log.info("即将进行数据库更新" + article.toString());
        int i = this.calculateScope(article);
        i *= 3;
        ApArticleHot apArticleHot = new ApArticleHot();
        BeanUtils.copyProperties(article,apArticleHot);
        apArticleHot.setScope(i);

        Integer channelId = article.getChannelId();
        String Channel = channelId == null ? ArticleConstants.DEFAULT_TAG : channelId.toString();
        Object o = redisTemplate.opsForValue().get(ArticleConstants.HOT_ARTICLE_FIRST_PAGE + article.getChannelId());

        try {
            if (o == null){
                log.info("没有找到热门文章");
                return;
            }
        }catch (Exception e){

        }

        List<ApArticleHot> apArticles = JSONArray.parseArray(o.toString(), ApArticleHot.class);
        Boolean flag = true;
        for (ApArticleHot apArticle : apArticles) {
            if (article.getId() == apArticle.getId()){

                apArticle.setScope(i);
                flag = false;
                
            }
        }

        if (apArticles.size() < 30 && flag){
            apArticles.add(apArticleHot);
            apArticles = apArticles.stream().sorted((a, b) -> Long.compare(b.getScope(), a.getScope())).collect(Collectors.toList());
            redisTemplate.opsForValue().set(ArticleConstants.HOT_ARTICLE_FIRST_PAGE + Channel ,JSON.toJSONString(apArticles));
            return;
        }
        apArticles = apArticles.stream().sorted((a, b) -> Long.compare(b.getScope(), a.getScope())).collect(Collectors.toList());

        if (apArticles.get(apArticles.size()-1).getScope() <= apArticleHot.getScope() && flag){
            apArticles.remove(apArticles.size()-1);
            apArticles.add(apArticleHot);
            apArticles.stream().sorted((a,b) -> Long.compare(b.getScope(),a.getScope()));

        }
        redisTemplate.opsForValue().set(ArticleConstants.HOT_ARTICLE_FIRST_PAGE + Channel ,JSON.toJSONString(apArticles));


    }

    private int calculateScope(ApArticle apArticle) {
        // 使用三元运算符进行 null 检查，如果为 null 则用 0 替代
        int likes = (apArticle.getLikes() != null) ? apArticle.getLikes() : 0;
        int comment = (apArticle.getComment() != null) ? apArticle.getComment() : 0;
        int collection = (apArticle.getCollection() != null) ? apArticle.getCollection() : 0;

        return likes * ArticleConstants.HOT_ARTICLE_LIKE_WEIGHT
                + comment * ArticleConstants.HOT_ARTICLE_COMMENT_WEIGHT
                + collection * ArticleConstants.HOT_ARTICLE_COLLECTION_WEIGHT;
    }
    private ApArticle article(ArticleVisitStreamMess mess){
        Long articleId = mess.getArticleId();
        ApArticle byId = apArticleService.getById(articleId);

        if (byId != null && mess != null) {
            // 使用 Optional.ofNullable 避免可能的空指针异常
            byId.setCollection(Optional.ofNullable(byId.getCollection()).orElse(0)
                    + Optional.ofNullable(mess.getCollect()).orElse(0));

            byId.setViews(Optional.ofNullable(byId.getViews()).orElse(0)
                    + Optional.ofNullable(mess.getView()).orElse(0));

            byId.setComment(Optional.ofNullable(byId.getComment()).orElse(0)
                    + Optional.ofNullable(mess.getComment()).orElse(0));

            byId.setLikes(Optional.ofNullable(byId.getLikes()).orElse(0)
                    + Optional.ofNullable(mess.getLike()).orElse(0));

            // 执行更新
            apArticleService.updateById(byId);
        } else {
            log.warn("Article or update message is null, cannot update.");
        }

        return byId;
    }

    @Autowired
    private KafkaTemplate<String,String> kafkaTemplate;
    private void createArticleEsIndex(ApArticle apArticle, String content, String path) {

        SearchArticleVo vo = new SearchArticleVo();
        BeanUtils.copyProperties(apArticle,vo);
        vo.setContent(content);
        vo.setStaticUrl(path);

        log.info("发送审核成功文章消息到es" +  vo.toString());
        kafkaTemplate.send(ArticleConstants.ARTICLE_ES_SYNC_TOPIC, JSON.toJSONString(vo));

    }

    @Autowired
    private RedisTemplate<String,Object> redisTemplate;


}