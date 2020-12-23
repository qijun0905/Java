package com.dfund.recom.entity;

/**
 * 2020/11/12 10:18 上午
 *
 * @author Seldom
 */
public class ResponseContext {


    private String code; //返回的code值
    private String message; // 返回的提示类信息
    private Object data; //返回的信息数据
    private boolean isSuccess; // 操作是否成功

    public static String OK = "0000"; // 操作成功返回的状态码
    /** 查询 成功*/
    public static String SELECT_OK = "0001";
    /** 保存 成功*/
    public static String SAVE_OK = "0002";
    /** 更新 成功*/
    public static String UPDATE_OK = "0003";
    /** 删除 成功*/
    public static String DEL_OK = "0004";
    /** 已经认证过的code */
    public static String VALIDATE_YES = "0005";
    /** 已经注册过的code */
    public static String REGISTE_YES = "0006";

    public static String FAIL = "1000"; // 操作失败的状态码

    public static String FAIL_NO_MESSAGE = "1008"; // 操作失败的状态码,但不返回信息

    /** 查询 失败*/
    public static String SELECT_FAIL = "1001";
    /** 保存 失败*/
    public static String SAVE_FAIL = "1002";
    /** 更新 失败*/
    public static String UPDATE_FAIL = "1003";
    /** 删除 失败*/
    public static String DEL_FAIL = "1004";
    /** 字段验证失败 */
    public static String VALIDATE_FAIL = "1005";
    /** 没有登录 失败 */
    public static String NOT_LOGIN_FAIL = "1006";
    /**  没有权限 失败 */
    public static String  UNAUTHORIZED = "1007";


    /**
     * 字段验证失败
     * @return
     */
    public static ResponseContext filedValidateFail(Object obj){
        ResponseContext context = new ResponseContext();
        context.setSuccess(false);
        context.setCode(VALIDATE_FAIL);
        context.setMessage("字段验证失败");
        context.setData(obj);
        return context;
    }

    /**
     * 操作失败带失败提示信息
     * @param message 失败提示信息
     * @return
     */
    public static ResponseContext fail(String message){
        ResponseContext context = new ResponseContext();
        context.setSuccess(false);
        context.setCode(FAIL);
        context.setMessage(message);
        context.setData(null);
        return context;
    }

    /**
     * 操作失败不带失败提示信息
     * @return
     */
    public static ResponseContext failNoMessage(){
        ResponseContext context = new ResponseContext();
        context.setSuccess(false);
        context.setCode(FAIL_NO_MESSAGE);
        context.setMessage(null);
        context.setData(null);
        return context;
    }

    /**
     * 失败信息返回全，带失败提示信息，失败Code，失败返回值信息
     * @param message 失败提示信息
     * @param code 失败Code
     * @param obj 失败返回值信息
     * @return 失败信息返回全，带失败提示信息，失败Code，失败返回值信息
     */
    public static ResponseContext failALL(String message , String code , Object obj){
        ResponseContext context = new ResponseContext();
        context.setSuccess(false);
        context.setCode(code);
        context.setMessage(message);
        context.setData(obj);
        return context;
    }

    /**
     * 操作失败 带失败提示信息和返回值信息
     * @param message 失败提示信息
     * @param obj 返回值信息
     * @return
     */
    public static ResponseContext failWithObj(String message,Object obj){
        ResponseContext context = new ResponseContext();
        context.setSuccess(false);
        context.setCode(FAIL);
        context.setMessage(message);
        context.setData(obj);
        return context;
    }


    /**
     * 操作成功 带成功提示信息
     * @param message 成功提示信息
     * @return
     */
    public static ResponseContext success(String message){
        ResponseContext context = new ResponseContext();
        context.setSuccess(true);
        context.setCode(OK);
        context.setMessage(message);
        context.setData(null);
        return context;
    }

    /**
     * 操作成功 带成功提示信息 和 返回值信息
     * @param message 成功提示信息
     * @param obj 返回值信息
     * @return
     */
    public static ResponseContext successWithObj(String message,Object obj){
        ResponseContext context = new ResponseContext();
        context.setSuccess(true);
        context.setCode(OK);
        context.setMessage(message);
        context.setData(obj);
        return context;
    }

    /**
     * 登录成功
     * @param obj 登录人信息
     * @return
     */
    public static ResponseContext loginSuccess(Object obj){
        ResponseContext context = new ResponseContext();
        context.setSuccess(true);
        context.setCode(OK);
        context.setMessage("登录成功！");
        context.setData(obj);
        return context;
    }
    /**
     * 用户没有登录
     * @param
     * @return
     */
    public static ResponseContext loginFail(){
        ResponseContext context = new ResponseContext();
        context.setSuccess(false);
        context.setCode(NOT_LOGIN_FAIL);
        context.setMessage("请先登录系统！");
        context.setData(null);
        return context;
    }

    /**
     * 查询成功带 返回值信息
     * @param obj 返回值信息
     * @return
     */
    public static ResponseContext getSuccess(Object obj){
        ResponseContext context = new ResponseContext();
        context.setSuccess(true);
        context.setCode(SELECT_OK);
        context.setMessage("查找成功");
        context.setData(obj);
        return context;
    }

    /**
     * 保存成功带 返回值信息
     * @param obj 返回值信息
     * @return
     */
    public static ResponseContext saveSuccess(Object obj){
        ResponseContext context = new ResponseContext();
        context.setSuccess(true);
        context.setCode(SAVE_OK);
        context.setMessage("保存成功");
        context.setData(obj);
        return context;
    }


    /**
     * 更新成功带 返回值信息
     * @param obj 返回值信息
     * @return
     */
    public static ResponseContext updaeSuccess(Object obj){
        ResponseContext context = new ResponseContext();
        context.setSuccess(true);
        context.setCode(UPDATE_OK);
        context.setMessage("更新成功");
        context.setData(obj);
        return context;
    }

    public static ResponseContext successWithAll(Object obj,String code,String message){
        ResponseContext context = new ResponseContext();
        context.setSuccess(true);
        context.setCode(code);
        context.setMessage(message);
        context.setData(obj);
        return context;
    }

    /**
     * 删除成功带 返回值信息
     * @param obj 返回值信息
     * @return
     */
    public static ResponseContext deleteSuccess(Object obj){
        ResponseContext context = new ResponseContext();
        context.setSuccess(true);
        context.setCode(DEL_OK);
        context.setMessage("删除成功");
        context.setData(obj);
        return context;
    }

    public String getCode() {
        return code;
    }
    public void setCode(String code) {
        this.code = code;
    }
    public String getMessage() {
        return message;
    }
    public void setMessage(String message) {
        this.message = message;
    }
    public Object getData() {
        return data;
    }
    public void setData(Object obj) {
        this.data = obj;
    }
    public boolean isSuccess() {
        return isSuccess;
    }
    public void setSuccess(boolean isSuccess) {
        this.isSuccess = isSuccess;
    }


}
