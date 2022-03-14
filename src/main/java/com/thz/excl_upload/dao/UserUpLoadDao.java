package com.thz.excl_upload.dao;


import com.thz.excl_upload.entity.UserUpLoad;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Component;



/**
 * (UserUpLoad)表数据库访问层
 *
 * @author makejava
 * @since 2020-07-27 09:57:53
 */
@Mapper
@Component
public interface UserUpLoadDao {

    /**
     * 新增数据
     *
     * @param user 实例对象
     * @return 影响行数
     */
    int insert(UserUpLoad user);

}