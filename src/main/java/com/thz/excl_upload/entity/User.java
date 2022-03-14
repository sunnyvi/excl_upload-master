package com.thz.excl_upload.entity;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.metadata.BaseRowModel;

import java.io.Serializable;
import java.util.Date;

/**
 * (User)实体类
 *
 * @author makejava
 * @since 2020-07-22 09:57:53
 */
public class User extends BaseRowModel implements Serializable {
    private static final long serialVersionUID = -75075031034829113L;

    @ExcelProperty(value = {"ID"}, index = 0)
    private Integer id;

    @ExcelProperty(value = {"neme"}, index = 1)
    private String name;

    @ExcelProperty(value = {"性别"}, index = 2)
    private String sex;

    @ExcelProperty(value = {"创建时间"}, index = 3)
    private Date createTime;


    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

}