import java.io.IOException;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class FollowList {
	private String URL;
	Document doc;
	int count = 0;
	SqlConnect con;
	String id;
	CookieInfo cookie;
	
	FollowList(String url, SqlConnect Con, String ID, CookieInfo c) {
		URL = "https://weibo.cn/" + url;
		con = Con;
		id = ID;
		cookie = c;
	}
	
	public void getFollow() {
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
				Elements list = doc.select("table");
				for (Element man: list) {
					count ++;
					String FollowName = man.select("td").select("a").get(1).text();
					System.out.println(count + " : " + FollowName);
					String[] data = {id, FollowName};
					con.insert("UserFollowing", data);
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
