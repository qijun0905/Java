package com.dfund.recom.algo.mapper;


import com.dfund.recom.algo.entity.Algorithm;
import com.dfund.recom.algo.entity.Factor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FactorMapper {


    List<Factor> selectByAlgoId(Integer algorithmId);

    void insertSelective(Factor factor1);

    int updateByFactorId(Factor factor);

    List<Factor> selectByAlgoName(Algorithm algorithm);
}
