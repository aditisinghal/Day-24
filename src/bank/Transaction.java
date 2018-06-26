/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package bank;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.InputMismatchException;
import java.util.Locale;
import java.util.Scanner;
import static oracle.net.aso.b.a;

/**
 *
 * @author ADITI
 */
public class Transaction {
    Scanner s=new Scanner(System.in);
   
    
    public Connection getDBConnection() throws ClassNotFoundException, SQLException{
        Class.forName("oracle.jdbc.driver.OracleDriver");
        Connection con=DriverManager.getConnection("jdbc:oracle:thin:@localhost:1523:orcl","aditi","aditi");
        return con;
    }
    
    public static void credit(int accNo) {
        try {
            Transaction t=new Transaction();
            Connection con=t.getDBConnection();
            PreparedStatement stmt;
            ResultSet rs;
            double amount=0,accBalance=0;
            System.out.println("Enter the amount to be credited:");
            amount=t.s.nextDouble();
            try {
                if(amount<0) {
                    throw new NegativeTransactionException("Negative Transactions are not allowed!!");
                }
             }catch(NegativeTransactionException e) {
                    System.out.println(e.getMessage());}
                stmt=con.prepareStatement("Select ACCBALANCE from ACCOUNT_INFO where ACCOUNTNO="+"?");
                stmt.setInt(1,accNo);
                rs=stmt.executeQuery();
                if(rs.next()) {
                    accBalance=rs.getDouble(1);
                }
                accBalance=accBalance+amount;
                System.out.println("\nYour account balance after the transaction is: "+accBalance);
                final Locale mylocale=Locale.US;
                String date=LocalDate.now().format(DateTimeFormatter.ofPattern("d-MMM-yy",mylocale)).toUpperCase(mylocale);
                String time=java.time.LocalTime.now().toString().substring(0,8);
                String fileName="C:/Internity/Bank/src/ACCOUNT_INFO/"+accNo+".txt";
                String str=date+", "+time+"      Via Banking APP         "+amount+"       "+0.0+"       "+accBalance;
                try(BufferedWriter bw=new BufferedWriter(new FileWriter(fileName,true))) { 
                    bw.write(str);
                    bw.newLine();
                }
                catch(IOException e) {
                    System.out.println(e);
                }
                stmt=con.prepareStatement("Update ACCOUNT_INFO set ACCBALANCE="+accBalance+" where ACCOUNTNO="+accNo);
                stmt.executeUpdate();
                stmt=con.prepareStatement("Insert into TRANSACTION_INF0 values(?,?,?,?,?,?,?)");
                stmt.setInt(1,accNo);
                stmt.setString(2,date);
                stmt.setString(3,time);
                stmt.setString(4,"viaBankingApp");
                stmt.setDouble(5,amount);
                stmt.setDouble(6,0);
                stmt.setDouble(7,accBalance);
                stmt.executeUpdate();
        }catch(Exception e){System.out.println(e);}
    }
    
    public static void debit(int accNo) {
        try {
            Transaction t=new Transaction();
            Connection con=t.getDBConnection();
            PreparedStatement stmt;
            ResultSet rs;    
            double amount=0,accBalance=0;
            System.out.println("Enter the amount to be debited:");
            amount=t.s.nextDouble();
            try {
                if(amount>30000) {
                    throw new LimitedWithdrawlException("Withdrawl of greater than 30000 not allowed!!");
                }
                if(amount<0) {
                    throw new NegativeTransactionException("Negative Transactions are not allowed!!");
                }
                stmt=con.prepareStatement("Select ACCBALANCE from ACCOUNT_INFO where ACCOUNTNO="+"?");
                stmt.setInt(1,accNo);
                rs=stmt.executeQuery();
                if(rs.next()) {
                    accBalance=rs.getDouble(1);
                }
                if(amount<accBalance) {
                    accBalance=accBalance-amount;
                    System.out.println("\nYour account balance after the transaction is: "+accBalance);
                    final Locale mylocale=Locale.US;
                    String date=LocalDate.now().format(DateTimeFormatter.ofPattern("d-MMM-yy",mylocale)).toUpperCase(mylocale);
                    String time=java.time.LocalTime.now().toString().substring(0,8);
                    String fileName="C:/Internity/Bank/src/ACCOUNT_INFO/"+accNo+".txt";
                    String str=date+", "+time+"      Via Banking APP         "+0.0+"       "+amount+"       "+accBalance;
                    try(BufferedWriter bw=new BufferedWriter(new FileWriter(fileName,true))) { 
                        bw.write(str);
                        bw.newLine();
                    }
                    catch(IOException e) {
                        System.out.println(e);
                    }
                    stmt=con.prepareStatement("Update ACCOUNT_INFO set ACCBALANCE="+accBalance+" where ACCOUNTNO="+accNo);
                    stmt.executeUpdate();
                    stmt=con.prepareStatement("Insert into TRANSACTION_INF0 values(?,?,?,?,?,?,?)");
                    stmt.setInt(1,accNo);
                    stmt.setString(2,date);
                    stmt.setString(3,time);
                    stmt.setString(4,"viaBankingApp");
                    stmt.setDouble(5,0);
                    stmt.setDouble(6,amount);
                    stmt.setDouble(7,accBalance);
                    stmt.executeUpdate();
                }
                else {
                    throw new InsufficientBalanceException("Insufficient Account Balance: "+accBalance);
                }
            }catch(LimitedWithdrawlException | InsufficientBalanceException | NegativeTransactionException e) {
                System.out.println(e.getMessage());
            }
        }catch(Exception e){System.out.println(e);} 
    }
}
