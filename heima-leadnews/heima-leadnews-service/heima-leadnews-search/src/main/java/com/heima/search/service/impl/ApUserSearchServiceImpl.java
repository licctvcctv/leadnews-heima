package com.heima.search.service.impl;

import com.heima.model.common.dtos.ResponseResult;
import com.heima.model.common.enums.AppHttpCodeEnum;
import com.heima.model.search.dtos.HistorySearchDto;
import com.heima.model.user.pojos.ApUser;
import com.heima.search.pojos.ApUserSearch;
import com.heima.search.service.ApUserSearchService;
import com.heima.utils.thread.AppThreadLocalUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
@Slf4j
public class ApUserSearchServiceImpl implements ApUserSearchService {

    @Autowired
    private MongoTemplate mongoTemplate;
    @Async
    @Override
    public void insert(String keyword, Integer userId) {
        //先找有没有这个数据
        Query query = Query.query(Criteria.where("userId").is(userId).where("keyword").is(keyword));

        ApUserSearch one = mongoTemplate.findOne(query, ApUserSearch.class);

        if (one != null){
            one.setCreatedTime(new Date());
            mongoTemplate.save(one);
            return;
        }

        one = new ApUserSearch();
        one.setUserId(userId);
        one.setKeyword(keyword);
        one.setCreatedTime(new Date());

        Query userId1 = Query.query(Criteria.where("userId").is(userId));
        userId1.with(Sort.by(Sort.Direction.DESC,"createdTime"));
        List<ApUserSearch> apUserSearchList = mongoTemplate.find(userId1, ApUserSearch.class);


        if(apUserSearchList == null || apUserSearchList.size() < 10){
            mongoTemplate.save(one);
        }else {
            ApUserSearch lastUserSearch = apUserSearchList.get(apUserSearchList.size() - 1);
            mongoTemplate.findAndReplace(Query.query(Criteria.where("id").is(lastUserSearch.getId())),one);
        }
    }

    @Override
    public ResponseResult findUserSearch() {
        ApUser user = AppThreadLocalUtils.getUser();
        if (user == null){
            return ResponseResult.errorResult(AppHttpCodeEnum.NEED_LOGIN);
        }

        List<ApUserSearch> apUserSearchList = mongoTemplate.find(Query.query(Criteria.where("userId").is(user.getId())).with(Sort.by(Sort.Direction.DESC, "createdTime")), ApUserSearch.class);

        return ResponseResult.okResult(apUserSearchList);
    }

    @Override
    public ResponseResult delUserSearch(HistorySearchDto Dto) {
        if (Dto == null){
            return ResponseResult.errorResult(AppHttpCodeEnum.PARAM_INVALID);
        }


        ApUser user = AppThreadLocalUtils.getUser();

        if (user == null){
            return ResponseResult.errorResult(AppHttpCodeEnum.NEED_LOGIN);
        }
        Query query = Query.query(Criteria.where("userId").is(user.getId()).where("id").is(Dto.getId()));

        mongoTemplate.remove(query,ApUserSearch.class);
        return ResponseResult.okResult(AppHttpCodeEnum.SUCCESS);
    }
}