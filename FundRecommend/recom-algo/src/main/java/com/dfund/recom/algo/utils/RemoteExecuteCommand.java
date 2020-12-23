package com.dfund.recom.algo.utils;

import ch.ethz.ssh2.Connection;
import ch.ethz.ssh2.Session;
import ch.ethz.ssh2.StreamGobbler;

import java.io.*;

/**
 * 2020/12/8 10:45 上午
 *
 * @author Seldom
 */
public class RemoteExecuteCommand {
    private static String  DEFAULTCHART="UTF-8";
    private static Connection conn;
    private String ip;
    private Integer port;
    private String userName;
    private String userPwd;

    public RemoteExecuteCommand(String ip,Integer port, String userName, String userPwd) {
        this.ip = ip;
        this.port=port;
        this.userName = userName;
        this.userPwd = userPwd;
    }

    public RemoteExecuteCommand() {

    }

    /**
     * 远程登录linux的主机
     * @author Ickes
     * @since  V0.1
     * @return
     *      登录成功返回true，否则返回false
     */
    public Connection login(){
        boolean flg=false;
        try {
            conn = new Connection(ip,port);
            conn.connect();//连接
            flg=conn.authenticateWithPassword(userName, userPwd);//认证
            if (flg){
                System.out.println("认证成功！");
                return conn;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
    /**
     * @author Ickes
     * 远程执行shll脚本或者命令
     * @param cmd
     *      即将执行的命令
     * @return
     *      命令执行完后返回的结果值
     * @since V0.1
     */
    public String execute(String cmd){
        String result="";
        try {
            if(login() != null){
                Session session= conn.openSession();//打开一个会话
                session.execCommand(cmd);//执行命令
                result=processStdout(session.getStdout(),DEFAULTCHART);
                //如果为得到标准输出为空，说明脚本执行出错了
                if(result == null){
                    result=processStdout(session.getStderr(),DEFAULTCHART);
                }
                conn.close();
                session.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }


    /**
     * @author Ickes
     * 远程执行shll脚本或者命令
     * @param cmd
     *      即将执行的命令
     * @return
     *      命令执行成功后返回的结果值，如果命令执行失败，返回空字符串，不是null
     * @since V0.1
     */
    public String executeSuccess(String cmd){
        String result="";
        try {
            if(login()!= null){
                Session session= conn.openSession();//打开一个会话
                session.execCommand(cmd);//执行命令
                result=processStdout(session.getStdout(),DEFAULTCHART);
                conn.close();
                session.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 解析脚本执行返回的结果集
     * @author Ickes
     * @param in 输入流对象
     * @param charset 编码
     * @since V0.1
     * @return
     *       以纯文本的格式返回
     */
    public static String processStdout(InputStream in, String charset){
        InputStream    stdout = new StreamGobbler(in);
        StringBuffer buffer = new StringBuffer();;
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(stdout,charset));
            String line=null;
            while((line=br.readLine()) != null){
                buffer.append(line+"\n");
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return buffer.toString();
    }



    public static void main(String[] args) {

        RemoteExecuteCommand rec=new RemoteExecuteCommand("112.84.178.2",6101, "root","Pk!QAZ2wsx");// 参数分别为服务器IP ，用户名，密码
        //执行命令
        try {
            if(rec.login()!= null){

                System.out.println("=====第一个步骤=====");
                Session session= conn.openSession();//打开一个会话
                //TODO:多条命令
                session.execCommand("docker build -t harbor.vkdata.com/library/hello:v0.0.7 /root/fund_rec/pythonfile/202012081607394436547");//执行命令，这里需要确定刷新脚本存在的绝对路径哦
                String result=processStdout(session.getStdout(),DEFAULTCHART);
                //如果为得到标准输出为空，说明脚本执行出错了
                if(result == null){
                    System.out.println("脚本出错");
                    result=processStdout(session.getStderr(),DEFAULTCHART);
                }
                System.out.println(result);
                session.close();

                /**
                 System.out.println("=====第二个步骤=====");
                 Session session2= conn.openSession();//打开一个会话
                 //TODO:多条命令
                 session2.execCommand("cd /home/ubuntu/Desktop/music_rec/user_sim/result;cat xyy_result_m10d.json");//执行命令
                 String result2=processStdout(session2.getStdout(),DEFAULTCHART);
                 //如果为得到标准输出为空，说明脚本执行出错了
                 if(result == null ){//StringUtils.isBlank(result2)
                 System.out.println("脚本出错");
                 result2=processStdout(session2.getStderr(),DEFAULTCHART);
                 }
                 System.out.println(result2);
                 session2.close();
                 */

                conn.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }



    public static void setCharset(String charset) {
        DEFAULTCHART = charset;
    }
    public Connection getConn() {
        return conn;
    }
    public void setConn(Connection conn) {
        this.conn = conn;
    }
    public String getIp() {
        return ip;
    }
    public void setIp(String ip) {
        this.ip = ip;
    }
    public String getUserName() {
        return userName;
    }
    public void setUserName(String userName) {
        this.userName = userName;
    }
    public String getUserPwd() {
        return userPwd;
    }
    public void setUserPwd(String userPwd) {
        this.userPwd = userPwd;
    }
}
