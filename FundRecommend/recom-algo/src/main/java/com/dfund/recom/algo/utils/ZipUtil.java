package com.dfund.recom.algo.utils;

import java.io.*;
//import java.util.zip.ZipEntry;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import com.jcraft.jsch.SftpException;
import com.sun.org.apache.xpath.internal.operations.Bool;
import org.apache.commons.lang3.StringUtils;
import org.apache.tools.zip.ZipFile;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

/**
 * 2020/12/9 10:42 上午
 *
 * @author Seldom
 */
@Component
public class ZipUtil {

    private static final int buffer = 2048;

    @Autowired
    private SFTPUtils sftpUtils;
    /**
     * 解压Zip文件
     * @param
     */
    public Boolean unZip(MultipartFile zfile, String pythonDir, String mills) {
        Boolean hasDockerfile = false;
        File file = null;
        ZipInputStream zs =null;
        String path = pythonDir+"/"+mills;
        try{
            String filename = zfile.getOriginalFilename();
            if(StringUtils.isNotBlank(filename)){
                zs = new ZipInputStream(zfile.getInputStream());
                ZipEntry ze ;
                while((ze =zs.getNextEntry()) != null) {
                    ZipEntry entry = ze;
                    String name = entry.getName();
                    if(entry.isDirectory()){
                        continue;
                    }
                    if(name.contains("Dockerfile")){
                        hasDockerfile =true;
                    }
                    file = new File(name);
                    sftpUtils.login();
                    sftpUtils.dirFile(pythonDir,mills);
                    sftpUtils.upload(path,file.getName(),zs);
                    sftpUtils.logout();
                }

            }
        }catch (IOException e){
            e.printStackTrace();
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            try {
                if(zs != null){
                    zs.close();
                }
            }catch (Exception e){
                e.printStackTrace();
            }

        }
        return hasDockerfile;
    }



    public void hhh(MultipartFile zfile){ {
        long millis = System.currentTimeMillis();
        ZipInputStream zs =null;
        File file = null;
        try
        {
            zs = new ZipInputStream(zfile.getInputStream());
            ZipEntry ze;
            while((ze = zs.getNextEntry()) != null) {
                ZipEntry entry = ze;
                String filename = entry.getName();
                if(entry.isDirectory()){
                    continue;
                }
                file = new File(filename);
                sftpUtils.login();
                sftpUtils.upload("/root/fund_rec/pythonfile/20201209/"+millis,file.getName(),zs);
                sftpUtils.logout();
            }
            }catch(IOException ioe){
                ioe.printStackTrace();
            }catch(Exception ioe){
                ioe.printStackTrace();
            }finally{
                try{
                    if(zs != null){
                        zs.close();
                    }
                }catch(Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
