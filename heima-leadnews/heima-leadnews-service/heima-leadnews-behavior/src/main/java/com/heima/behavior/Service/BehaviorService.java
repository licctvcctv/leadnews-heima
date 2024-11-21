package com.heima.behavior.Service;

import com.heima.model.behavior.dtos.LikesBehaviorDto;
import com.heima.model.behavior.dtos.ReadDto;

public interface BehaviorService {
    public void saveLikeBehavior(LikesBehaviorDto Dto);

    public void ReadCount(ReadDto Dto);

    public void noLoveArticle(LikesBehaviorDto dto);
}
