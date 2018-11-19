package com.gatz.smarthomeapp.utils;

import java.io.Serializable;

public class ResultCode implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public final static String CODE_SUCCESS = "200";

	/** 201 设备控制失败  */
	public final static String CODE_CONTROL_FAILED = "201";
	/** 300 多种选择  */
	public final static String CODE_MORE_CHIOSE = "300";
	
	/** 400 （错误请求） 服务器不理解请求的语法。 */
	public final static String CODE_FAILED = "400";
	public final static String CODE_FAILED_MSG = "错误的请求";

	/**
	 * 401 （未授权） 请求要求身份验证。 对于需要登录的网页，服务器可能返回此响应。 //authenticated_failed
	 */
	public final static String CODE_AUTHENTICATED_FAILED = "401";
	public final static String CODE_AUTHENTICATED_FAILED_MSG = "认证失败";

	/** 403 （禁止） 服务器拒绝请求。 */
	public final static String CODE_SERVER_REFUSE = "403";
	public final static String CODE_SERVER_REFUSE_MSG = "服务器拒绝请求";

	/** 404 */
	public final static String CODE_NOT_FOUND = "404";
	public final static String CODE_NOT_FOUND_MSG = "错误的请求地址";

	/** 412 （未满足前提条件） 服务器未满足请求者在请求中设置的其中一个前提条件。 */
	public final static String CODE_INVALID_PARAMETERS = "412";
	public final static String CODE_INVALID_PARAMETERS_MSG = "无效的参数";

	/**
	 * 416 （请求范围不符合要求） 如果页面无法提供请求的范围，则服务器会返回此状态代码。 //permission denied
	 */
	public final static String CODE_PERMISSION_DENIED = "416";
	public final static String CODE_PERMISSION_DENIED_MSG = "没有权限";

	/** 500 */
	public final static String CODE_SERVER_ERROR = "500";
	public final static String CODE_SERVER_ERROR_MSG = "服务器错误";
	/** 503 */
	public final static String CODE_SERVER_TIMEOUT = "503";
	public final static String CODE_SERVER_TIMEOUT_MSG = "服务器超时";



}