package com.zbn.springbootinit.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

/**
 * 有声PPT
 *
 * @TableName AuditoryPPT
 */
@TableName(value = "AuditoryPPT")
@Data
public class Auditoryppt {
    /**
     * id
     */
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    private String duration;
    /**
     * 文件名
     */
    private String name;

    /**
     * 存储地址
     */
    private String filePath;

    /**
     * 文件大小（字节）
     */
    private Long fileSize;

    /**
     * 文件类型
     */
    private String fileType;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 个人中心ID
     */
    private Long spaceId;

    /**
     * 音色ID
     */
    private Long audioId;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;

    /**
     * 是否删除
     */
    @TableLogic
    private Integer isDelete;
}