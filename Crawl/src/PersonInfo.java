import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.gargoylesoftware.htmlunit.javascript.host.media.rtc.mozRTCIceCandidate;

public class PersonInfo {
	String UserID;
	SqlConnect con;
	CookieInfo cookie;
	
	public PersonInfo(String ID, SqlConnect Con, CookieInfo c) {
		UserID = "https://weibo.cn/u/" + ID;
		con = Con;
		cookie = c;
	}
	
	public void getUserInfo() {
		Document doc;
		//粉丝列表（前100人） 有重复 需要数据库来处理
		//id基本信息：id—id，昵称，关注数，关注人列表（前100人），粉丝数，，微博数
		try {
			Thread.sleep(10000);
			doc = Jsoup.connect(UserID)
					.ignoreContentType(true)
					.header("Referer","https://weibo.cn/search/")
					.header("Host","weibo.cn")
					.header("Accept","text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8")
					.header("Connection","keep-alive")
					.header("Accept-Language","zh-cn")
					.header("Accept-Encoding","gzip, deflate")
					.header("User-Agent",cookie.GetUserAgent())
					.header("Cookie",cookie.GetCnCookie())
					.get();
			
			String IDString = doc.select("[valign=top]").select("a").attr("href");
			int position1 = IDString.indexOf('/');
			int position2 = IDString.indexOf('/', position1+1);
			String ID = IDString.substring(position1+1, position2);
			System.out.println("ID = " +ID);
			
			String NameString = doc.select("span[class=ctt]").text();
			String name = NameString.substring(0, NameString.indexOf(' '));
			System.out.println("Name = " + name);
			
			Elements links= doc.select("div[class=tip2]").select("a");
			String temp = links.get(0).text();
			position1 = temp.indexOf('[');
			position2 = temp.indexOf(']');
			int FollowNumber = Integer.valueOf(temp.substring(position1+1, position2));
			System.out.println("\n关注 = "+ FollowNumber);
			String Followurl = links.get(0).attr("href");
			FollowList follow = new FollowList(Followurl, con, ID, cookie);
			follow.getFollow();
			
			temp = links.get(1).text();
			position1 = temp.indexOf('[');
			position2 = temp.indexOf(']');
			int FanNumber = Integer.valueOf(temp.substring(position1+1, position2));
			System.out.println("\n粉丝 = "+ FanNumber);
			String Fanurl = links.get(1).attr("href");
			FanList fan = new FanList(Fanurl, con, ID, cookie);
			fan.getFan();
			
			temp = doc.select("span[class=tc]").text();
			position1 = temp.indexOf('[');
			position2 = temp.indexOf(']');
			int weiboNumber = Integer.valueOf(temp.substring(position1+1, position2));
			System.out.println("微博 = "+weiboNumber);
			String[] data = {ID, name, FollowNumber+"", FanNumber+"", weiboNumber + ""};
			con.insert("UserInf", data);
			
			System.out.println();
			//GetPost post = new GetPost(doc, name, ID, con, cookie);
			//post.getWeibo();
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
}
