package com.dfund.recom.algo.web;

import com.alibaba.fastjson.JSONObject;
import com.dfund.recom.algo.service.AlgoService;
import com.dfund.recom.entity.ResponseContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;


/**
 * 2020/11/20 10:25 上午
 * 算法管理
 * @author Seldom
 */
@Controller
@ResponseBody
@RequestMapping(value = "fund")
public class AlgoController {

    private Logger logger = LoggerFactory.getLogger(AlgoController.class);

    @Autowired
    private AlgoService algoService;


    /**
     * 根据条件查询
     * @param str
     * @param type
     * @param creater
     * @return
     */
    @RequestMapping(value = "/algoList")
    public ResponseContext selectAlgoList(Integer pageSize, Integer pageNum, String str, String type, String creater){
        logger.info("算法管理条件查询，str {},type {}, creater {}",str,type,creater);
        return ResponseContext.getSuccess(algoService.getAlgolist(pageSize,pageNum,str, type, creater));
    }


    @RequestMapping(value = "/algoUpd")
    public ResponseContext updAlgorithm(@RequestBody Map<String,Object> map, MultipartFile file){
        logger.info("算法管理编辑，map = "+map.size());
        int i = algoService.updAlgoByAlgoId(file,map);
        if(i > 0){
            return ResponseContext.updaeSuccess(i);
        }else{
            return ResponseContext.fail(ResponseContext.UPDATE_FAIL);
        }
    }

    @RequestMapping(value = "/algoAdd")
    public ResponseContext addAlgorithm(@RequestParam Map<String,Object> map, MultipartFile file, HttpServletRequest request){
        System.out.println("map "+map.size() +"####"+file.getName());
        Integer integer = algoService.insertAlgo(map,file);
        if(integer > 0){
            return ResponseContext.saveSuccess("success");
        }
        else{
            return ResponseContext.fail(ResponseContext.SAVE_FAIL);
        }


    }



    @RequestMapping(value = "/upload")
    public ResponseContext upload(MultipartFile file,String version){
        logger.info("upload - file~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
        try{
            int i = algoService.upload(file,version);
            if(i > 0){
                return ResponseContext.updaeSuccess(i);
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }
        return ResponseContext.fail(ResponseContext.UPDATE_FAIL);
    }

}
