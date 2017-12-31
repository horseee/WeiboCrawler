import java.io.IOException;

import org.apache.xml.utils.StringBufferPool;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;


//缺重复用户处理
public class FanList {
	private String URL;
	Document doc;
	SqlConnect con;
	int count = 0;
	String ID;
	CookieInfo cookie;
	
	FanList(String url, SqlConnect Con, String id, CookieInfo c) {
		URL = "https://weibo.cn/" + url;
		con = Con;
		ID = id;
		cookie = c;
	}
	
	public void getFan() {
		try {
			while (count < 100) {
				doc = Jsoup.connect(URL)
						.ignoreContentType(true)
						.header("Host","weibo.cn")
						.header("Accept","text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8")
						.header("Connection","keep-alive")
						.header("Accept-Language","zh-cn")
						.header("Accept-Encoding","gzip, deflate")
						.header("User-Agent",cookie.GetUserAgent())
						.header("Cookie",cookie.GetCnCookie())
						.get();
				//System.out.println(doc);
				Elements list = doc.select("[class=c]").select("table");
				for (Element man: list) {
					count ++;
					String FanName = man.select("td").select("a").get(1).text();
					System.out.println(count + " : " + FanName);
					String[] data = {ID, FanName};
					if (con.insert("UserFollower", data) == 0)
						count --;
				}
				URL = doc.select("div[class=pa][id=pagelist]").select("a").attr("href");
				URL = "https://weibo.cn/" + URL;
				Thread.sleep(2000);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
