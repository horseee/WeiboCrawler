
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.phantomjs.PhantomJSDriver;

public class TopicFan {
	String FanUrl;
	WebDriver driver;
	SqlConnect con;
	String TopicID;
	
	public TopicFan(String ID, String url, WebDriver Driver, SqlConnect c) {
		FanUrl = url;
		driver = Driver;
		con = c;
		TopicID = ID;
	}

	public void getFanList() {
		int count = 0;
	    //try {
	    	while (count < 100) {
		    driver.get(FanUrl);
		    //System.out.println(driver.getPageSource());
		    List<WebElement> Fans = driver.findElements(By.cssSelector("li[class='follow_item S_line2']"));
		    for (WebElement Fan:Fans) {
		    		count ++;
		    		String FanName = (Fan.findElement(By.cssSelector("dl > dd.mod_info.S_line1 > div.info_name.W_fb.W_f14 > a.S_txt1 > strong")).getText());
		    		System.out.println(""+count + ":"+ FanName);
		    		String[] data = {TopicID, FanName};
		    		int res =con.insert("TopicFan", data);
		    		if (res == 0) count --;
		    		if (count == 100) break;
		    }
		    if (count == 100) break;
		    WebElement NextUrl = driver.findElement(By.cssSelector("a[class='page next S_txt1 S_line1']"));
		    FanUrl = NextUrl.getAttribute("href");
		    try {
		    		Thread.sleep(2000);
		    } catch (Exception e) {
				// TODO: handle exception
		    		e.printStackTrace();
			}
		    
	    	}
	}
}
