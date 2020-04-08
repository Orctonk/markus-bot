package se.projektmas.modules.quotes;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.LinkedList;
import java.util.List;

public class QuoteDB {
    private static final String sqldriver = "jdbc:sqlite:markus.db";
    private static Connection conn = null;
    
    public static void connect(){
        try{
            conn = DriverManager.getConnection(sqldriver);
            System.out.println("Connection to SQLite has been established!");   
        } catch (SQLException e){
            System.out.println(e.getMessage());
        }
    }

    public static List<Quote> getAllQuotes(){
        List<Quote> retlist = new LinkedList<>();

        String sql = "SELECT * FROM quotes;";

        try(Statement stmt = conn.createStatement()){
            ResultSet rs = stmt.executeQuery(sql);
            while(rs.next()){
                String quote = rs.getString(2);
                String quotee = rs.getString(3);
                String timestamp = rs.getString(4);
                String sender = rs.getString(5);
                Quote q = new Quote(quote,quotee,sender,timestamp);
                retlist.add(q);
            }
        } catch(SQLException e){
            System.out.println(e.getMessage());
        }

        return retlist;
    }

    public static Quote getRandomQuote(){
        Quote q = null;

        String sql = "SELECT * FROM quotes ORDER BY RANDOM() LIMIT 1;";

        try(Statement stmt = conn.createStatement()){
            ResultSet rs = stmt.executeQuery(sql);
            rs.next();
            String quote = rs.getString(2);
            String quotee = rs.getString(3);
            String timestamp = rs.getString(4);
            String sender = rs.getString(5);
            q = new Quote(quote,quotee,sender,timestamp);
            
        } catch(SQLException e){
            System.out.println(e.getMessage());
        }

        return q;
    }

    public static String getRandomGenQuote(){
        String q = null;

        String sql = "SELECT * FROM genquotes ORDER BY RANDOM() LIMIT 1;";

        try(Statement stmt = conn.createStatement()){
            ResultSet rs = stmt.executeQuery(sql);
            rs.next();
            q = rs.getString(2);
            
        } catch(SQLException e){
            System.out.println(e.getMessage());
        }

        return q;
    }
}