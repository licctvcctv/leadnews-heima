package com.heima.model.wemedia.pojos;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName("wm_channel")
public class Channel {
    @TableId(value = "id",type = IdType.AUTO)
    private int id;

    @TableField("description")
    private String description;

    @TableField("name")
    private String name;

    @TableField("ord")
    private Integer ord;

    @TableField("status")
    private Boolean status;


    @TableField("created_time")
    private Date createdTime;
}
