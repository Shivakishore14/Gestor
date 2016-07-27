package gestor.server;
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import java.util.*;
import java.text.*;
import java.io.*;
import java.sql.*;
/**
 *
 * @author root
 */
public class util {
    static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";  
    static final String DB_URL = "jdbc:mysql://localhost/gestor";

    static final String USER = "root";
    static final String PASS = "sken";

    static Connection conn = null;
    static Statement stmt = null;
    public static int fun(){
        try{
            Class.forName("com.mysql.jdbc.Driver");
            conn = DriverManager.getConnection(DB_URL, USER, PASS);
            stmt = conn.createStatement();
            String sql = "SELECT * FROM clients ";
            ResultSet rs = stmt.executeQuery(sql);
            if(rs.first())
                return 1;
            else
                return 0;
            
        }catch(Exception e){
            e.printStackTrace();
        }
        return 1;
    }
    public static int newClient( String ip, String name ){
	String query = "insert into clients values('" + ip + "', '" + name + "', 'online');";
	try(Connection con = DriverManager.getConnection(DB_URL,USER,PASS); Statement stmt = con.createStatement()) {
	    stmt.executeUpdate(query);
	}catch(SQLIntegrityConstraintViolationException eI){
	    System.out.println(eI);
	}catch(Exception e){
	    e.printStackTrace();
	    return 0;
	}
	return 1;
    }
}
