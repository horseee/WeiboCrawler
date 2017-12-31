
import java.io.*;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.openqa.selenium.WebDriver;

//2,10个topic（可以指定并写死）：
//（1）.topic基本信息：
//（2）.topic-post（前500，就是主持人推荐）：link，发布人，发布人id，发布时间（转化为绝对时间）
//     来自，评论数，评论列表（前50），转发数，赞数，
//     本post是否转发，（如是）原post发布人，（如是）原post发布时间（转化为绝对时间），（如是）原post来自，（如是）原post评论数，（如是）原post转发数，（如是）原post赞数

//topic-id，名称，阅读数，讨论数，粉丝数，主持人，主持人id ， 粉丝列表（前100人）
//

public class TopicInfo {
	String TopicUrl;
	String TopicName;
	String UserUrl;
	String UserName;
	WebDriver driver;
	SqlConnect con;
	
	public TopicInfo(String Url, String name, String userUrl, String userName, WebDriver Driver, SqlConnect sql) {
		TopicUrl = Url;
		TopicName = name;
		UserUrl = userUrl;
		UserName = userName;
		driver = Driver;
		con = sql;
	}
	
	public void getTopicInfo() {
		try {
			Document doc = Jsoup.connect(TopicUrl)
					.ignoreContentType(true)
					.header("Referer",new CookieInfo().GetRefererForCom())
					.header("Host","d.weibo.com")
					.header("Accept","text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8")
					.header("Connection","keep-alive")
					.header("Accept-Language","zh-cn")
					.header("Accept-Encoding","gzip, deflate")
					.header("User-Agent",new CookieInfo().GetUserAgent())
					.header("Cookie",new CookieInfo().GetComCookie())
					.get();
			File file =new File("javaio-appendfile14.txt");
			if(!file.exists()){
			       file.createNewFile();
			}
			String html = doc.toString();
			int position = html.indexOf("tb_counter");
			if (position == -1) {
				System.out.println("无效Topic");
				return;
			}
			
			System.out.println("Name = " + TopicName);
			
			//String TopicUrl ="http://weibo.com/p/1008088a58f201bd42b0e12ae6a39522eb91dc?from=faxian_huati"; 
			int index1 = TopicUrl.indexOf("/p/");
			int index2 = TopicUrl.indexOf('?');
			String TopicID = TopicUrl.substring(index1+3, index2);
			System.out.println("ID = " + TopicID);
			
			
			int pos = html.indexOf("<strong", position);
			pos = html.indexOf('>', pos);
			int pos2 = html.indexOf("<", pos);
			String ReadNumber = html.substring(pos+1, pos2);
			System.out.println("阅读 = " + ReadNumber);
			position = pos2+1;
			//<strong class=\"\">8194.3万<\/strong><span class=\"S_txt2\">阅读
			
			pos = html.indexOf("<strong", position);
			pos = html.indexOf('>', pos);
			pos2 = html.indexOf("<", pos);
			String DiscussNumber = html.substring(pos+1, pos2);
			System.out.println("讨论 = " + DiscussNumber);
			position = pos2+1;
			
			pos = html.indexOf("<strong", position);
			int fanUrlpos = html.indexOf("href=", pos-100);
			String FanURL = ParseUrl(html.substring(fanUrlpos+7, pos));
			System.out.println("粉丝URL = " + FanURL);
			pos = html.indexOf('>', pos);
			pos2 = html.indexOf("<", pos);
			String FanNumber = html.substring(pos+1, pos2);
			System.out.println("粉丝 = " + FanNumber);
			
			
			String UserID = UserUrl.substring(UserUrl.indexOf("/u/")+3,UserUrl.indexOf('?'));
			System.out.println("主持人ID = " + UserID);
			System.out.println("主持人Name = " + UserName);

			pos = html.indexOf("WB_cardmore WB_cardmore_noborder S_txt1 clearfix");
			System.out.println(pos);
			pos = html.indexOf("<a href=", pos - 200);
			pos2 = html.indexOf(">", pos +10);
			System.out.println(pos + " " + pos2); 
			String PageURL = html.substring(pos+10, pos2);
			String PostURL = ParseUrl(PageURL);
			System.out.println(PostURL);
			
			String data[] = {TopicID,TopicName,  ReadNumber, DiscussNumber, FanNumber, UserName, UserID};
			con.insert("TopicInf", data);
			TopicFan newFan = new TopicFan(TopicID, FanURL, driver, con);
			newFan.getFanList();
			
			GetPosts newTopic = new GetPosts(TopicID, PostURL, driver, con);
			newTopic.GetPostInfo();
			
		} catch (Exception e){
			e.printStackTrace();
		}
	}
	
	public static String ParseUrl(String url) {
		StringBuffer sBuffer = new StringBuffer("http:");
		int position = url.indexOf('\\');
		int nextPosition = url.indexOf('\\', position+1);
		while (position >= 0) {
			if (nextPosition != -1) {
				sBuffer.append(url.substring(position+1, nextPosition));
				if (url.charAt(nextPosition+1) == '"')
					break;
			}
			else {
				sBuffer.append(url.substring(position+1));
				break;
			}
			position = url.indexOf('\\', nextPosition);
			nextPosition = url.indexOf('\\', position+1);
		}
		return sBuffer.toString();
	}
}


//System.out.println(doc);
/*File file =new File("javaio-appendfile.txt");
if(!file.exists()){
       file.createNewFile();
}
FileWriter fileWritter = new FileWriter(file.getName(),true);
BufferedWriter bufferWritter = new BufferedWriter(fileWritter);
bufferWritter.write(doc.toString());
bufferWritter.close();*/
