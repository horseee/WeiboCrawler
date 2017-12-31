
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import com.mysql.jdbc.DatabaseMetaData;
import com.mysql.jdbc.ResultSetMetaData;

public class SqlConnect {
	
	private static Connection con;
    private static String driver = "com.mysql.jdbc.Driver";
    private static String user = "root";
    private static String password = "maxinyin";
    private static String DbName;
    
    SqlConnect(String name) {
    		DbName = name;
    }
	
	public void connect(){
		String url = "jdbc:mysql://localhost:3306/"+ DbName+"?useSSL=false";
		try {
            //加载驱动程序
            Class.forName(driver);
            //1.getConnection()方法，连接MySQL数据库！！
            con = DriverManager.getConnection(url,user,password);
            if(!con.isClosed())
                System.out.println("Succeeded connecting to the Database!");
		} catch(ClassNotFoundException e) {   
            System.out.println("Sorry,can`t find the Driver!");   
            e.printStackTrace();   
        } catch(SQLException e) {
            e.printStackTrace();  
        } catch (Exception e) {
            e.printStackTrace();
        } finally{
            System.out.println("成功连接数据库！！");
        }
	}
	
	public void Select(String TableName) {
		if (con == null) {
			System.out.println("No connection! please connect to the database first!");
			return;
		}
		try {
			Statement statement = con.createStatement();
			
	        //要执行的SQL语句
	        String sql = "select * from " + TableName;
	        //3.ResultSet类，用来存放获取的结果集！！
	        ResultSet rs = statement.executeQuery(sql);
	        System.out.println();
	        System.out.println(sql);
	        System.out.println("----------------------");
	        System.out.println("查找结果如下所示:"); 
	        System.out.println("----------------------"); 
	        ResultSetMetaData rsmd = (ResultSetMetaData) rs.getMetaData();
	        int ColumnCount = rsmd.getColumnCount();
	        for (int i=1; i<ColumnCount; i++) 
	        		System.out.print(rsmd.getColumnName(i)+"\t");
	        System.out.println(rsmd.getColumnName(ColumnCount));  
	        System.out.println("----------------------");  
	         
	        String name = null;
	        String id = null;
	        int count = 0;
	        while(rs.next()){
	            for (int i=1; i<ColumnCount; i++) 
	            		System.out.print(rs.getString(i) + "\t");
	            System.out.println(rs.getString(ColumnCount));
	            count ++;
	        }
	        if (count == 0) System.out.println();
	        System.out.println("----------------------");  
	        if (count > 1) 
	        		System.out.println(count + " rows in set"); 
	        else 
	        		System.out.println(count + " row in set"); 
	        System.out.println("----------------------");  
	        rs.close();
		} catch (SQLException e) {
			e.printStackTrace();   
		} 
	}

	public void Select(String TableName, String SelectCond) {
		
		if (con == null) {
			System.out.println("No connection! please connect to the database first!");
			return;
		}
		try {
			Statement statement = con.createStatement();
	        //要执行的SQL语句
	        String sql = "select " + SelectCond + " from " + TableName;
	        //3.ResultSet类，用来存放获取的结果集！！
	        ResultSet rs = statement.executeQuery(sql);
	        System.out.println();
	        System.out.println(sql);
	        System.out.println("----------------------");
	        System.out.println("查找结果如下所示:");  
	        System.out.println("----------------------");  
	        ResultSetMetaData rsmd = (ResultSetMetaData) rs.getMetaData();
	        int ColumnCount = rsmd.getColumnCount();
	        for (int i=1; i<ColumnCount; i++) {
	        		String name = rsmd.getColumnName(i);
	        		System.out.print(name+"\t");
	        }
	        		
	        System.out.println(rsmd.getColumnName(ColumnCount));    
	        System.out.println("----------------------");  
	         
	        String name = null;
	        String id = null;
	        int count = 0;
	        while(rs.next()){
	            for (int i=1; i<ColumnCount; i++) 
	            		System.out.print(rs.getString(i) + "\t");
	            System.out.println(rs.getString(ColumnCount));
	            count ++;
	        }
	        if (count == 0) System.out.println();
	        System.out.println("----------------------");  
	        if (count > 1) 
	        		System.out.println(count + " rows in set"); 
	        else 
	        		System.out.println(count + " row in set"); 
	        System.out.println("----------------------");  
	        rs.close();
		} catch (SQLException e) {
			e.printStackTrace();   
		}
		
	}
	
	public void Select(String TableName, String SelectCond, String where) {
		if (con == null) {
			System.out.println("No connection! please connect to the database first!");
			return;
		}
		try {
			Statement statement = con.createStatement();
	        //要执行的SQL语句
	        String sql = "select " + SelectCond + " from " + TableName + " where " + where;
	        //3.ResultSet类，用来存放获取的结果集！！
	        System.out.println();
	        System.out.println(sql);
	        ResultSet rs = statement.executeQuery(sql);
	        System.out.println("----------------------");
	        System.out.println("查找结果如下所示:");  
	        System.out.println("----------------------");  
	        ResultSetMetaData rsmd = (ResultSetMetaData) rs.getMetaData();
	        int ColumnCount = rsmd.getColumnCount();
	        for (int i=1; i<ColumnCount; i++) {
	        		String name = rsmd.getColumnName(i);
	        		System.out.print(name+"\t");
	        }
	        		
	        System.out.println(rsmd.getColumnName(ColumnCount));    
	        System.out.println("----------------------");  
	         
	        String name = null;
	        String id = null;
	        int count = 0;
	        while(rs.next()){
	            for (int i=1; i<ColumnCount; i++) 
	            		System.out.print(rs.getString(i) + "\t");
	            System.out.println(rs.getString(ColumnCount));
	            count ++;
	        }
	        if (count == 0) System.out.println();
	        System.out.println("----------------------");  
	        if (count > 1) 
	        		System.out.println(count + " rows in set"); 
	        else 
	        		System.out.println(count + " row in set"); 
	        System.out.println("----------------------");  
	        rs.close();
		} catch (SQLException e) {
			e.printStackTrace();   
		}
	}
	
	public int insert(String TableName, String[] ValueList) {
		if (con == null) {
			System.out.println("No connection! please connect to the database first!");
		}
		try {
			Statement statement = con.createStatement();
			int length = ValueList.length;
	        //要执行的SQL语句
	        String sql = "insert into " + TableName + " values (";
	        for (int i=0; i<length-1; i++)
	        		sql += "'" + ValueList[i] + "',";
	        sql += "'" + ValueList[length-1] + "'";
	        	sql = sql + 	")";
	        //3.ResultSet类，用来存放获取的结果集！！
	        //INSERT INTO Persons VALUES ('Gates', 'Bill', 'Xuanwumen 10', 'Beijing')
	        System.out.println(sql);
	        boolean res= statement.execute(sql);
	        return 1;
		} catch (SQLException e) {
			System.out.println("Duplicate key!");
			return 0;
		}
	}
	
	
	
	public void delete(String TableName) {
		if (con == null) {
			System.out.println("No connection! please connect to the database first!");
			return;
		}
		try {
			Statement statement = con.createStatement();
	        //要执行的SQL语句
	        String sql = "delete from " + TableName;
	        System.out.println();
	        System.out.println(sql);
	        boolean res= statement.execute(sql);
	        System.out.println("Delete sucess!");
		} catch (SQLException e) {
			System.out.println("Delete Error");
		}
	}
	
	public void delete(String TableName, String where) {
		if (con == null) {
			System.out.println("No connection! please connect to the database first!");
			return;
		}
		try {
			Statement statement = con.createStatement();
	        //要执行的SQL语句
	        String sql = "delete from " + TableName + " where " + where;
	        System.out.println();
	        System.out.println(sql);
	        boolean res= statement.execute(sql);
	        System.out.println("Delete sucess!");
		} catch (SQLException e) {
			System.out.println("Delete Error");
		}
	}
	
	public void update(String TableName, String set, String where) {
		if (con == null) {
			System.out.println("No connection! please connect to the database first!");
			return;
		}
		try {
			Statement statement = con.createStatement();
	        //要执行的SQL语句
			//UPDATE Person SET FirstName = 'Fred' WHERE LastName = 'Wilson' 
	        String sql = "update " + TableName + " set " + set +
	        		" where " + where;
	        System.out.println();
	        System.out.println(sql);
	        boolean res= statement.execute(sql);
	        System.out.println("Update sucess!");
		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("Update Error");
		}
	}
	
	public void close() {
		try {
			con.close();
			System.out.println();
			System.out.println("成功断开与数据库的连接");
		} catch (Exception e){
			e.printStackTrace();
		}
		
	}
	
}