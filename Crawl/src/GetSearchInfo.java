import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import javax.print.Doc;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

//3,10个搜索词（可以指定并写死）
// 发布时间（转化为绝对时间），（如是）原post发布时间（转化为绝对时间），（如是）原post来自，
// 评论列表（前50），

//（1）.搜索结果post（前500）：link，来自，发布人，发布人id，
// 评论数，转发数，赞数，本post是否转发
// (如是）原post发布人，（如是）原post评论数，（如是）原post转发数，（如是）原post赞数

public class GetSearchInfo {
	Document doc;
	String Searchurl;
	int count = 0;
	int index = 1;
	SqlConnect con;
	String Key;
	
	public GetSearchInfo(String keyWord, SqlConnect c) {
		con = c;
		Key = keyWord;
	}
	
	public void GetSearchResult() {
		while (count < 500) {
			Searchurl = "https://weibo.cn/search/mblog?hideSearchFrame=&keyword="+ Key + "&page=" + String.valueOf(index);
			System.out.println(Searchurl);
			try {
				Thread.sleep(5000);
				doc = Jsoup.connect(Searchurl)
						.ignoreContentType(true)
						.header("Host","weibo.cn")
						.header("Accept","text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8")
						.header("Connection","keep-alive")
						.header("Accept-Language","zh-cn")
						.header("Accept-Encoding","gzip, deflate")
						.header("User-Agent", new CookieInfo().GetUserAgent())
						.header("Cookie",new CookieInfo().GetCnCookie())
						.get();
				//System.out.println(doc);
				
				Elements posts = doc.select("[class=c][id]");
				for (Element post:posts) {
					int Support = 0;
					count += 1;
					Thread.sleep(1000);
					Elements isPost = post.select("div");
					
					Elements postInf = isPost.get(1).select("> a");
					String PostName = postInf.get(0).text();
					String PostID = GetUserId(postInf.get(0).attr("href"));
					System.out.println("微博 "+ count + " :");
					System.out.println("Name = " + PostName);
					System.out.println("ID = " + PostID);
					
					postInf = isPost.get(isPost.size()-1).select("> a");
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
					
					System.out.println("赞数 = " + Support);
					System.out.println("转发 = " + Forward);
					System.out.println("评论 = " + Comment);
					System.out.println("URL = " + Posturl);
					
					String time = post.select("[class=ct]").text();
					String Comefrom = "null";
					int position = time.indexOf("来自");
					if (position < 0) {
						System.out.println("来自 = null");
						time = RelativeDeal(time);
						System.out.println("Time = " + time);
					}
					else  {
						Comefrom = time.substring(position + 2);
						System.out.println("来自 = " + Comefrom);
						time = RelativeDeal(time.substring(0, position-1));
						System.out.println("Time = " + time);
					}
					
					if (isPost.size()  == 4) {	
						System.out.println("是否转发 =  true");
						
						Element origin = isPost.get(1);
						String OriginName = origin.select("[class=cmt]").select("a").get(0).text();
						String OriginUserURL = origin.select("[class=cmt]").select("a").attr("href");
						System.out.println("OriginName = " + OriginName);
						
						origin = isPost.get(2);
						Elements inf = origin.select("[class=cmt]");
						int OriginSupport = GetNumber(inf.get(0).text());
						int OriginForward = GetNumber(inf.get(1).text());
						int OriginComment = GetNumber(origin.select("[class=cc]").text());
						String OriginURL = origin.select("[class=cc]").attr("href");
						String OriginTime = RelativeDeal(GetOriginTime(OriginURL));
						String OriginComefrom = GetOriginComefrom(OriginUserURL);
						
						System.out.println("原赞数 = " + OriginSupport);
						System.out.println("原转发 = " + OriginForward);
						System.out.println("原评论 = " + OriginComment);
						System.out.println("原时间 = " + OriginTime);
						System.out.println("原来自 = " + OriginComefrom);
						String[] data1 = {Key, Posturl, PostName, PostID, time, Comefrom, Comment+"", Forward+"", Support+"", "1"};
						int res = con.insert("SearchPostInf", data1);
						if (res == 0) {
							count --;
							continue;
						}
						String[] data2 = {Posturl, OriginName, OriginTime, OriginComefrom, OriginComment+"", OriginForward+"", OriginSupport+""};
						con.insert("SearchPostOriginInf", data2);
						
						/*Elements OriginInf = isPost.get(1).select("a");
						int hrefsize = OriginInf.size();
						int OriginComment = GetNumber((OriginInf.get(hrefsize-1)).text());
						String OriginUrl = (OriginInf.get(hrefsize-1)).attr("href");
						String OriginTime = GetOriginTime(OriginUrl);
						
						System.out.println("原赞数 = " + OriginSupport);
						System.out.println("原转发 = " + OriginForward);
						System.out.println("原评论 = " + OriginComment);
						System.out.println("原时间 = " + OriginTime);*/
				
					} else {
						if (isPost.size()  == 2) {
							System.out.println("是否转发 =  false");
							String[] data1 = {Key, Posturl, PostName, PostID, time, Comefrom, Comment+"", Forward+"", Support+"", "1"};
							int res = con.insert("SearchPostInf", data1);
							if (res == 0) {
								count --;
								continue;
							}
						}
						else try {
							isPost.get(1).select("[class=cmt]");
							
							Element origin = isPost.get(1);
							String OriginName = origin.select("[class=cmt]").select("a").get(0).text();
							String OriginUserURL = origin.select("[class=cmt]").select("a").attr("href");
							System.out.println("OriginName = " + OriginName);
							
							Elements inf = origin.select("[class=cmt]");
							int OriginSupport = GetNumber(inf.get(1).text());
							int OriginForward = GetNumber(inf.get(2).text());
			
							Elements OriginInf = isPost.get(1).select("a");
							int hrefsize = OriginInf.size();
							int OriginComment = GetNumber((OriginInf.get(hrefsize-1)).text());
							String OriginUrl = (OriginInf.get(hrefsize-1)).attr("href");
							String OriginTime = RelativeDeal(GetOriginTime(OriginUrl));
							String OriginComefrom = GetOriginComefrom(OriginUserURL);
							
							System.out.println("是否转发 =  true");
							System.out.println("原赞数 = " + OriginSupport);
							System.out.println("原转发 = " + OriginForward);
							System.out.println("原评论 = " + OriginComment);
							System.out.println("原时间 = " + OriginTime);
							System.out.println("原来自 = " + OriginComefrom);
							
							String[] data1 = {Key, Posturl, PostName, PostID, time, Comefrom, Comment+"", Forward+"", Support+"", "1"};
							int res = con.insert("SearchPostInf", data1);
							if (res == 0) {
								count --;
								continue;
							}
							String[] data2 = {Posturl, OriginName, OriginTime, OriginComefrom, OriginComment+"", OriginForward+"", OriginSupport+""};
							con.insert("SearchPostOriginInf", data2);
							
						} catch (Exception e) {
							System.out.println("是否转发 =  false");
							String[] data1 = {Key, Posturl, PostName, PostID, time, Comefrom, Comment+"", Forward+"", Support+"", "1"};
							int res = con.insert("SearchPostInf", data1);
							if (res == 0) {
								count --;
								continue;
							}
						}
							
					}
					if (Comment != 0)
						GetCommentList(CommentUrl, Comment);
					System.out.println();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			index ++;
		}
	}
	
	private int GetNumber(String temp) {
		int position1 = temp.indexOf('[');
		int position2 = temp.indexOf(']');
		//System.out.println(temp.substring(position1+1, position2));
		int number = Integer.valueOf(temp.substring(position1+1, position2));
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
					.header("User-Agent",new CookieInfo().GetUserAgent())
					.header("Cookie",new CookieInfo().GetCnCookie())
					.get();
			return Doc.select("[class=ct]").get(0).text();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	private String GetUserId(String url) {
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
					.header("User-Agent",new CookieInfo().GetUserAgent())
					.header("Cookie",new CookieInfo().GetCnCookie())
					.get();
			String IDString = Doc.select("[valign=top]").select("a").attr("href");
			int position1 = IDString.indexOf('/');
			int position2 = IDString.indexOf('/', position1+1);
			String ID = IDString.substring(position1+1, position2);
			return ID;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	private void GetCommentList(String CommentUrl, int commentNumber) {
		int commentCount= 0;
		String FormerURL = CommentUrl;
		try {
			while (commentCount < commentNumber && commentCount < 50) {
				Thread.sleep(1000);
				Document Doc = Jsoup.connect(CommentUrl)
						.ignoreContentType(true)
						.header("Referer","https://weibo.cn/search/")
						.header("Host","weibo.cn")
						.header("Accept","text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8")
						.header("Connection","keep-alive")
						.header("Accept-Language","zh-cn")
						.header("Accept-Encoding","gzip, deflate")
						.header("User-Agent",new CookieInfo().GetUserAgent())
						.header("Cookie",new CookieInfo().GetCnCookie())
						.get();
					Elements Comments = Doc.select("[class=c][id]");
					for (Element comment:Comments) {
						if (commentCount >= commentNumber)
							return;
						String Content = comment.select("[class=ctt]").text();
						commentCount ++;
						System.out.println("Comment " + commentCount + " : " + Content);
						String[] data = {FormerURL, Content};
						con.insert("SearchPostComment", data);
						//System.out.println(comment);
					}
					Comments.clear();
				if (commentCount >= commentNumber) {
					CommentUrl = Doc.select("div[class=pa][id=pagelist]").select("a").attr("href");
					CommentUrl = "https://weibo.cn/" + CommentUrl;
				}
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
					.header("User-Agent",new CookieInfo().GetUserAgent())
					.header("Cookie",new CookieInfo().GetCnCookie())
					.get();
			String temp = Doc.select("[class=ct]").get(0).text();
			return temp.substring(temp.indexOf("来自") + 2);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
}
