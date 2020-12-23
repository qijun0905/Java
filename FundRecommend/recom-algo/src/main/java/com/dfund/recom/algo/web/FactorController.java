package com.dfund.recom.algo.web;

import com.alibaba.fastjson.JSONObject;
import com.dfund.recom.algo.entity.Factor;
import com.dfund.recom.algo.service.FactorService;
import com.dfund.recom.entity.ResponseContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * 2020/11/30 6:06 下午
 *
 * @author Seldom
 */

@Controller
@ResponseBody
@RequestMapping(value = "fundFactor")
public class FactorController {

    private Logger logger = LoggerFactory.getLogger(FactorController.class);

    @Autowired
    private FactorService factorService;

    @RequestMapping(value = "/factorUpd")
    public ResponseContext updAlgorithm(Factor factor){
        logger.info("算法因子管理编辑，factor = "+factor.toString());
        int i = factorService.updFactorByAlgoId(factor);
        if(i > 0){
            return ResponseContext.updaeSuccess(i);
        }else{
            return ResponseContext.fail(ResponseContext.UPDATE_FAIL);
        }
    }

    @RequestMapping(value = "/factorList")
    public ResponseContext selectFactorList(Integer pageSize, Integer pageNum, String algo){
        logger.info("算法因子条件查询，alog {}",algo);
        return ResponseContext.getSuccess(factorService.getFactorlist(pageSize,pageNum,algo));
    }


}
