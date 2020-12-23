package com.dfundata.fund.schedule.utils;


import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;

/**
 * @author ly
 * @date 2020-02-24 22:27
 * @description 遍历JSON文件信息
 */
@Component
public class TraverseJson {

    @Autowired
    private HiveDBUtils hiveDBUtils;

    @Value("#{'${hive.params}'.split(',')}")
    private List<String> hiveParams;

    private Logger logger = LoggerFactory.getLogger(TraverseJson.class);

    public void traverseFile(Date date, File file) throws SQLException, IOException{
        if (file.isDirectory()) {
            File[] files = file.listFiles();
            for (File f : files) {
                traverseFile(date, f);
            }
        } else {
            String name = file.getName();
            if (name.endsWith("json")) {
                AnalysisJson(date, file);
            }
        }
    }

    /**
     * 解析JSON
     * @param file
     */
    private  void AnalysisJson(Date date,File file) throws SQLException, IOException {
        String json = FileUtils.readFile(file.getPath());
        JSONObject obj = JSONObject.parseObject(json);
        logger.info("start analysis {} , it is {} " , obj.getString("name"),obj.getString("description"));
        JSONArray content =
                obj.getJSONArray("content");

        for (Object o : content) {
            JSONObject jsonO = (JSONObject)o;
            // 是否加载 sql 文件里面的
            Boolean fromFile = jsonO.getBoolean("fromFile");
            if(fromFile){
                logger.info("{} this sql execute from sqlFile , this sql is {} ",Thread.currentThread().getName(),jsonO.getString("descripton"));
                String fileName = jsonO.getString("fileName");
                String sql = FileUtils.readFile(file.getParent() + "/" + fileName);
                JSONArray args = AnalysisParameters(jsonO.getJSONArray("parameters"),date);
                logger.info("{} this sql parameters  is {} ",Thread.currentThread().getName(),args);
                hiveDBUtils.executeSql(sql, args);
            }else {
                Boolean check = jsonO.getBoolean("check");
                if(check == null || !check){
                    logger.info("{} this sql execute from sqlStr ,this sql is {} ",Thread.currentThread().getName(),jsonO.getString("descripton"));
                    String sql = jsonO.getString("sql");
                    JSONArray args =AnalysisParameters(jsonO.getJSONArray("parameters"),date);
                    logger.info("{} this sql parameters  is {} ",Thread.currentThread().getName(),args);
                    hiveDBUtils.executeSql(sql, args);
                }
            }
        }

    }



    public PreparedStatement getPreparedStatement(Connection conn, JSONArray hiveParamArray) {
        PreparedStatement pstm = null;
        try {
            pstm = conn.prepareStatement("set hive.exec.dynamic.partition=true ");
            pstm.execute();
            pstm.execute("set character.literal.as.string=true ");
            // 默认参数
            for (String hiveParam : hiveParams) {
                if (StringUtils.isNotBlank(hiveParam)) {
                    pstm.execute(hiveParam);
                    logger.info(hiveParam);
                }
            }
            if (hiveParamArray != null && hiveParamArray.size() > 0) {
                // json 文件参数，可以覆盖
                for (int i = 0; i < hiveParamArray.size(); i++) {
                    String hiveParam = hiveParamArray.getString(i);
                    pstm.execute(hiveParam);
                    logger.info(hiveParam);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return pstm;
    }

    private  JSONArray AnalysisParameters(JSONArray args){
        return AnalysisParameters(args,new Date());
    }

    public JSONArray AnalysisParameters(JSONArray args, Date date) {
        if(args != null){
            // 获取上周最大交易日
            Integer lastTradeWeekMaxDate = 0;
            for (int i = 0; i < args.size(); i++) {
                String arg = args.getString(i);
                boolean hasBatchIntDate = arg.contains("batch_int_date");
                boolean hasBefore = arg.contains("BEFORE");
                boolean hasBfM = arg.contains("bfM");
                boolean hasBeforeYearDate = arg.contains("before_year_date");
                boolean hasBatchDate =  arg.contains("batch_date");
                boolean hasLastYearTransDay =  arg.contains("last_year_trans_day");
                boolean hasLastIntYearTransDay =  arg.contains("last_int_year_trans_day");
                boolean hasMaxProfit =  arg.contains("max_profit_d");
                boolean hasMaxStockPosition =  arg.contains("max_stock_position_d");
                // date 就是交易日 如20200911，20200827，20200430
                if(!hasBefore && hasBatchIntDate && !hasBfM){
//                    String date2 = MyDateUtils.getDate(lastTradeWeekMaxDate, "yyyyMMdd");
                    args.set(i,lastTradeWeekMaxDate);
                }
                if(hasMaxProfit){
                    String sqlTemp = "select max(init_date) from temp.tmp_jjtj_t_user_profit_d";
                    try{
                        Long result = hiveDBUtils.getResult(sqlTemp);
                        args.set(i,result+"");
                    }catch (Exception e){
                        logger.error(e.getMessage());
                    }

                }
                if(hasMaxStockPosition){
                    String sqlTemp = "select max(init_date) from temp.tmp_jjtj_t_user_stock_position";
                    try{
                        Long result = hiveDBUtils.getResult(sqlTemp);
                        args.set(i,result+"");
                    }catch (Exception e){
                        logger.error(e.getMessage());
                    }

                }
                if(!hasBefore && hasBatchDate && !hasBfM){
                    String date2 = MyDateUtils.getDate(date, "yyyy-MM-dd");
                    args.set(i,date2);
                }
                if(hasBeforeYearDate){
                    Date yearDate = MyDateUtils.getBeforeYearDate(date);
                    String dateStr = MyDateUtils.getDate(yearDate, "yyyy-MM-dd");
                    args.set(i,dateStr);
                }
                if(hasLastYearTransDay){
                    Date yearDate = MyDateUtils.getBeforeYearDate(date);
                    String date1 = MyDateUtils.getDate(yearDate, "yyyy-MM-dd");
                    args.set(i,date1);
                }
                if(hasLastIntYearTransDay){
                    Date yearDate = MyDateUtils.getBeforeYearDate(date);
                    String date1 = MyDateUtils.getDate(yearDate, "yyyyMMdd");
                    args.set(i,date1);
                }
                if(hasBefore && !hasBfM){
                    String[] split = arg.split(",");
                    String dateStr = "";
                    if("batch_int_date".equals(split[1])){
                        if(split.length >= 3){
                            long before = Long.valueOf(split[2]);
                            dateStr = MyDateUtils.getDate(MyDateUtils.getBeforeDay(date,before), "yyyyMMdd");
                        }
                    }
                    if("batch_date".equals(split[1])){
                        if(split.length >= 3){
                            long before = Long.valueOf(split[2]);
                            dateStr = MyDateUtils.getDate(MyDateUtils.getBeforeDay(date,before), "yyyy-MM-dd");
                        }
                    }
                    args.set(i,dateStr);
                }
                if(hasBfM && !hasBefore){
                    String[] split = arg.split(",");
                    String dateStr = "";
                    if("batch_int_date".equals(split[1])){
                        if(split.length >= 3){
                            int before = Integer.valueOf(split[2]);
                            dateStr = MyDateUtils.getDate(MyDateUtils.getBeforeMonthDate(date,before), "yyyyMMdd");
                        }
                    }
                    if("batch_date".equals(split[1])){
                        if(split.length >= 3){
                            int before = Integer.valueOf(split[2]);
                            dateStr = MyDateUtils.getDate(MyDateUtils.getBeforeMonthDate(date,before), "yyyy-MM-dd");
                        }
                    }
                    args.set(i,dateStr);
                }
            }
        }
        return args;
    }

}
