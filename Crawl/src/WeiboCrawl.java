
import java.io.*;
import java.util.ArrayList;
import java.util.List;

import javax.swing.text.Position;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.phantomjs.PhantomJSDriver;

public class WeiboCrawl {
	
	public static void main(String[] args) {
		
		int choice = 2;
		SqlConnect sql = new SqlConnect("weibo");
		sql.connect();
		CookieInfo cookie = new CookieInfo();
		WebDriver driver;
		
		switch (choice) {
			case 1:
				//User ID
				PersonInfo user1 = new PersonInfo("5187664653", sql, cookie);
				user1.getUserInfo();	//UserID			
				PersonInfo user2 = new PersonInfo("1271329254", sql, cookie);
				user2.getUserInfo();	//UserID
				PersonInfo user3 = new PersonInfo("1192329374", sql, cookie);
				user3.getUserInfo();	//UserID
				PersonInfo user4 = new PersonInfo("1223178222", sql, cookie);
				user4.getUserInfo();	//UserID
				PersonInfo user5 = new PersonInfo("1259110474", sql, cookie);
				user5.getUserInfo();	//UserID
				PersonInfo user6 = new PersonInfo("1574684061", sql, cookie);
				user6.getUserInfo();	//UserID
				PersonInfo user7 = new PersonInfo("1669879400", sql, cookie);
				user7.getUserInfo();	//UserID
				PersonInfo user8 = new PersonInfo("1733275311", sql, cookie);
				user8.getUserInfo();	//UserID
				PersonInfo user9 = new PersonInfo("1195354434", sql, cookie);
				user9.getUserInfo();	//UserID
				PersonInfo user10 = new PersonInfo("1195230310", sql, cookie);
				user10.getUserInfo();	//UserID
				
				break;
			case 2:
				//topic
				driver = NewWebDriver();
				try {
					Document doc = Jsoup.connect("https://d.weibo.com/100803?refer=index_hot_new")
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
								
			            String Linkscript = doc.select("script").get(21).toString();
			            int position1 = 0;
			            int count = 0;
			            boolean isUrl = true;
			            String TopicName = null;
			            String TopicUrl = null;
			            while (position1 != -1) {
			            		if (count >= 10) break;
			            		position1 = Linkscript.indexOf("target=\\\"_blank\\\" href", position1);
			                int position2 = Linkscript.indexOf(">", position1);
			                String url = Linkscript.substring(position1+25, position2);
			                int position3 = Linkscript.indexOf("<", position2);
			                
			                if (url.indexOf("\\/u\\/") != -1) {
			                		String UserName = ParseName(Linkscript.substring(position2+1, position3));
			                		String UserUrl = ParseUrl(url);
			                		TopicInfo topic = new TopicInfo(TopicUrl, TopicName, UserUrl, UserName, driver, sql);
			            			topic.getTopicInfo();
			            			System.out.println(TopicUrl); 
			                		position1 = position2;
			                		System.out.println();
			                		count ++;
			                		continue;
			                }
			                TopicUrl = ParseUrl(url);
			                TopicName = ParseName(Linkscript.substring(position2+1, position3));
			                position1 = position2;
			            }
			            //String TopicUrl ="http://weibo.com/p/1008088a58f201bd42b0e12ae6a39522eb91dc?from=faxian_huati"; 
			            //TopicInfo topic = new TopicInfo(TopicUrl);
			        		//topic.getTopicInfo();
			           
			            
					} catch (Exception e) {
						e.printStackTrace();
						driver.close();
					}
					driver.close();
				break;
			case 3:
				//searchword
				GetSearchInfo keyword1 = new GetSearchInfo("四川大学", sql);
				keyword1.GetSearchResult();
				GetSearchInfo keyword2 = new GetSearchInfo("上海交通大学", sql);
				keyword2.GetSearchResult();
				GetSearchInfo keyword3 = new GetSearchInfo("中山大学", sql);
				keyword3.GetSearchResult();
				break;
			default:
				break;
				
		}
	}
	
	public static WebDriver NewWebDriver() {
		PhantomjsInfo info =  new PhantomjsInfo();
		System.setProperty("phantomjs.binary.path",info.GetPath());
	    WebDriver driver = new PhantomJSDriver();
	    driver.manage().window().maximize();
	    String tempurl = "https://login.sina.com.cn/signup/signin.php";
	    while (true) {
	    		driver.get("https://login.sina.com.cn/");
		    System.out.println(driver.getCurrentUrl());
		    WebElement username = driver.findElement(By.id("username"));
		    WebElement password = driver.findElement(By.id("password"));
		    username.sendKeys(info.GetUsername());
		    password.sendKeys(info.GetPassword());
		    WebElement sbtn = driver.findElement(By.cssSelector(".btn_mod .W_btn_a"));
		    sbtn.submit(); 
		    try {
				Thread.sleep(2000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		    if (!driver.getCurrentUrl().equals(tempurl))
		    		break;
		    System.out.println(driver.getCurrentUrl());
	    }
	    System.out.println(driver.getCurrentUrl());
	    return driver;
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
	
	public static String ParseName(String name) {
		StringBuffer sBuffer = new StringBuffer();
		for (int i=0; i<name.length(); i++) {
			if (name.charAt(i) == '\\') {
				i++;
			}
			else sBuffer.append(name.charAt(i));
		}
		return sBuffer.toString();
	}
}



