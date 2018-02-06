import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;

import java.sql.SQLException;
import java.util.ArrayList;

//JSOUP не умеет работать с javascript
//используется selenium для отображения всей таблицы
//т.к. парсить <script> не всегда удобно
public class Main {
    private static final String fileLocation = System.getProperty("user.dir");
    private static int i;
    public static void main (String args[]) throws SQLException {

        JDBCClass jdbcClass = new JDBCClass();
        final String service = fileLocation+"\\chromedriver.exe";
        System.setProperty("webdriver.chrome.driver", service);
        WebDriver  driver = new ChromeDriver();

        final String company[] = {"GOOG", "AAPL", "MSFT", "K", "F", "T", "NEE", "BIDU", "CAJ"};
        final String urlPartOne = "https://finance.yahoo.com/quote/";
        final String urlPartTwo = "/history?period1=1221426000&period2=1506114000&interval=1d&filter=history&frequency=1d";
        //1221426000 - мс 15 sep 2008
        //1506114000 - мс 23 sep 2017
        try {
            for (i = 0; i < company.length; i++) {
                driver.get(urlPartOne + company[i] + urlPartTwo);
                Scrolling(driver);
                Parsing(driver, jdbcClass);
            }
        }finally {
            jdbcClass.close();
            driver.close();
        }

    }

    private static void Parsing(WebDriver driver, JDBCClass jdbcClass){
        String line = driver.getPageSource();
        Document document = Jsoup.parse(line);
        Element table = document.select("table").get(1);
        Elements rows = table.select("tr");
        rows.forEach(row -> {
            ArrayList<String> arrOfCol = new ArrayList<>();
            Elements cols = row.select("td");
            cols.forEach(col -> {
                arrOfCol.add(col.text());
            });
            if (cols.size() != 0) {
                try {
                    jdbcClass.batchSQL(i + 1, arrOfCol);
                } catch (Exception e) {
                }
            }
        });
    }


//прокрутка страницы
    private static void Scrolling(WebDriver driver) {
        for(int j=1; j<60; j++) {
            ((JavascriptExecutor) driver).executeScript("scroll(0,1000000)");
        }
    }


}
