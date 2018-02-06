import com.mysql.fabric.jdbc.FabricMySQLDriver;
import java.sql.*;
import java.util.*;


public class JDBCClass {
    private int numOfSt=0;
    private Connection connection;
    private PreparedStatement stmt;
    public JDBCClass() throws SQLException {
        Properties properties=new Properties();
        properties.setProperty("user","root");
        properties.setProperty("password","root");
        Driver driver = new FabricMySQLDriver();
        DriverManager.registerDriver(driver);
        connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/mydbbest", properties);
        this.stmt = connection.prepareStatement("INSERT INTO statistic " +
                "(idStatistic, Date, Open, High, Low, Close, `Adj Close`, Volume, Dividend)" +
                " VALUES (?,?,?,?,?,?,?,?,? )");

    }


    public void batchSQL(int id, ArrayList<String> statementSql) throws SQLException {
        stmt.setString(1, Integer.toString(id));
       if(statementSql.size()==7){//если стандартная строка

        for (int whatElement =0; whatElement<statementSql.size(); whatElement++){
            switch (whatElement){
                case 0:{
                    stmt.setDate(whatElement+2, convertDate(statementSql.get(whatElement)));
                    break;
                }
                case 6:{
                    stmt.setInt(whatElement+2, convertVolume(statementSql.get(whatElement)));
                    stmt.setString(9, "");
                    stmt.addBatch();
                    numOfSt++;
                    break;
                }
                default:{
                    stmt.setFloat(whatElement+2, Float.valueOf(statementSql.get(whatElement)));
                    break;
                }
            }
        }
       }else{//если дивидент
           stmt.setFloat(1, id);
           stmt.setDate(2, convertDate(statementSql.get(0)));
           for (int s=3; s<=7; s++){
               stmt.setFloat(s, 0);
           }
           stmt.setInt(8, 0);
           stmt.setString(9, statementSql.get(1));
           stmt.addBatch();
           numOfSt++;
       }
       //отправка если >10 записей
        if(numOfSt>10){
            stmt.executeBatch();
            numOfSt=0;
        }
    }
    private java.sql.Date convertDate (String line){
        String mass[] = line.split(" ");
        int YEAR;
        int MONTH=0;
        int DAY;
        YEAR = Integer.parseInt(mass[2]);
        switch (mass[0]){
            case "Jan":MONTH=1;
            break;
            case "Feb":MONTH=2;
                break;
            case "Mar":MONTH=3;
                break;
            case "Apr":MONTH=4;
                break;
            case "May":MONTH=5;
                break;
            case "Jun":MONTH=6;
                break;
            case "Jul":MONTH=7;
                break;
            case "Aug":MONTH=8;
                break;
            case "Sep":MONTH=9;
                break;
            case "Oct":MONTH=10;
                break;
            case "Nov":MONTH=11;
                break;
            case "Dec":MONTH=12;
                break;

        }
        DAY =Integer.parseInt(mass[1].substring(0,2));

        Calendar calendar = new GregorianCalendar(YEAR, MONTH-1, DAY);
        long NewDate = calendar.getTimeInMillis();
        java.sql.Date date = new java.sql.Date(NewDate);
        return date;
    }
    private int convertVolume(String line){
        String convertedLine = line.replace(",","");
        return Integer.parseInt(convertedLine);
    }

    public void close() throws SQLException {
        stmt.close();
        connection.close();
    }

}
