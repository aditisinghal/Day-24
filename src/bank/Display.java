/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package bank;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.stream.Stream;

/**
 *
 * @author ADITI
 */
public class Display {
 
    public Connection getDBConnection() throws ClassNotFoundException, SQLException{
        Class.forName("oracle.jdbc.driver.OracleDriver");
        Connection con=DriverManager.getConnection("jdbc:oracle:thin:@localhost:1523:orcl","aditi","aditi");
        return con;
    }
    
    public static void passbook(int accNo) {
        Path path=Paths.get("C:/Internity/Bank/src/ACCOUNT_INFO/"+accNo+".txt");
        try(Stream<String> stream=Files.lines(path)) {
            stream.forEach(System.out::println);
        }
        catch(IOException e) {
            System.out.println(e);
        }
    }
    
    public static void miniStatement(int accNo) {
        try {
            Display d=new Display();
            Connection con=d.getDBConnection();
            PreparedStatement stmt;
            ResultSet rs;
            stmt=con.prepareStatement("select * from TRANSACTION_INF0  where rownum<=5 and ACCOUNTNO="+accNo+" order by  TRANSACTIONDATE desc, TIME desc ");
            rs=stmt.executeQuery();
            System.out.println("Account no       Date         Time       Particulars      CRAmount        DrAmount      Balance  ");
            while(rs.next()){    
                System.out.println(rs.getInt(1)+"         "+rs.getString(2)+"      "+rs.getString(3)+"      "+rs.getString(4)+"        "+rs.getDouble(5)+"           "+rs.getDouble(6)+"         "+rs.getDouble(7));
            }
        }catch(Exception e){System.out.println(e);}
    }
}
