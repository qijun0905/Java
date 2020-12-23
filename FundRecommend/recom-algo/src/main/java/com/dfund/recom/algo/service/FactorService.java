package com.dfund.recom.algo.service;

import com.dfund.recom.algo.entity.Algorithm;
import com.dfund.recom.algo.entity.Factor;
import com.dfund.recom.algo.mapper.FactorMapper;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 2020/11/30 6:13 下午
 *
 * @author Seldom
 */
@Service
public class FactorService {

    @Autowired
    private FactorMapper factorMapper;

    public int updFactorByAlgoId(Factor factor) {

        return factorMapper.updateByFactorId(factor);
    }

    public PageInfo<Factor> getFactorlist(Integer pageSize, Integer pageNum, String algo) {
        Algorithm algorithm = new Algorithm();
        if(StringUtils.isNotBlank(algo)){
            algorithm.setAlgorithmName(algo);
        }else{
            return null;
        }
        PageHelper.startPage(pageNum,pageSize);
        List<Factor> list = factorMapper.selectByAlgoName(algorithm);
        PageInfo<Factor> pageInfo = new PageInfo<>(list);
        return pageInfo;
    }


}
