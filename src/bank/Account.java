/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bank;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.InputMismatchException;
import java.util.Locale;
import java.util.Scanner;

/**
 *
 * @author ADITI
 */
public class Account {
    Scanner s=new Scanner(System.in);
    PreparedStatement stmt;
    ResultSet rs;
   
    /**
     *
     * @return
     * @throws ClassNotFoundException
     * @throws SQLException
     */
    public Connection getDBConnection() throws ClassNotFoundException, SQLException{
        Class.forName("oracle.jdbc.driver.OracleDriver");
        Connection con=DriverManager.getConnection("jdbc:oracle:thin:@localhost:1523:orcl","aditi","aditi");
        return con;
    }
       
    public int create(String accType, int customerId) {
        int accNo=0;
        double openingBalance=0;
        try { 
            Account a =new Account();
            Connection con=a.getDBConnection();
            stmt=con.prepareStatement("Select max(ACCOUNTNO) from ACCOUNT_INFO");
            rs=stmt.executeQuery();
            if(rs.next()) {
                accNo=rs.getInt(1)+1;
            }
            System.out.println("Your account has been created.");
            System.out.println("Your "+accType+" account number is:"+accNo);
            Path path=Paths.get("C:/Internity/Bank/src/ACCOUNT_INFO/"+accNo+".txt");
            try {
                Files.createFile(path);
            }
            catch(IOException e) {
                System.out.println(e);
            }
            System.out.println("Enter 4-digit pin No:");
            int pinNo=a.s.nextInt();
            final Locale mylocale=Locale.US;
            String date=LocalDate.now().format(DateTimeFormatter.ofPattern("d-MMM-yy",mylocale)).toUpperCase(mylocale);
            String time=java.time.LocalTime.now().toString().substring(0,8);
            System.out.println("Enter an opening balance:");
            openingBalance=a.s.nextDouble();
            String fileName="C:/Internity/Bank/src/ACCOUNT_INFO/"+accNo+".txt";
            String str="Creation Date   Creation Time   Particulars     CR AMOUNT      DR AMOUNT       Balance\n"
                    +date+",    "+time+"      during  creation         "+0.0+"       "+openingBalance+"       "+openingBalance;
            try(BufferedWriter bw=new BufferedWriter(new FileWriter(fileName,true))) { 
                bw.write(str);
                bw.newLine();
            }
            catch(IOException e) {
                System.out.println(e);
            }
            stmt=con.prepareStatement("Insert into ACCOUNT_INFO values(?,?,?,?,?,?,?,?)");
            stmt.setInt(1,accNo);
            stmt.setInt(2,pinNo);
            stmt.setInt(3,customerId);
            stmt.setString(4,date);
            stmt.setString(5,time);
            stmt.setString(6,accType);
            stmt.setDouble(7,openingBalance);
            stmt.setString(8,"ADS1405");
            stmt.executeUpdate();
            stmt=con.prepareStatement("Insert into TRANSACTION_INF0 values(?,?,?,?,?,?,?)");
            stmt.setInt(1,accNo);
            stmt.setString(2,date);
            stmt.setString(3,time);
            stmt.setString(4,"during creation");
            stmt.setDouble(5,openingBalance);
            stmt.setDouble(6,0);
            stmt.setDouble(7,openingBalance);
            stmt.executeUpdate();
            System.out.println("Your account details are:");
            System.out.println("Account No: "+accNo);
            System.out.println("Pin No: "+pinNo);
            System.out.println("CustomerId: "+customerId);
            System.out.println("Account Type: "+accType);
            System.out.println("Date of creation: "+date);
            System.out.println("Account Balance: "+openingBalance);
        }catch(ClassNotFoundException | SQLException e){ System.out.println(e);}
        return accNo;
    }
    
    public void delete(int accNo, int customerId, String accType) {
        try {
            Account a =new Account();
            Connection con=a.getDBConnection();
            System.out.println("Are you sure you want to delete your account\n1:YES\n2:NO");
            int sure=s.nextInt();
            if(sure==1) {
                    stmt=con.prepareStatement("delete from ACCOUNT_INFO where ACCOUNTNO="+accNo);
                    stmt.executeUpdate();
                    stmt=con.prepareStatement("select ACCOUNTNO from ACCOUNT_INFO where CUSTOMERID="+customerId+" and NOT ACCTYPE="+"?");
                    stmt.setString(1,accType);
                    rs=stmt.executeQuery();
                    if(!rs.next()){
                       stmt=con.prepareStatement("delete from CUSTOMER_INFO where CUSTOMERID="+customerId);
                       rs=stmt.executeQuery();
                            }
                    stmt=con.prepareStatement("delete from TRANSACTION_INF0 where ACCOUNTNO="+accNo);
                    stmt.executeUpdate();
                Path path=Paths.get("C:/Internity/Bank/src/ACCOUNT_INFO/"+accNo+".txt");
                try {
                    Files.delete(path);
                    System.out.println("Your account has been closed.");
                }
                catch(IOException e) {
                    System.out.println(e);
                }
            }
        }catch(Exception e){System.out.println(e);}
    }
   
    public int customerDetails(String adhaar) {
        int customerId=0;
        try {
            Account a =new Account();
            Connection con=a.getDBConnection();
            stmt=con.prepareStatement("Select max(CUSTOMERID) from CUSTOMER_INFO");
            rs=stmt.executeQuery();
            if(rs.next()) {
                customerId=rs.getInt(1)+1;
            }
            System.out.println("Your customerId is "+customerId);
            System.out.println("Enter your name:");
            String name=a.s.next();
            System.out.println("Enter your House NO:");
            String houseNo=a.s.next();
            System.out.println("Enter your locality:");
            String locality=a.s.next();
            System.out.println("Enter your city:");
            String city=a.s.next();
            System.out.println("Enter your state:");
            String state=a.s.next();
            System.out.println("Enter your zipcode:");
            String zipCode=a.s.next();
            System.out.println("Enter your date of birth:");
            String dob=a.s.next();
            System.out.println("Enter your Phone NO:");
            String phoneNo=a.s.next();
            stmt=con.prepareStatement("Insert into CUSTOMER_INFO values(?,?,?,?,?,?,?,?,?,?)");
            stmt.setInt(1,customerId);
            stmt.setString(2,name);
            stmt.setString(3,houseNo);
            stmt.setString(4,locality);
            stmt.setString(5,city);
            stmt.setString(6,state);
            stmt.setString(7,zipCode);
            stmt.setString(8,dob);
            stmt.setString(10,adhaar);
            stmt.setString(9,phoneNo);
            stmt.executeUpdate();
        }catch(ClassNotFoundException | SQLException e){System.out.println(e);};
        return customerId;
    }
    
    public void secondMenu(int accNo, int customerId, String accType) {
        int n;
        Account a=new Account();
        System.out.println("Select one of the option if you want to do any of these operations else press 6 to"
                + " return to the previous menu");
        do
        {
            System.out.println("\n1: Debit");
            System.out.println("2: Credit");
            System.out.println("3: Mini Statement");
            System.out.println("4: Passbook");
            System.out.println("5: Delete Account");
            System.out.println("6: Exit");
            n=a.s.nextInt();
            switch(n)
            {
                case 1:
                    Transaction.debit(accNo);
                    break;
                case 2:
                    Transaction.credit(accNo);
                    break;
                case 3:
                    Display.miniStatement(accNo);
                    break;
                case 4:
                    Display.passbook(accNo);
                    break;
                case 5:
                    a.delete(accNo,customerId,accType);
                    break;
                case 6:
                    n=0;
                    break;
                default:
                    System.out.println("Invalid Input");
            }
        }while(n>0);
   }
    
    public static void main(String args[]){  
        try{ 
            Account a =new Account();
            Connection con=a.getDBConnection();
            PreparedStatement stmt;
            ResultSet rs;
            int n;
            do
            {
                System.out.println("1: Create Account");
                System.out.println("2: Already a User");
                System.out.println("3: Exit");
                n=a.s.nextInt();
                switch(n)
                {
                    case 1:
                        int rows=0,customerId=0;
                        String accType="";
                        System.out.println("Enter your Adhaar Card Number:");
                        String ADHAAR=a.s.next();
                        stmt=con.prepareStatement("Select count(ACCOUNTNO) from ACCOUNT_INFO where CUSTOMERID="
                                + "(select CUSTOMERID from CUSTOMER_INFO where ADHAARCARDNO="+"?"+")");
                        stmt.setString(1,ADHAAR);
                        rs=stmt.executeQuery();
                        if(rs.next()) {
                          rows=rs.getInt(1);  
                        }
                        if(rows==2)
                            System.out.println("You already have an account!!");
                        else if(rows==1) {
                            stmt=con.prepareStatement("Select CUSTOMERID,ACCTYPE from ACCOUNT_INFO where CUSTOMERID="
                                + "(select CUSTOMERID from CUSTOMER_INFO where ADHAARCARDNO="+"?"+")");
                            stmt.setString(1,ADHAAR);
                            rs=stmt.executeQuery();
                            if(rs.next()) {
                                accType=rs.getString(2);
                                customerId=rs.getInt(1);
                            }
                            System.out.println("You already have a "+accType+" account");
                            System.out.println("Enter 1 if you want to create account of another type else enter another key:");
                            String input=a.s.next();
                            if(input.equals("1")){
                                if(accType.equals("Savings"))
                                    a.create("Current",customerId);
                                else
                                    a.create("Savings",customerId);           
                            }
                            else {
                                System.out.println("Invalid input");
                                break;
                            }
                        }
                        else {
                            int inputAccType=0,accNo=0;
                            customerId=a.customerDetails(ADHAAR);
                            do {
                                try {
                                    System.out.println("Enter 1 for savings and 2 for current:");
                                    inputAccType=a.s.nextInt();
                                    if(inputAccType==1||inputAccType==2)
                                        break;
                                    else {
                                        System.out.println("Invalid Input");
                                        inputAccType=0;
                                    }
                                }
                                catch(InputMismatchException e){
                                    System.out.println("Invalid Input");
                                }
                                a.s.nextLine();
                            }while(inputAccType<=0); 
                            if(inputAccType==1)
                                accNo=a.create("Savings",customerId);
                            else
                                accNo=a.create("Current",customerId);
                        }   
                        break;
                    case 2:
                        int accNo=0, inputPinNo=0,dbPinNo;
                        System.out.println("Enter your Account No:");
                        accNo=a.s.nextInt();
                        stmt=con.prepareStatement("Select PINNO,CUSTOMERID,ACCTYPE from ACCOUNT_INFO where ACCOUNTNO="+"?");
                        stmt.setInt(1,accNo);
                        rs=stmt.executeQuery();
                        if(rs.next()) {
                            dbPinNo=rs.getInt(1);
                            System.out.println("Enter your 4 digit pin number:");
                            inputPinNo=a.s.nextInt();
                            if(inputPinNo==dbPinNo) 
                                a.secondMenu(accNo,rs.getInt(2),rs.getString(3));
                            else
                                System.out.println("Wrong pin number!!");
                        }
                        else
                            System.out.println("You are not a registered user!!");
                        break;    
                    case 3:
                        n=0;
                        break;
                    default:
                        System.out.println("Invalid Input");
                }
            }while(n>0);
        }catch(ClassNotFoundException | SQLException e){ System.out.println(e);}   
    }
}
