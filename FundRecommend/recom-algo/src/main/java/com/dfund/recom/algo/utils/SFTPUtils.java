package com.dfund.recom.algo.utils;

import com.dfund.recom.algo.service.AlgoService;
import com.jcraft.jsch.*;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.*;
import java.util.Properties;
import java.util.Vector;

/**
 * 2020/12/2 4:10 下午
 *
 * @author Seldom
 */
@Component
public class SFTPUtils {


    private Logger logger = LoggerFactory.getLogger(SFTPUtils.class);
   
    private ChannelSftp sftp;

    private Session session;
    /** FTP 登录用户名*/
    @Value("${sftp.username}")
    private String username;
    /** FTP 登录密码*/
    @Value("${sftp.password}")
    private String password;
    /** 私钥 */
    private String privateKey;
    /** FTP 服务器地址IP地址*/
    @Value("${sftp.hostname}")
    private String host;
    /** FTP 端口*/
    @Value("${sftp.port}")
    private int port;


    /**
     * 构造基于密码认证的sftp对象 
     * @param username
     * @param password
     * @param host
     * @param port
     */
    public SFTPUtils(String username, String password, String host, int port) {
        this.username = username;
        this.password = password;
        this.host = host;
        this.port = port;
    }

    /**
     * 构造基于秘钥认证的sftp对象
     * @param username
     * @param host
     * @param port
     * @param privateKey
     */
    public SFTPUtils(String username, String host, int port, String privateKey) {
        this.username = username;
        this.host = host;
        this.port = port;
        this.privateKey = privateKey;
    }

    public SFTPUtils(){}

    /**
     * 连接sftp服务器
     *
     * @throws Exception
     */
    public void login(){
        try {
            JSch jsch = new JSch();
            if (privateKey != null) {
                jsch.addIdentity(privateKey);// 设置私钥
                logger.info("sftp connect,path of private key file：{}" + privateKey);
            }
            logger.info("sftp connect by host:{} username:{}"+host+username);

            session = jsch.getSession(username, host, port);
            logger.info("Session is build");
            if (password != null) {
                session.setPassword(password);
            }
            Properties config = new Properties();
            config.put("StrictHostKeyChecking", "no");

            session.setConfig(config);
            session.connect();
            logger.info("Session is connected");

            Channel channel = session.openChannel("sftp");
            channel.connect();
            logger.info("channel is connected");

            sftp = (ChannelSftp) channel;
            logger.info(String.format("sftp server host:[%s] port:[%s] is connect successfull", host, port));
        } catch (JSchException e) {
            logger.error("Cannot connect to specified sftp server : {}:{} \n Exception message is: {}" +e.getMessage());
        }
    }

    /**
     * 关闭连接 server 
     */
    public void logout(){
        if (sftp != null) {
            if (sftp.isConnected()) {
                sftp.disconnect();
                logger.info("sftp is closed already");
            }
        }
        if (session != null) {
            if (session.isConnected()) {
                session.disconnect();
                logger.info("sshSession is closed already");
            }
        }
    }

    public void dirFile(String path,String mills) throws SftpException{
        try{
            sftp.cd(path);
        }catch (SftpException e){
            sftp.mkdir(path);
            sftp.cd(path);
            sftp.mkdir(mills);
        }
    }

    /**
     * 将输入流的数据上传到sftp作为文件 
     *
     * @param directory
     *            上传到该目录 
     * @param sftpFileName
     *            sftp端文件名 
     * @param
     *
     * @throws SftpException
     * @throws Exception
     */
    public void upload(String directory, String sftpFileName, InputStream input) throws SftpException{
        try {
            sftp.cd(directory);
        } catch (SftpException e) {
            logger.info("directory is not exist");
            sftp.mkdir(directory);
            sftp.cd(directory);
        }
        sftp.put(input, sftpFileName);
        logger.info("file:{} is upload successful   "+ sftpFileName);
    }

    /**
     * 上传单个文件
     *
     * @param directory
     *            上传到sftp目录 
     * @param uploadFile
     *            要上传的文件,包括路径 
     * @throws
     * @throws SftpException
     * @throws Exception
     */
    public void upload(String directory, String uploadFile) throws FileNotFoundException, SftpException{
        File file = new File(uploadFile);
        upload(directory, file.getName(), new FileInputStream(file));
    }

    /**
     * 将byte[]上传到sftp，作为文件。注意:从String生成byte[]是，要指定字符集。
     *
     * @param directory
     *            上传到sftp目录
     * @param sftpFileName
     *            文件在sftp端的命名
     * @param byteArr
     *            要上传的字节数组
     * @throws SftpException
     * @throws Exception
     */
    public void upload(String directory, String sftpFileName, byte[] byteArr) throws SftpException{
        upload(directory, sftpFileName, new ByteArrayInputStream(byteArr));
    }

    /**
     * 将字符串按照指定的字符编码上传到sftp
     *
     * @param directory
     *            上传到sftp目录
     * @param sftpFileName
     *            文件在sftp端的命名
     * @param dataStr
     *            待上传的数据
     * @param charsetName
     *            sftp上的文件，按该字符编码保存
     * @throws UnsupportedEncodingException
     * @throws SftpException
     * @throws Exception
     */
    public void upload(String directory, String sftpFileName, String dataStr, String charsetName) throws UnsupportedEncodingException, SftpException{
        upload(directory, sftpFileName, new ByteArrayInputStream(dataStr.getBytes(charsetName)));
    }

    /**
     * 下载文件 
     *
     * @param directory
     *            下载目录 
     * @param downloadFile
     *            下载的文件
     * @param saveFile
     *            存在本地的路径
     * @throws SftpException
     * @throws FileNotFoundException
     * @throws Exception
     */
    public void download(String directory, String downloadFile, String saveFile) throws SftpException, FileNotFoundException{
        if (directory != null && !"".equals(directory)) {
            sftp.cd(directory);
        }
        File file = new File(saveFile);
        sftp.get(downloadFile, new FileOutputStream(file));
        logger.info("file:{} is download successful"+ downloadFile);
    }
    /**
     * 下载文件
     * @param directory 下载目录
     * @param downloadFile 下载的文件名
     * @return 字节数组
     * @throws SftpException
     * @throws IOException
     * @throws Exception
     */
    public byte[] download(String directory, String downloadFile) throws SftpException, IOException{
        if (directory != null && !"".equals(directory)) {
            sftp.cd(directory);
        }
        InputStream is = sftp.get(downloadFile);

        byte[] fileData = IOUtils.toByteArray(is);

        logger.info("file:{} is download successful" + downloadFile);
        return fileData;
    }

    /**
     * 删除文件
     *
     * @param directory
     *            要删除文件所在目录
     * @param deleteFile
     *            要删除的文件
     * @throws SftpException
     * @throws Exception
     */
    public void delete(String directory, String deleteFile) throws SftpException{
        sftp.cd(directory);
        sftp.rm(deleteFile);
    }

    /**
     * 列出目录下的文件
     *
     * @param directory
     *            要列出的目录
     * @param
     * @return
     * @throws SftpException
     */
    public Vector<?> listFiles(String directory) throws SftpException {
        return sftp.ls(directory);
    }

    public static void main(String[] args) throws SftpException, IOException {
        SFTPUtils sftp = new SFTPUtils("root", "Pk!QAZ2wsx", "112.84.178.2", 6101);
        sftp.login();
        //byte[] buff = sftp.download("/opt", "start.sh");
        //logger.info(Arrays.toString(buff));
        File file = new File("/Users/seldom/Desktop/GraphProject/GraphSchedule/python/hello.py");
        InputStream is = new FileInputStream(file);

        sftp.upload("fund_rec/python", "hello.py", is);
        sftp.logout();
    }
}
