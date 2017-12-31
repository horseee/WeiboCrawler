
public class CookieInfo {
	static String cookieForCn2 = "Your cookie for www.weibo.cn";
	static String cookieForCom = "Your cookie for www.weibo.com";
	static String RefererForCom = "Your referer for www.weibo.com";
	static String UserAgent = "Your useragent for www.weib.com/www.weibo.cn";
	int count;
	
	public CookieInfo() {
		count = 0;
	}
	
	public String GetCnCookie() {
		return cookieForCn2;
	}
	
	public String GetComCookie() {
		return cookieForCom;
	}
	
	public String GetRefererForCom() {
		return RefererForCom;
	}
	
	public String GetUserAgent() {
		return UserAgent;
	}
}


