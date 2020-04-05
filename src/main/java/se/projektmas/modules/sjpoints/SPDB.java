package se.projektmas.modules.sjpoints;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.LinkedList;
import java.util.List;

enum Point{
    SP,
    JP
}

public class SPDB {
    private static final String sqldriver = "jdbc:sqlite:sp.db";
    private static Connection conn = null;
    
    public static void connect(){
        try{
            conn = DriverManager.getConnection(sqldriver);
            System.out.println("Connection to SQLite has been established!");   
        } catch (SQLException e){
            System.out.println(e.getMessage());
        }

        final String sql = "CREATE TABLE IF NOT EXISTS tally (" +
                            "   id VARCHAR(20) PRIMARY KEY," +
                            "   sp INTEGER," + 
                            "   jp INTEGER);";

        try(Statement stmt = conn.createStatement()){
            stmt.execute(sql);
        } catch(SQLException e){
            System.out.println(e.getMessage());
        }
    }

    public static void increment(String snowflake, Point p){
        String sql = "";
        if(p == Point.JP)
            sql = "INSERT INTO tally VALUES( ? ,0,1) ON CONFLICT(id) DO UPDATE SET jp=jp+1;";
        else
            sql = "INSERT INTO tally VALUES( ? ,1,0) ON CONFLICT(id) DO UPDATE SET sp=sp+1;";

        try(PreparedStatement stmt = conn.prepareStatement(sql)){
            stmt.setString(1, snowflake);
            stmt.executeUpdate();
        } catch(SQLException e){
            System.out.println(e.getMessage());
        }
    }

    public static void decrement(String snowflake, Point p){
        String sql = "INSERT INTO tally VALUES( ? ,0,0) ON CONFLICT(id) DO UPDATE SET ";
        if(p == Point.JP)
            sql += "jp=jp-1;";
        else
            sql += "sp=sp-1;";

        try(PreparedStatement stmt = conn.prepareStatement(sql)){
            stmt.setString(1, snowflake);
            stmt.executeUpdate();
        } catch(SQLException e){
            System.out.println(e.getMessage());
        }
    }

    public static List<TallyRow> getSorted(){
        LinkedList<TallyRow> retlist = new LinkedList<>();

        String sql = "SELECT * FROM tally ORDER BY sp - jp DESC;";

        try(Statement stmt = conn.createStatement()){
            ResultSet rs = stmt.executeQuery(sql);
            while(rs.next()){
                TallyRow tr = new TallyRow();
                tr.snowflake = rs.getString(1);
                tr.sp = rs.getInt(2);
                tr.jp = rs.getInt(3);
                retlist.add(tr);
            }
        } catch(SQLException e){
            System.out.println(e.getMessage());
        }
        
        return retlist;
    }
}