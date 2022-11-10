package cn.flood.tools.captcha;

import cn.flood.base.core.lang.RandomType;
import cn.flood.base.core.lang.StringUtils;
import com.wf.captcha.ArithmeticCaptcha;
import com.wf.captcha.ChineseCaptcha;
import com.wf.captcha.GifCaptcha;
import com.wf.captcha.SpecCaptcha;
import com.wf.captcha.base.Captcha;
import org.springframework.util.DigestUtils;

import java.time.Clock;
import java.util.UUID;

public class TokenFactory {
	
	private static long delay = 3100000;//5分钟--多10s

	public Token getToken(TokenEnum tokenType) {
		String token;
		Captcha captcha = null;
		switch (tokenType){
			case CAPTCHA:
				captcha = createCaptcha("arithmetic");
				token = captcha.text();
				break;
			case MOBILE:
				token = StringUtils.random(4, RandomType.INT);
				break;
			case EMAIL:
				token = StringUtils.random(6, RandomType.INT);
				break;
			default:
				token = StringUtils.random(4, RandomType.INT);
		}
		if(captcha != null){
			return new TokenImpl(token, Clock.systemDefaultZone().millis(), delay, captcha.toBase64());
		}
		return new TokenImpl(token, Clock.systemDefaultZone().millis(), delay);
	}


	public Token getIdempotentToken(){
		String token = DigestUtils.md5DigestAsHex(UUID.randomUUID().toString().replaceAll("-", "").getBytes());
		return new TokenImpl(token, Clock.systemDefaultZone().millis(), delay);
	}


	private Captcha createCaptcha(String type) {
		Captcha captcha;
		if ("gif".equalsIgnoreCase(type)) {
			captcha = new GifCaptcha(115, 42, 4);
		} else if ("png".equalsIgnoreCase(type)) {
			captcha = new SpecCaptcha(115, 42, 4);
		} else if ("chinese".equalsIgnoreCase(type)) {
			captcha = new ChineseCaptcha(115, 42);
		} else  /*if (StrUtil.equalsIgnoreCase(type, "arithmetic")) */ {
			captcha = new ArithmeticCaptcha(115, 42);
		}
		captcha.setCharType(2);
		return captcha;
	}

}
