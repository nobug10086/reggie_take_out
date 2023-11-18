package com.itheima.reggie.utils;

/**
 * @Author: JIAO
 * @Date: 2023/10/9 17:52
 * @Description: 生成随机验证码
 **/

import java.text.DecimalFormat;
import java.util.Random;

/**
 * 获取随机数
 *
 * @author qianyi
 *
 */
public class RandomUtil {

	private static final Random random = new Random();

	private static final DecimalFormat fourdf = new DecimalFormat("0000");

	private static final DecimalFormat sixdf = new DecimalFormat("000000");

	//生成4位随机数
	public static String getFourBitRandom() {
		return fourdf.format(random.nextInt(10000));
	}
	//生成6位随机数
	public static String getSixBitRandom() {
		return sixdf.format(random.nextInt(1000000));
	}

}
