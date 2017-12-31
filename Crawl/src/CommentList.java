import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import java.io.*;

public class CommentList {
	WebDriver driver;
	List<String> urls;
	String NowUrl;
	SqlConnect con;
	
	public CommentList(List<String> URL, WebDriver Driver, SqlConnect c) {
		urls = URL;
		driver = Driver;
		con = c;
	}
	
	public void GetComment() {
		System.out.println(urls.size());
		for (String url:urls) {
			System.out.println("\n" + url + " :");
			NowUrl = url;
			driver.get(url);
			
			//test empty
			/*WebElement TestEmpty = driver.findElement(By.cssSelector("div[class='list_ul'] > div"));
			if(TestEmpty.getAttribute("class").equals("WB_empty")) {
				System.out.println("No comment");
				continue;
			}*/
			int count = 0;
			
			while (true) {
		        	WebElement lazynode = null;
		        	System.out.println("11111111111111111111111");
		        	try {
					Thread.sleep(3000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
		        	try {
		        		lazynode = driver.findElement(By.cssSelector("div[node-type='comment_loading']"));
		        	} catch (Exception e) {
					break;
				}
		        	Actions action = new Actions(driver);
		        	action.moveToElement(lazynode).perform();
		        try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
		    	}
			try {
				WebElement clickforMore = driver.findElement(By.cssSelector("a[action-type='click_more_comment']"));
				clickforMore.click();
				Thread.sleep(1000);
			} catch (Exception e) {
			}
					
			System.out.println("2222222222");
		
			//div.repeat_list > div:nth-child(2) > div > div > div:nth-child(1) > div.list_con > div.WB_text > a:nth-child(3)
				
			List<WebElement> Comments = driver.findElements(By.cssSelector("div[node-type='root_comment']"));
			if(Comments.size() == 0) {
				try {
					File file =new File(count + "java.txt");
					if(!file.exists()){
					       file.createNewFile();
					}
					FileWriter fileWritter = new FileWriter(file.getName(),true);
					BufferedWriter bufferWritter = new BufferedWriter(fileWritter);
					bufferWritter.write(driver.getPageSource());
					bufferWritter.close();
				} catch (Exception e) {
					// TODO: handle exception	
				}
			}
			
			for (int i=0; i<Comments.size(); i++) {
				count ++;
				System.out.println(count + ": ");
				WebElement comment = Comments.get(i);
				String content = comment.findElement(By.cssSelector("div[class='WB_text']")).getText();
				System.out.println(content);
				String[] data = {NowUrl, content};
				con.insert("TopicPostComment", data);
			}
			
		}
		
				
	}
}
