package com.dfundata.fund.schedule.service;

import com.dfundata.fund.schedule.utils.TraverseJson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Date;

/**
 * 2020/10/20 10:46 上午
 *
 * @author Seldom
 */
@Service
public class HiveSqlTask {

    @Value("${sql.json.file}")
    private String sqlJsonFileUrl;

    @Autowired
    private TraverseJson traverseJson;


    public void  executeHiveSqlTask(Date date, String procedure) throws SQLException, IOException {
        File file = new File(sqlJsonFileUrl + procedure);
        traverseJson.traverseFile(date,file);

    }

}
