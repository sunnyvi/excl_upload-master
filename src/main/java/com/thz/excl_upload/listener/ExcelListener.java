package com.thz.excl_upload.listener;

import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import com.thz.excl_upload.dao.UserUpLoadDao;
import com.thz.excl_upload.entity.UserUpLoad;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ExcelListener extends AnalysisEventListener<UserUpLoad> {
    private List<UserUpLoad> datas = new ArrayList<>();
    private static final int BATCH_COUNT = 3000;
    private UserUpLoadDao userUpLoadDao;

    public ExcelListener(UserUpLoadDao userUpLoadDao){
        this.userUpLoadDao = userUpLoadDao;
    }

    @Override
    public void invoke(UserUpLoad user, AnalysisContext analysisContext) {
        //数据存储到datas，供批量处理，或后续自己业务逻辑处理。
        datas.add(user);
        //达到BATCH_COUNT了，需要去存储一次数据库，防止数据几万条数据在内存，容易OOM
        if(datas.size() >= BATCH_COUNT){
            saveData();
            // 存储完成清理datas
            datas.clear();
        }
    }

    private void saveData() {
        for(UserUpLoad user : datas){
            user.setCreateTime(new Date());
            this.userUpLoadDao.insert(user);
        }
    }

    public List<UserUpLoad> getDatas() {
        return datas;
    }

    public void setDatas(List<UserUpLoad> datas) {
        this.datas = datas;
    }

    /**
     * 所有数据解析完成了 都会来调用
     */
    @Override
    public void doAfterAllAnalysed(AnalysisContext analysisContext) {
        saveData();//确保所有数据都能入库
    }
}


