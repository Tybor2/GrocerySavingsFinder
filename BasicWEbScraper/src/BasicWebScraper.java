
import java.util.List;

import org.jsoup.*;
import org.jsoup.nodes.*;
import org.jsoup.select.Elements;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.phantomjs.PhantomJSDriver;
public class BasicWebScraper
{

    public static void main(String[] args)
    {
        // TODO Auto-generated method stub
        String[][] publix_table = new String[100][100];
        String[][] ht_table = new String[100][100];
        try {
            Document doc = Jsoup.connect("https://weeklyad.publix.com/Publix/BrowseByListing/ByCategory/?StoreID=2700312&CategoryID=5117976").get();
            Elements publix_links = doc.getElementsByClass("title cursorPointer action-tracking-nav action-goto-listingdetail desktopBBDTabletTitle");
            Elements publix_deals = doc.getElementsByClass("deal");
            
            Document ht = Jsoup.connect("https://www.harristeeter.com/specials/weekly-list/best-deals").get();
            System.out.println(ht.getElementsByClass("section.main").toString());
            Elements ht_items = ht.getElementsByAttributeValueContaining("data-title", "weekly-ad-product-title-7");
            Elements ht_deals = ht.select("ui-view");
            System.out.println(ht.getElementsByClass("main").select("ui-view"));
            System.out.println("Publix");
            for (int i = 0; i < publix_deals.size(); i++) {
                publix_table[i][0] = publix_links.get(i).text();
                publix_table[i][1] = publix_deals.get(i).text();
                System.out.println("[Item: " + publix_table[i][0] + ", Deal: " + publix_table[i][1]);
            }
            System.out.println("Harris Teeter");
            for (int i = 0; i < ht_deals.size(); i++) {
                ht_table[i][0] = ht_items.get(i).text();
                ht_table[i][1] = ht_deals.get(i).text();
                System.out.println("[Item: " + ht_table[i][0] + ", Deal: " + ht_table[i][1]);
            }
            System.out.println(ht_deals.size() + 1);
            
            System.setProperty("webdriver.gecko.driver","D:\\Programming\\workspace\\BasicWEbScraper\\geckodriver.exe");
            WebDriver driver = new FirefoxDriver();
            driver.get("https://www.harristeeter.com/specials/weekly-list/best-deals");
            

        } catch(Exception e) {
            System.out.println("Shit fucked up");
        }
        
        
    }

}
