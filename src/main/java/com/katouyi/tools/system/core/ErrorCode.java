package com.katouyi.tools.system.core;

public enum ErrorCode {
    OK(0, "执行成功!!"),
    ADD_OK(0, "添加成功!!"),
    UPDATE_OK(0, "更新成功!!"),
    DELETE_OK(0, "删除成功!!"),
    FIND_OK(0, "查询成功!!"),
    REQUEST_NO_HANDLER_FOUND(100003, "请求资源不存在,请确认相关资源!!"),
    REQUEST_METHOD_NOT_SUPPORTED(100004, "请求方式不正确,请核查后请求!!"),
    REQUEST_MEDIATYPE_NOT_SUPPORTED(100005, "请求类型不支持,请核查后请求!!"),
    REQUEST_FILE_IS_EMPTY(100006, "请选择文件后提交!!"),
    SYSTEM_ERROR(110099, "系统错误,请稍后重试!!"),
    FILE_IS_EMPTY(110001, "请选择文件后提交!!"),
    HTTP_METHOD_NOT_SUPPORT(110002, "请求方式不正确,请核查后请求!!"),
    HTTP_MEDIA_TYPE_NOT_SUPPORT(110003, "请求类型不支持,请核查后请求!!"),
    RESOURCE_NOT_FOUND(110004, "请求资源不存在,请确认相关资源!!"),
    PARAM_ERROR(110005, "参数错误,请核查后请求!!"),
    PARAM_EMPTY(110006, "参数为空,请核查后请求!!"),
    PARAM_ILLEGAL(110007, "参数不合法,请核查后再试!!"),
    DELETE_FAIL_ILLEGAL(110008, "删除失败,请核查后再试!!"),
    SAVE_FAIL_DATA_DUPLICATE(110009, "添加失败,请忽重复添加!!"),
    USER_IS_NOT_LOGIN(110010, "用户未登陆,请登陆后再试!!"),
    AUTH_FAIL(110011, "签名验证失败!!");

    private int code;
    private String message;

    private ErrorCode(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public int getCode() {
        return this.code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return this.message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
