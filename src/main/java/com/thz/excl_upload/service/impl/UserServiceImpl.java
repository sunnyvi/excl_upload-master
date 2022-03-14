package com.thz.excl_upload.service.impl;


import com.alibaba.excel.ExcelReader;
import com.alibaba.excel.metadata.Sheet;
import com.thz.excl_upload.dao.UserDao;
import com.thz.excl_upload.dao.UserUpLoadDao;
import com.thz.excl_upload.entity.User;
import com.thz.excl_upload.entity.UserUpLoad;
import com.thz.excl_upload.listener.ExcelListener;
import com.thz.excl_upload.service.UserService;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * (User)表服务实现类
 *
 * @author makejava
 * @since 2020-07-22 09:57:54
 */
@Service("userService")
public class UserServiceImpl implements UserService {
    @Resource
    private UserDao userDao;
    @Resource
    private UserUpLoadDao userUpLoadDao;

    /**
     * 通过ID查询单条数据
     *
     * @param id 主键
     * @return 实例对象
     */
    @Override
    public User queryById(Integer id) {
        return this.userDao.queryById(id);
    }

    /**
     * 查询多条数据
     *
     * @param offset 查询起始位置
     * @param limit  查询条数
     * @return 对象列表
     */
    @Override
    public List<User> queryAllByLimit(int offset, int limit) {
        return this.userDao.queryAllByLimit(offset, limit);
    }

    /**
     * 新增数据
     *
     * @param user 实例对象
     * @return 实例对象
     */
    @Override
    public User insert(User user) {
        this.userDao.insert(user);
        return user;
    }

    /**
     * 修改数据
     *
     * @param user 实例对象
     * @return 实例对象
     */
    @Override
    public User update(User user) {
        this.userDao.update(user);
        return this.queryById(user.getId());
    }

    /**
     * 通过主键删除数据
     *
     * @param id 主键
     * @return 是否成功
     */
    @Override
    public boolean deleteById(Integer id) {
        return this.userDao.deleteById(id) > 0;
    }

    @Override
    public List<User> queryAll(User user) {
        return this.userDao.queryAll(user);
    }


    /**
     * 读取excl并插入到数据中
     * @param file
     * @return
     */
    @Override
    public Map<String,Object> uploadExcl(MultipartFile file) {
        Map<String,Object> ruslt = new HashMap<>();
        try {
            String fileName = file.getOriginalFilename();
            //判断文件格式并获取工作簿
            Workbook workbook;
            if(fileName.endsWith("xls")){
                workbook = new HSSFWorkbook(file.getInputStream());
            }else if(fileName.endsWith("xlsx")){
                workbook = new XSSFWorkbook(file.getInputStream());
            } else {
                ruslt.put("code","1");
                ruslt.put("message","文件格式非excl");
                return ruslt;
            }
            //判断第一页不为空
            if(null != workbook.getSheetAt(0)){
                //读取excl第二行，从1开始
                for(int rowNumofSheet = 1;rowNumofSheet <=workbook.getSheetAt(0).getLastRowNum();rowNumofSheet++){
                    if (null != workbook.getSheetAt(0).getRow(rowNumofSheet)) {
                        //定义行，并赋值
                        Row aRow = workbook.getSheetAt(0).getRow(rowNumofSheet);
                        User user = new User();
                        System.out.println(aRow.getLastCellNum());
                        for(int cellNumofRow=0;cellNumofRow<aRow.getLastCellNum();cellNumofRow++){
                            //读取rowNumOfSheet值所对应行的数据
                            //获得行的列数
                            Cell xCell = aRow.getCell(cellNumofRow);
                            Object cell_val;
                            if(cellNumofRow == 0){
                                if(xCell != null && !xCell.toString().trim().isEmpty()){
                                    cell_val = xCell.getStringCellValue();
                                    if(cell_val != null){
                                        String temp = (String)cell_val;
                                        user.setName(temp);
                                    }
                                }
                            }
                            if(cellNumofRow == 1){
                                if(xCell != null && !xCell.toString().trim().isEmpty()){
                                    cell_val = xCell.getStringCellValue();
                                    if(cell_val != null){
                                        String temp = (String)cell_val;
                                        if("男".equals(temp)){
                                            user.setSex("1");
                                        } else {
                                            user.setSex("0");
                                        }
                                        user.setCreateTime(new Date());
                                        userDao.insert(user);
                                    }
                                }
                            }
                        }

                    }
                }
                ruslt.put("code","0");
                ruslt.put("message","成功插入数据库！");
            }else {
                ruslt.put("code","1");
                ruslt.put("message","第一页EXCL无数据！");
            }
        }catch (Exception e){
            e.printStackTrace();
            ruslt.put("code","1");
            ruslt.put("message",e.getMessage());
        }
        return ruslt;
    }

    @Override
    public void saveUser(MultipartFile file) throws IOException {
        if(!file.getOriginalFilename().equals("上传测试.xls") && !file.getOriginalFilename().equals("上传测试.xlsx") ){
            return;
        }
        InputStream inputStream = new BufferedInputStream(file.getInputStream());
        //实例化实现了AnalysisEventListener接口的类
        ExcelListener excelListener = new ExcelListener(userUpLoadDao);
        ExcelReader reader = new ExcelReader(inputStream,null,excelListener);
        //读取信息
        reader.read(new Sheet(1,1,UserUpLoad.class));
    }

}