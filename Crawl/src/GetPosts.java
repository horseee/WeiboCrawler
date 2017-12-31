
import java.awt.Desktop.Action;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import javax.print.attribute.standard.PrinterMessageFromOperator;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.io.*;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.phantomjs.PhantomJSDriver;

import com.sun.jna.platform.win32.Netapi32Util.User;

public class GetPosts {
	String PostUrl;
	WebDriver driver;
	SqlConnect con;
	String TopicID;
	
	public GetPosts(String id, String Url,WebDriver Driver, SqlConnect c) {
		PostUrl = Url;
		driver = Driver;
		con = c;
		TopicID = id;
	}
	
	public void GetPostInfo() {
		
	    int count = 0;
	    try {
	    	while (count < 500) {
	    		List<String> ComUrl = new ArrayList<String>();
		    driver.get(PostUrl);
	        //System.out.println(driver.getPageSource());
	        
		    /*File file =new File("javaio-appendfile"+count +".txt");
		    if(!file.exists()){
		           file.createNewFile();
		    }
		    FileWriter fileWritter = new FileWriter(file.getName(),true);
		    BufferedWriter bufferWritter = new BufferedWriter(fileWritter);
		    bufferWritter.write(driver.getPageSource());
		    bufferWritter.close();*/
		    
	        while (true) {
	        		WebElement lazynode = null;
	        		System.out.println("11111111111111111111111");
	        		Thread.sleep(3000);
	        		try {
	        			//#Pl_Third_App__46 > div > div > div.WB_feed.WB_feed_v3.WB_feed_v4 > div:nth-child(10)
	        			lazynode = driver.findElement(By.cssSelector("div[node-type='lazyload']"));
	        		} catch (Exception e) {
					break;
				}
	        		Actions action = new Actions(driver);
	        		action.moveToElement(lazynode).perform();
	            try {
					Thread.sleep(3000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
	        } 
	        System.out.println("2222222222");
	        //发布时间（转化为绝对时间），（如是）原post发布时间（转化为绝对时间）
	        //评论列表（前50），
	        //发布人，发布人id，来自，link，评论数，转发数，赞数，
	        //本post是否转发，如是）原post发布人，（如是）原post来自,（如是）原post评论数，（如是）原post转发数，（如是）原post赞数
	        List<WebElement> PostInfo = driver.findElements(By.cssSelector("div[action-type='feed_list_item']"));
	        System.out.println(PostInfo.size());
        
	        	for (WebElement EachPost:PostInfo) {
	    	   		WebElement temp = EachPost.findElement(By.cssSelector("a[class='W_f14 W_fb S_txt1']"));
	    	   		String UserName = temp.getAttribute("nick-name");
	    	   		String UserID = temp.getAttribute("usercard");
	    	   		UserID = UserID.substring(3, UserID.indexOf('&'));
	    	   		count ++;
	    	   		System.out.println("微博 "+ count + " :");
	    	   		System.out.println("UserName = " + UserName);
	    	   		System.out.println("UserID = " + UserID);
	    	   		
	    	   		//Time 和 link
	    	   		temp = EachPost.findElement(By.cssSelector("a[node-type='feed_list_item_date']"));
	    	   		int lastpos = temp.getText().indexOf("最后");
	    	   		String UserDate = null;
	    	   		if (lastpos >=0) {
	    	   			UserDate = RelativeDeal(temp.getText().substring(0, lastpos-1));
	    	   		}
	    	   		else UserDate = RelativeDeal(temp.getText());
	    	   		System.out.println("Time = " + UserDate);
	    	   		String UserPostUrl = temp.getAttribute("href");
	    	   		System.out.println("Link = " + UserPostUrl);
	    	   		
	    	   		//来自
	    	   		try {
	    	   			temp = EachPost.findElement(By.cssSelector("div[class='WB_from S_txt2'] :nth-child(2)"));
	    	   		} catch (Exception e) {
	    	   			temp = null;
	    	   		}
	    	   		
	    	   		String Comefrom;
	    	   		if (temp == null)  {
	    	   			Comefrom = "null";
	    	   			System.out.println("来自 = null");
	    	   		}
	    	   		else {
	    	   			Comefrom = temp.getText();
	    	   			System.out.println("来自 = " + Comefrom);
	    	   		}
	    	   			
	    	   	
	    	   		//赞、转发、评论
	    	   		List<WebElement> PostStatus = EachPost.findElements(By.cssSelector("ul[class='WB_row_line WB_row_r4 clearfix S_line2'] > li"));
	    	   		String forward = PostStatus.get(1).findElement(By.cssSelector("span[node-type = 'forward_btn_text'] > span > em:nth-child(2)")).getText();
	    	   		int forNum = GetNum(forward);
	    	   		String Comment = PostStatus.get(2).findElement(By.cssSelector("span[node-type = 'comment_btn_text'] > span > em:nth-child(2)")).getText();
	    	   		int comNum = GetNum(Comment);
	    	   		String like = PostStatus.get(3).findElement(By.cssSelector("span[node-type = 'like_status'] > em:nth-child(2)")).getText();
	    	   		int likeNum = GetNum(like);
	    	   		System.out.println("转发 = " + forNum);
	    	   		System.out.println("评论 = " + comNum);
	    	   		System.out.println("赞 = " + likeNum);
	    	   		if (comNum > 0) {
	    	   			ComUrl.add(UserPostUrl);
	    	   		}
	    	   		
	    	   		List<WebElement> isPosts = EachPost.findElements(By.cssSelector("div[class='WB_detail'] > div")); 
	    	   		if (isPosts.get(isPosts.size()-1).getAttribute("class").equals("WB_feed_expand")) {
	    	   			System.out.println("是否转发 = true");
	    	   			WebElement Origin = EachPost.findElement(By.cssSelector("div[class='WB_expand S_bg1']"));
	    	   			//div.WB_feed_expand > div.WB_expand.S_bg1
	    	   			WebElement OriginInfo = Origin.findElement(By.cssSelector("a[class='W_fb S_txt1'] "));
	    	   			String OriginName = OriginInfo.getAttribute("nick-name");
	    	   			String Oid = OriginInfo.getAttribute("usercard");
	    	   			String OriginID = Oid.substring(3, Oid.indexOf('&'));
	    	   			System.out.println("Origin Name = " + OriginName);
	    	   			System.out.println("Origin ID = " + OriginID);
	    	   			
	    	   			//div.WB_feed_expand > div.WB_expand.S_bg1 > div.WB_func.clearfix > div.WB_from.S_txt2
	    	   			List<WebElement> OriginCome = Origin.findElements(By.cssSelector("div.WB_func.clearfix > div.WB_from.S_txt2 > a"));
	    	   			String OriginTime = RelativeDeal(OriginCome.get(0).getText());
	    	   			String OriginComeFrom = OriginCome.get(1).getText();
	    	   			System.out.println("Origin Time = " + OriginTime);
	    	   			System.out.println("原来自 = " + OriginComeFrom);
	    	   			
	    	   			//div.WB_feed_expand > div.WB_expand.S_bg1 > div.WB_func.clearfix > div.WB_handle.W_fr > ul > li:nth-child(1)
	    	   			//div.WB_feed_expand > div.WB_expand.S_bg1 > div.WB_func.clearfix > div.WB_handle.W_fr > ul > li:nth-child(1) > span > a
	    	   			//div.WB_feed_expand > div.WB_expand.S_bg1 > div.WB_func.clearfix > div.WB_handle.W_fr > ul > li:nth-child(3) > span > a > span > em
	    	   			List<WebElement> OriginStatus = Origin.findElements(By.cssSelector("div.WB_func.clearfix > div.WB_handle.W_fr > ul > li"));
	    	   			String OriginForward = OriginStatus.get(0).findElement(By.cssSelector("span > a")).getText();
	    	   			String OriginComment = OriginStatus.get(1).findElement(By.cssSelector("span > a")).getText();
	    	   			String OriginLike = OriginStatus.get(2).findElement(By.cssSelector("span > a > span > em")).getText();
	    	   			System.out.println("原转发 = " + OriginForward);
	    	   			System.out.println("原评论 = " + OriginComment);
	    	   			System.out.println("原赞 = " + OriginLike);
	    	   			
	    	   			String[] data1 = {TopicID,  UserName, UserID,UserDate, UserPostUrl,Comefrom, forNum+"", comNum+"", likeNum+"", "0"};
	    	   			int res = con.insert("TopicPostInf", data1);
	    	   			if (res == 0) count--;
	    	   			
	    	   			String[] data2 = {PostUrl, OriginName, OriginTime, OriginComeFrom, OriginForward, OriginComment, OriginLike};
	    	   			con.insert("TopicPostOriginInf", data2);
	    	   			
	    	   		}
	    	   		else {
	    	   			System.out.println("是否转发 = false");
	    	   			String[] data = {TopicID,  UserName, UserID,UserDate, UserPostUrl,Comefrom, forNum+"", comNum+"", likeNum+"", "0"};
	    	   			int res = con.insert("TopicPostInf", data);
	    	   			if (res == 0) count--;
	    	   		}
	    	   			
	    	   		System.out.println();
	    	   		
	        }
	        	//下一页
    	   		WebElement Nextpage = driver.findElement(By.cssSelector("[class='page next S_txt1 S_line1']"));
    	   		String NextpageUrl = Nextpage.getAttribute("href");
    	   		PostUrl = NextpageUrl;
    	   		System.out.println(NextpageUrl);
    	   		System.out.println();
    	   		
    	   		CommentList newComment = new CommentList(ComUrl,driver, con);
    	   		newComment.GetComment();
    	   		
    	   		Thread.sleep(5000);
	    	}
        } catch (Exception e) {
        		e.printStackTrace();
        		//driver.close();
		}
		//driver.close();
		
	}
	
	private int GetNum(String str) {
		if (str.equals("转发") ||str.equals("评论")||str.equals("赞"))
			return 0;
		return Integer.valueOf(str);
	}
	
	private String RelativeDeal(String time) {
		int wrongpos = time.indexOf("来自");
		if (wrongpos >0 ) {
			time = time.substring(0, wrongpos-1);
		}
		int p = time.indexOf("分钟前");
		if (p >= 0) {
			int minutedelta = Integer.valueOf(time.substring(0, p));
			SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");//设置日期格式
			Calendar c = Calendar.getInstance();
			int hour = c.get(Calendar.HOUR); 
			int minute = c.get(Calendar.MINUTE) - p;
			String RealTime = df.format(new Date()) + " " + hour + "-" + minute + "-00";
			return RealTime;
		}
		int pos = time.indexOf("今天");
		if (pos >= 0) {
			int pos2 = time.indexOf(':');
			String hour = time.substring(pos+3, pos2);
			String minute = time.substring(pos2 + 1);
			SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");//设置日期格式
			
			String RealTime = df.format(new Date()) + " " + hour + "-" + minute + "-00";
			return RealTime;
		}
		else {
			pos = time.indexOf("月");
			
			if (pos >= 0) {
				int pos2 = time.indexOf("日", pos);
				int pos3 = time.indexOf(":", pos2);
				Calendar c = Calendar.getInstance();
				int year = c.get(Calendar.YEAR); 
				String RealTime = year + "-" + time.substring(0, pos) + "-" + time.substring(pos+1, pos2) + " " + 
							time.substring(pos2+2, pos3) + "-" + time.substring(pos3+1) + "-00";
				return RealTime;
			}
		}
		return time;
	}
}
