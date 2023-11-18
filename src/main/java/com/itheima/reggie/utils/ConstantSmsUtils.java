package com.itheima.reggie.utils;

/**
 * @Author: JIAO
 * @Date: 2023/10/9 17:53
 * @Description: 发送短信工具类
 **/

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * 实现了InitializingBean接口，当spring进行初始化bean时，会执行afterPropertiesSet方法
 */
@Component
public class ConstantSmsUtils implements InitializingBean {

	@Value("${tencent.sms.keyId}")
	private String secretID ;
	@Value("${tencent.sms.keysecret}")
	private String secretKey ;
	@Value("${tencent.sms.smsSdkAppId}")
	private String smsSdkAppID ;
	@Value("${tencent.sms.signName}")
	private String signName ;
	@Value("${tencent.sms.templateId}")
	private String templateID ;

	public static String SECRET_ID;
	public static String SECRET_KEY;
	public static String SMSSDKAPP_ID;
	public static String SIGN_NAME;
	public static String TEMPLATE_ID;


	@Override
	public void afterPropertiesSet() throws Exception {
		SECRET_ID = secretID;
		SECRET_KEY = secretKey;
		SMSSDKAPP_ID = smsSdkAppID;
		SIGN_NAME = signName;
		TEMPLATE_ID = templateID;
	}
}

