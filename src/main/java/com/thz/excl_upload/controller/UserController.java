package com.thz.excl_upload.controller;

import com.alibaba.excel.EasyExcelFactory;
import com.alibaba.excel.ExcelWriter;
import com.alibaba.excel.metadata.Sheet;
import com.thz.excl_upload.entity.User;
import com.thz.excl_upload.service.UserService;
import org.apache.poi.hssf.usermodel.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;


import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * (User)表控制层
 *
 * @author makejava
 * @since 2020-07-22 09:57:55
 */
@RestController
@RequestMapping("user")
public class UserController {
    /**
     * 服务对象
     */
    @Resource
    private UserService userService;

    /**
     * 通过主键查询单条数据
     *
     * @param id 主键
     * @return 单条数据
     */
    @GetMapping("selectOne")
    public User selectOne(Integer id) {
        return this.userService.queryById(id);
    }


    @RequestMapping(value = "/uploadExcl")
    public @ResponseBody
    Map<String ,Object> uploadExcl(HttpServletRequest request, @RequestParam("file") MultipartFile file){
        Map<String ,Object> result = new HashMap<>();
        String path = request.getSession().getServletContext().getRealPath("/");
        try{
            // 如果文件不为空，写入上传路径
            if(!file.isEmpty()){
                result = userService.uploadExcl(file);
            }else {
                result.put("code","1");
                result.put("message","上传文件为空！");
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        if (result.get("code").equals("0")){
            //根据时间戳创建新的文件名，这样即便是第二次上传相同名称的文件，也不会把第一次的文件覆盖了
            String fileName = System.currentTimeMillis() + file.getOriginalFilename();
            //通过req.getServletContext().getRealPath("") 获取当前项目的真实路径，然后拼接前面的文件名
//            String destFileName = request.getContextPath()+ "uploaded" + File.separator + fileName;
            String destFileName = request.getServletContext().getRealPath("") + "uploaded" + File.separator + fileName;
            System.out.println(request.getServletPath());
            System.out.println(request.getServletContext());
            System.out.println(request.getServletContext().getRealPath(""));
            System.out.println(request.getServletContext().getRealPath("/"));
            System.out.println(request.getContextPath());
            System.out.println(destFileName);
            //第一次运行的时候，这个文件所在的目录往往是不存在的，这里需要创建一下目录
            File destFile = new File(destFileName);
            destFile.getParentFile().mkdirs();
            System.out.println(destFile);
            //把浏览器上传的文件复制到希望的位置
            try {
                file.transferTo(destFile);
            } catch (IOException e) {
                e.printStackTrace();
            }
            System.out.println(fileName);
        }
        return result;
    }


    /**
     * 下载数据库数据为exles
     *
     * @param response
     * @throws IOException
     */
    @RequestMapping(value = "UserExcelDownloads", method = RequestMethod.GET)
    public void downloadAllClassmate(HttpServletResponse response) throws IOException {
        HSSFWorkbook workbook = new HSSFWorkbook();
        HSSFSheet sheet = workbook.createSheet("信息表");

        List<User> userList = userService.queryAll(new User());

        String fileName = "userinf"  + ".xls";//设置要导出的文件的名字
        //新增数据行，并且设置单元格数据

        int rowNum = 1;

        String[] headers = { "id", "name", "sex", "create_time"};
        //headers表示excel表中第一行的表头

        HSSFRow row = sheet.createRow(0);
        //在excel表中添加表头

        for(int i=0;i<headers.length;i++){
            HSSFCell cell = row.createCell(i);
            HSSFRichTextString text = new HSSFRichTextString(headers[i]);
            cell.setCellValue(text);
        }

        //在表中存放查询到的数据放入对应的列
        for (User user : userList) {
            HSSFRow row1 = sheet.createRow(rowNum);
            row1.createCell(0).setCellValue(user.getId());
            row1.createCell(1).setCellValue(user.getName());
            row1.createCell(2).setCellValue(user.getSex());
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String dateString = formatter.format(user.getCreateTime());
            row1.createCell(3).setCellValue(dateString);
            System.out.println(dateString);
            rowNum++;
        }

        response.setContentType("application/octet-stream");
        response.setHeader("Content-disposition", "attachment;filename=" + fileName);
        response.flushBuffer();
        workbook.write(response.getOutputStream());
    }


    //UserExcelDownloadsEasyExcel
    @RequestMapping("UserExcelDownloadsEasyExcel")
    public void UserExcelDownloadsEasyExcel(HttpServletResponse response) throws IOException {
        ExcelWriter writer = EasyExcelFactory.getWriter(response.getOutputStream());
        // 写仅有一个 Sheet 的 Excel 文件, 此场景较为通用
        Sheet sheet1 = new Sheet(1, 0, User.class);

        // 第一个 sheet 名称
        sheet1.setSheetName("第一个sheet");

        // 写数据到 Writer 上下文中
        // 入参1: 数据库查询的数据list集合
        // 入参2: 要写入的目标 sheet
        writer.write(userService.queryAll(new User()), sheet1);

        // 将上下文中的最终 outputStream 写入到指定文件中
        response.setContentType("application/octet-stream");
        String fileName = "userinf"  + ".xls";
        response.setHeader("Content-disposition", "attachment;filename=" + fileName);
        response.flushBuffer();
        writer.finish();
    }



    @RequestMapping(value = "/uploadEasyExcl")
    public @ResponseBody
    Map<String ,Object> uploadEasyExcl(HttpServletRequest request, @RequestParam("file") MultipartFile file) throws IOException {
        Map<String ,Object> result = new HashMap<>();
        userService.saveUser(file);
        return result;
    }



}