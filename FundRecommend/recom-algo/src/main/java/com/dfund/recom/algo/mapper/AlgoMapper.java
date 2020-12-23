package com.dfund.recom.algo.mapper;

import com.dfund.recom.algo.entity.Algorithm;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AlgoMapper {

    List<Algorithm> selectAlgoByCondition(@Param("algorithm") Algorithm algorithm);

    int updByAlgoId(@Param("algorithm") Algorithm algorithm);

    Integer insertAlgo(Algorithm algorithm);
}
