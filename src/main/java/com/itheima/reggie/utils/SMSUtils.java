package com.itheima.reggie.utils;

import com.tencentcloudapi.common.Credential;
import com.tencentcloudapi.common.exception.TencentCloudSDKException;
import com.tencentcloudapi.common.profile.ClientProfile;
import com.tencentcloudapi.common.profile.HttpProfile;
import com.tencentcloudapi.sms.v20210111.SmsClient;
import com.tencentcloudapi.sms.v20210111.models.SendSmsRequest;
import com.tencentcloudapi.sms.v20210111.models.SendSmsResponse;
import org.springframework.util.StringUtils;


/**
 * 短信发送工具类
 */
public class SMSUtils {

	//发送短信的方法
	public static boolean send(String phone, String verificationCode) {
		//判断手机号是否为空
		if (StringUtils.isEmpty(phone)){
			return false;
		}
		try{
			// 实例化一个认证对象，入参需要传入腾讯云账户secretId，secretKey,此处还需注意密钥对的保密
			// 密钥可前往https://console.cloud.tencent.com/cam/capi网站进行获取
			Credential cred = new Credential(ConstantSmsUtils.SECRET_ID, ConstantSmsUtils.SECRET_KEY);
			// 实例化一个http选项，可选的，没有特殊需求可以跳过
			HttpProfile httpProfile = new HttpProfile();
			httpProfile.setEndpoint("sms.tencentcloudapi.com");
			// 实例化一个client选项，可选的，没有特殊需求可以跳过
			ClientProfile clientProfile = new ClientProfile();
			clientProfile.setHttpProfile(httpProfile);
			// 实例化要请求产品的client对象,clientProfile是可选的  第二个参数是地域信息
			SmsClient client = new SmsClient(cred, "ap-guangzhou", clientProfile);
			// 实例化一个请求对象,每个接口都会对应一个request对象
			SendSmsRequest req = new SendSmsRequest();
			//设置固定的参数
			req.setSmsSdkAppId(ConstantSmsUtils.SMSSDKAPP_ID);// 短信应用ID: 短信SdkAppId在 [短信控制台] 添加应用后生成的实际SdkAppId
			req.setSignName(ConstantSmsUtils.SIGN_NAME);//短信签名内容: 使用 UTF-8 编码，必须填写已审核通过的签名
			req.setTemplateId(ConstantSmsUtils.TEMPLATE_ID);//模板 ID: 必须填写已审核通过的模板 ID
			/* 用户的 session 内容: 可以携带用户侧 ID 等上下文信息，server 会原样返回 */
//            String sessionContext = "xxx";
//            req.setSessionContext(sessionContext);

			//设置发送相关的参数
			String[] phoneNumberSet1 = {"+86"+phone};
			req.setPhoneNumberSet(phoneNumberSet1);//发送的手机号
			String[] templateParamSet1 = {verificationCode, "5"};//模板的参数 第一个是验证码，第二个是过期时间
			req.setTemplateParamSet(templateParamSet1);//发送验证码

			//发送短信
			// 返回的resp是一个SendSmsResponse的实例，与请求对象对应
			SendSmsResponse resp = client.SendSms(req);
			System.out.println("resp"+resp);
			// 输出json格式的字符串回包
			System.out.println(SendSmsResponse.toJsonString(resp));
			return true;
		} catch (TencentCloudSDKException e) {
			e.printStackTrace();
			return false;
		}
	}
}
