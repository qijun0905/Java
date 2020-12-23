package com.dfund.recom.algo.service;

import ch.ethz.ssh2.Connection;
import ch.ethz.ssh2.Session;
import com.dfund.recom.algo.entity.Algorithm;
import com.dfund.recom.algo.entity.Factor;
import com.dfund.recom.algo.mapper.AlgoMapper;
import com.dfund.recom.algo.mapper.FactorMapper;
import com.dfund.recom.algo.utils.K8SClientUtils;
import com.dfund.recom.algo.utils.RemoteExecuteCommand;
import com.dfund.recom.algo.utils.SFTPUtils;
import com.dfund.recom.algo.utils.ZipUtil;
import com.dfund.recom.utils.MyDateUtils;
import com.dfund.recom.utils.NullUtis;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.charset.Charset;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * 2020/11/20 2:01 下午
 *
 * @author Seldom
 */
@Service
public class AlgoService {

    private Logger logger = LoggerFactory.getLogger(AlgoService.class);

    @Autowired
    private AlgoMapper algoMapper;

    @Autowired
    private FactorMapper factorMapper;

    @Autowired
    private SFTPUtils sftpUtils;

    @Autowired
    private ZipUtil zipUtil;

    @Value("${sftp.username}")
    private String username;
    /** FTP 登录密码*/
    @Value("${sftp.password}")
    private String password;
    /** FTP 服务器地址IP地址*/
    @Value("${sftp.hostname}")
    private String host;
    /** FTP 端口*/
    @Value("${sftp.port}")
    private int port;

    @Value("${host.fileurl}")
    private String fileurl;

    @Value("${host.schema}")
    private String schema;

    private static String  DEFAULTCHART="UTF-8";

    /**
     * 根据条件查询
     * @param str
     */
    public PageInfo<Algorithm> getAlgolist(Integer pageSize,Integer pageNum,String str, String type, String user){
        Algorithm algorithm = new Algorithm();
        if(StringUtils.isBlank(str) && StringUtils.isBlank(type)
                && StringUtils.isBlank(user)){
            // 无条件查询
        }else{
            // 有条件查询
            if(NullUtis.isNotNull(str)){
                if(str.contains("v") && str.contains(".")){
                    String name = str.substring(0, str.lastIndexOf("v"));
                    String version = str.substring(str.lastIndexOf("v"));
                    algorithm.setAlgorithmName(name);
                    algorithm.setAlgorithmVision(version);
                }else{
                    algorithm.setAlgorithmName(str);
                }
            }
            if(NullUtis.isNotNull(type)) {
                algorithm.setAlgorithmType(type);
            }
            if(NullUtis.isNotNull(user)) {
                algorithm.setCreater(user);
            }
        }
        PageHelper.startPage(pageNum,pageSize);
        List<Algorithm>  list = algoMapper.selectAlgoByCondition(algorithm);
        if (list.size() > 0){
            for (Algorithm algo:list) {
                List<Factor> factors = factorMapper.selectByAlgoId(algo.getAlgorithmId());
                algo.setList(factors);
                Map<String,List<Factor>> map = new HashMap<>();
                if(factors.size() > 0 ){
                    for (Factor fact:factors
                         ) {
                        String factorType = fact.getFactorType();
                        if(map.containsKey(factorType)){
                            map.get(factorType).add(fact);
                        }else {
                            List<Factor> fList = new ArrayList<>();
                            fList.add(fact);
                            map.put(factorType,fList);
                        }
                    }
                }
                algo.setMap(map);
            }
        }
        PageInfo<Algorithm> pageInfo = new PageInfo<>(list);
        return pageInfo;
    }

    @Transactional
    public int updAlgoByAlgoId(MultipartFile file,Map map){
        Algorithm algorithm = new Algorithm();


        algorithm.setUpdTime(new Date());
        return algoMapper.updByAlgoId(algorithm);

    }


    /**
     * 插入新的算法，根据插入后的算法id,再分别插入客户因子以及基金因子
     * 因子表中的factor_type = 0 - 客户因子，1-基金因子
     * @param
     * @param
     * @return
     */
    @Transactional
    public Integer insertAlgo(Map<String,Object> map, MultipartFile file){
        try{
            if(map.size() == -1 ){
//                Algorithm algorithm = new Algorithm();
//                Factor factor0 = new Factor();
//                Factor factor1 = new Factor();
                Algorithm algorithm = (Algorithm) map.get("algorithm");
                Factor factor0 = (Factor) map.get("factor0");
                Factor factor1 = (Factor) map.get("factor1");
//                if(map.get("algorithmName") != null){
//                    algorithm.setAlgorithmName((String)map.get("algorithmName"));
//                }
//                if(map.get("algorithmVision") != null){
//                    algorithm.setAlgorithmVision((String)map.get("algorithmVision"));
//                }
//                if(map.get("algorithmType") != null){
//                    algorithm.setAlgorithmType((String)map.get("algorithmType"));
//                }
//                if(map.get("applyClientScope") != null){
//                    algorithm.setApplyClientScope((String)map.get("applyClientScope"));
//                }
//                if(map.get("creater") != null){
//                    algorithm.setCreater((String)map.get("creater"));
//                }
//                if(map.get("algorithmDescription") != null){
//                    algorithm.setAlgorithmDescription((String)map.get("algorithmDescription"));
//                }
//                if(map.get("algorithmState") != null){
//                    algorithm.setAlgorithmState((String)map.get("algorithmState"));
//                }
                String name = file.getName();
                if(name.endsWith(".zip")){
//                    uploadZip(file);
                }else{

                }
                algorithm.setPythonfile(name);
                String yyyyMMdd = MyDateUtils.getDate(new Date(), "yyyy-MM-dd");
                algorithm.setInitDate(Integer.parseInt(yyyyMMdd));
                algorithm.setUpdUser(algorithm.getCreater());
                algoMapper.insertAlgo(algorithm);
                Integer algorithmId = algorithm.getAlgorithmId();
                factor0.setAlgorithmId(algorithmId);
                factor0.setInitDate(Integer.parseInt(yyyyMMdd));
                factor1.setAlgorithmId(algorithmId);
                factor1.setInitDate(Integer.parseInt(yyyyMMdd));
                factorMapper.insertSelective(factor0);
                factorMapper.insertSelective(factor1);
                return algorithmId;
            }

        }catch (Exception e){
            logger.info("AlgoService ... insertAlgo error : "+e.getMessage());
        }
        return 0;
    }

    public Integer upload(MultipartFile file,String version) throws Exception{
        Boolean hasDockerfile = false;
        String yyyyMMdd = MyDateUtils.getDate(new Date(), "yyyyMMdd");
        long millis = System.currentTimeMillis();
        String path = fileurl+yyyyMMdd+"/"+millis;
        if(file != null){
            String filename = file.getOriginalFilename();
            if(filename.matches(".*\\.zip")){
//                zipUtil.hhh(file);
                hasDockerfile = zipUtil.unZip(file, fileurl + yyyyMMdd, millis + "");
            }
            if(!hasDockerfile){
                InputStream inputStream = file.getInputStream();
                sftpUtils.login();
                sftpUtils.dirFile(fileurl+yyyyMMdd,millis+"");
                sftpUtils.upload(path,filename,inputStream);
                sftpUtils.logout();
                // 获取Dockerfile
                StringBuilder sb = new StringBuilder();
                sb.append("FROM python:3.6-alpine\r\n");
                sb.append("ADD").append(" ").append(filename)
                        .append(" ").append(filename).append("\r\n");
                sb.append("WORKDIR /");

                String str = sb.toString();
                sftpUtils.login();
                sftpUtils.upload(path,"Dockerfile",str,"utf8");
                sftpUtils.logout();
            }

            // 文件上传完毕， build image
            // 这里的filename 需要剔除后缀zipdemo.zip
            String jobName = "";
            if(filename.endsWith(".zip")){
                jobName = filename.replace(".zip","");
            }
            if(filename.endsWith(".py")){
                jobName = filename.replace(".py","");
            }
            System.out.println("jobname ======= " + jobName);
            String image = buildAndPush(path,jobName,version);
            System.out.println("image = " + image);
//            K8SClientUtils.createJob(jobName,"fund",image);
            K8SClientUtils.getLog();
            System.out.println("pos  7");
            return 1;
        }
        return 0;
    }


    private String buildAndPush(String path,String filename,String version){
        RemoteExecuteCommand rec = new RemoteExecuteCommand(host,port,username,password);
        try {
            Connection conn = rec.login();
            if(conn != null){
                logger.info("=====build=====");
                //打开一个会话
                Session session= conn.openSession();
                session.execCommand("docker build -t "+schema+"/"+filename+":"+version+" "+path);
                String result=RemoteExecuteCommand.processStdout(session.getStdout(),DEFAULTCHART);
                //如果为得到标准输出为空，说明脚本执行出错了
                if(result == null){
                    logger.error("脚本出错");
                    result=RemoteExecuteCommand.processStdout(session.getStderr(),DEFAULTCHART);
                }
                session.close();
                logger.info("=====push=====");
                //打开一个会话
                Session session2= conn.openSession();
                session2.execCommand("docker push "+schema+"/"+filename+":"+version);
                String result2=RemoteExecuteCommand.processStdout(session2.getStdout(),DEFAULTCHART);
                //如果为得到标准输出为空，说明脚本执行出错了
                if(result2 == null ){
                    logger.error("脚本出错");
                    result2=RemoteExecuteCommand.processStdout(session2.getStderr(),DEFAULTCHART);
                }
                session2.close();
                conn.close();
                return schema+"/"+filename+":"+version;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

}
