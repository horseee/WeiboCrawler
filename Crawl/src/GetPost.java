
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import javax.swing.table.TableModel;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

//发布时间（转化为绝对时间）（如是）原post发布时间（转化为绝对时间）,（如是）原post来自，


//id-post（前500）：link，发布人，发布人id，发布时间（转化为绝对时间），来自，评论数，评论列表（前50），转发数，赞数
//本post是否转发，（如是）原post发布人，（如是）原post发布时间（转化为绝对时间），（如是）原post来自，（如是）原post评论数，（如是）原post转发数，（如是）原post赞数

// link, 发布人，发布人id， 转发数，赞数 ，来自，评论数，
// 本post是否转发，（如是）原post发布人,（如是）原post评论数，（如是）原post转发数，（如是）原post赞数

public class GetPost {
	Document doc;
	int count = 0;
	String name;
	String ID;
	SqlConnect con;
	CookieInfo cookie;
	
	GetPost(Document Doc, String Name, String Id, SqlConnect Con, CookieInfo c) {
		doc = Doc;
		name = Name;
		ID = Id;
		con = Con;
		cookie = c;
	}
	
	public void getWeibo() {
		while (count < 500) {
			try {
				Thread.sleep(3000);
			} catch (Exception e1) {
				e1.printStackTrace();
			}
			int Support = 0;
			Elements posts = doc.select("[class=c][id]");
			for (Element post:posts) {
				count ++;
				try {
					Thread.sleep(2000);
				} catch (InterruptedException e1) {
					e1.printStackTrace();
				}
				
				Elements isPost = post.select("div");
				Elements postInf = isPost.get(isPost.size()-1).select("a");
				int size= postInf.size();
				int Forward = GetNumber(postInf.get(size-3).text());
				String Posturl = postInf.get(size-3).attr("href");
				int Comment = GetNumber(postInf.get(size-2).text());
				String CommentUrl = postInf.get(size-2).attr("href");
				try {
					Support = GetNumber(postInf.get(size-4).text());
				} catch (Exception e) {
					Support = GetNumber(post.select("[class=cmt]").text());
				}
				System.out.println("\n微博 " + count + ":");
				System.out.println("name = " + name);
				System.out.println("ID = " + ID);
				System.out.println("赞数 = " + Support);
				System.out.println("转发 = " + Forward);
				System.out.println("评论 = " + Comment);
				System.out.println("URL = " + Posturl);
				
				String time = post.select("[class=ct]").text();
				String RelativeTime = RelativeDeal(time);
				int position = time.indexOf("来自");
				String comefrom = null;
				if (position < 0) {
					System.out.println("来自 = null");
				}
				else  {
					comefrom = time.substring(position + 2);
					System.out.println("来自 = " + comefrom);
				}
				System.out.println("Time = " + RelativeTime);
				
				if (isPost.size()  == 3) {	
					System.out.println("是否转发 =  true");
					
					try {
						Element origin = isPost.get(1);
						Element originUser = origin.select("[class=cmt]").select("a").get(0);
						String OriginName = originUser.text();
						String OriginUserurl = originUser.attr("href");
						System.out.println("OriginName = " + OriginName);
						
						Elements inf = origin.select("[class=cmt]");
						int OriginSupport = GetNumber(inf.get(1).text());
						int OriginForward = GetNumber(inf.get(2).text());
						
						Elements OriginInf = isPost.get(1).select("a");
						int hrefsize = OriginInf.size();
						int OriginComment = GetNumber((OriginInf.get(hrefsize-1)).text());
						String OriginUrl = (OriginInf.get(hrefsize-1)).attr("href");
						String OriginTime = GetOriginTime(OriginUrl);
						String OriginRelativeTime = RelativeDeal(OriginTime);
						String OriginComefrom = GetOriginComefrom(OriginUserurl);
						if (OriginComefrom == null) 
							OriginComefrom = "null";
						
						System.out.println("原赞数 = " + OriginSupport);
						System.out.println("原转发 = " + OriginForward);
						System.out.println("原评论 = " + OriginComment);
						System.out.println("原时间 = " + OriginRelativeTime);
						System.out.println("原来自 = " + OriginComefrom);
						
						String[] data = {Posturl ,name, ID, RelativeTime, comefrom, Comment+"", Forward+"", Support+"", "1"};
						int insres = con.insert("UserPostInf", data);
						if (insres == 0) {
							count --;
							continue;
						}
						
						String[] data2 = {Posturl, OriginName,OriginRelativeTime, OriginComefrom,  OriginComment+"" , OriginForward+"", OriginSupport+""};
						con.insert("UserPostOriginInf", data2);
						
					} catch (Exception e) {
						String[] data = {Posturl ,name, ID, RelativeTime, comefrom, Comment+"", Forward+"", Support+"", "1"};
						int insres = con.insert("UserPostInf", data);
						if (insres == 0) {
							count --;
							continue;
						}

						String[] data2 = {Posturl, "原微博已删除", "1970-1-1 12-00-00", "null",  "0" , "0", "0"};
						con.insert("UserPostOriginInf", data2);
				
						GetCommentList(CommentUrl);
						continue;
					}
					
					
				} else {
					String[] data = {Posturl ,name, ID, RelativeTime, comefrom, Comment+"", Forward+"", Support+"", "0"};
					int insres = con.insert("UserPostInf", data);
					if (insres == 0) {
						count --;
						continue;
					}
					System.out.println("是否转发 =  false");
				}
				GetCommentList(CommentUrl);
				
			}
			if (count >= 500) break;
			String URL = doc.select("div[class=pa][id=pagelist]").select("a").attr("href");
			URL = "https://weibo.cn/" + URL;
			try {
				doc = Jsoup.connect(URL)
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
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	private int GetNumber(String temp) {
		int position1 = temp.indexOf('[');
		int position2 = temp.indexOf(']');
		int number = 0;
		try {
			number = Integer.valueOf(temp.substring(position1+1, position2));
		} catch (Exception e) {
			number = 0;
		}
		return number;
	}
	
	private String GetOriginTime(String url) {
		try {
			Thread.sleep(1000);
			Document Doc = Jsoup.connect(url)
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
			return Doc.select("[class=ct]").get(0).text();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	
	private String GetOriginComefrom(String url) {
		try {
			Thread.sleep(1000);
			Document Doc = Jsoup.connect(url)
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
			String temp = Doc.select("[class=ct]").get(0).text();
			return temp.substring(temp.indexOf("来自") + 2);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	private void GetCommentList(String url) {
		String formerurl = url;
		int commentCount = 0;
		try {
			while (commentCount < 50) {
				try {
					Thread.sleep(2000);
				} catch (InterruptedException e1) {
					e1.printStackTrace();
				}
				Document Doc = Jsoup.connect(url)
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
					Elements Comments = Doc.select("[class=c][id]");
					for (Element comment:Comments) {
						commentCount ++;
						String Content = comment.select("[class=ctt]").text();
						System.out.println("Comment " + commentCount + " : " + Content);
						String data[] = {formerurl, Content};
						con.insert("UserPostComment", data);
					}
				url = Doc.select("div[class=pa][id=pagelist]").select("a").attr("href");
				url = "https://weibo.cn/" + url;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
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
