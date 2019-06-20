package com.example.android.webscraper;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;;

public class PublixScraper
{

    public PublixScraper(){
        try {
            System.setProperty("webdriver.gecko.driver","geckodriver.exe");
            FirefoxOptions options = new FirefoxOptions();
            options.setHeadless(true);
            WebDriver driver = new FirefoxDriver();
            driver.get("https://weeklyad.publix.com/publix");
            run(driver);
        }catch (InterruptedException ie) {
            System.out.println("Fail");
        }
    }


    public static void checkForPopup(WebDriver driver) {
        driver.manage().timeouts().implicitlyWait(500,  TimeUnit.MILLISECONDS);
        JavascriptExecutor js = ((JavascriptExecutor) driver);
        try {
            driver.findElement(By.cssSelector("a.acsInviteButton:nth-child(6)")).click();
        } catch (Exception e) {

        }
        try {
            WebElement popup = driver.findElement(By.className("acsModalBackdrop acsAbandonButton"));
            WebElement exit = popup.findElement(By.cssSelector("a[class='acsCloseButton acsAbandonButton']"));
            js.executeScript("arguments[0].click();", exit);
            System.out.println("Goodbye Popup");
        } catch (NoSuchElementException e) {
            System.out.println("No Modal");
        }

        try {
            driver.findElement(By.cssSelector("svg[class='fsrButton fsrButton__inviteClose fsrAbandonButton']")).click();
            System.out.println("Popup Found");
            WebElement popup = driver.findElement(By.className("__fsr "));
            System.out.println("Popup Found");
            WebElement exit = popup.findElement(By.cssSelector("a[class='fsrButton fsrButton__inviteClose fsrAbandonButton']"));
            js.executeScript("arguments[0].click();", exit);
            System.out.println("Goodbye Popup");
        } catch (NoSuchElementException e) {
            System.out.println("No FSR");
        }
    }

    private WebDriver getToAd(WebDriver driver) {
        driver.findElement(By.cssSelector("button[class='btn btn-large nuepSelectAStore selectStoreBtn']")).click();
        String zipcode = "28213";
        WebElement inputZip = driver.findElement(By.xpath("//*[@id=\"pblx-txtLocation\"]"));
        inputZip.sendKeys(zipcode);
        driver.findElement(By.cssSelector("#pblx-btnStoreSearch")).click();
        try
        {
            Thread.sleep(1000);
        }
        catch (InterruptedException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        //Select 1st store
        driver.findElement(By.cssSelector("button[class='btn js-selectStore']")).click();
        try
        {
            Thread.sleep(1000);
        }
        catch (InterruptedException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        checkForPopup(driver);

        //Opens weekly ad
        try
        {
            Thread.sleep(1000);
        }
        catch (InterruptedException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        checkForPopup(driver);

        //Select coupon page
        System.out.println("Prints something");
        driver.findElement(By.cssSelector("a[class='btn btn-large hero-cta  action-tracking-nav action-bbDeptButton']")).click();
        try
        {
            Thread.sleep(1000);
        }
        catch (InterruptedException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        checkForPopup(driver);
        return driver;
    }

    private void createFile(HashSet<JSONObject> deals) {
        File file = new File("app\\src\\main\\assets\\dataPublix.json");
        JSONParser parser = new JSONParser();
        JSONObject everything = new JSONObject();

        Iterator<JSONObject> it = deals.iterator();
        int count = 0;
        for(JSONObject j: deals){
            //System.out.println(it.next());
            everything.put(count, j);
            count++;
        }

        try (FileWriter writer = new FileWriter(file)) {
            writer.write(everything.toJSONString());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void run(WebDriver driver) throws InterruptedException
    {
        driver.manage().timeouts().implicitlyWait(2,  TimeUnit.SECONDS);
        HashSet<JSONObject> deals = new HashSet<JSONObject>();
        driver = getToAd(driver);
        JavascriptExecutor js = ((JavascriptExecutor) driver);
        int menu = driver.findElement(By.cssSelector("select[class='catbranddropdown action-tracking-nav']")).findElements(By.tagName("option")).size();
        List<WebElement> options = driver.findElement(By.cssSelector("select[class='catbranddropdown action-tracking-nav']")).findElements(By.tagName("option"));

        for(int j = 3; j < menu; j++) {
            options = driver.findElement(By.cssSelector("select[class='catbranddropdown action-tracking-nav']")).findElements(By.tagName("option"));
            Thread.sleep(100);
            checkForPopup(driver);
            
            //Selects options of menu options
            String optionPath = "#TitleBar > div:nth-child(2) > h1:nth-child(1) > select:nth-child(3) > option:nth-child(" + j + ")";
            if(driver.findElement(By.cssSelector(optionPath)).getText().equals("------"))
                break;
            driver.findElement(By.cssSelector(optionPath)).click();

            //Find a load more button if available
            try {
                driver.findElement(By.xpath("/html/body/div[1]/div[3]/div[7]/div[1]/div[7]/div[1]/div[2]/div[5]/button")).click();
                Thread.sleep(100);
            } catch (Exception e) {
                System.out.println("Nothing more to load");
            }
            
            //checkForPopup(driver);
            System.out.println(driver.findElement(By.cssSelector(optionPath)).getText());

            //Scroll to the bottom to load all items
            while((boolean) js.executeScript("return (parseInt(window.pageYOffset + window.innerHeight, 10) + 1) >= document.body.scrollHeight;") == false) {
                driver.manage().timeouts().implicitlyWait(1,  TimeUnit.SECONDS);
                js.executeScript("window.scrollTo(0, document.body.scrollHeight)");
                Thread.sleep(500);
            }
            
            //Retrieve individual items
            List<WebElement> items = driver.findElements(By.className("gridTileContain"));
            for(int i = 0; i < items.size(); i++) {
                Thread.sleep(300);
                JSONObject listing = new JSONObject();
                System.out.println(i);
                checkForPopup(driver);

                
                String url = items.get(i).findElement(By.cssSelector("img[class='image cursorPointer']")).getAttribute("src").toString();
                try {
                    //Check if coupon needs to be printed
                    items.get(i).findElement(By.cssSelector("div[class='action-print-idp btn excludeFromMobile fl']")).isDisplayed();
                    continue;
                } catch (NoSuchElementException e) {
                    items.get(i).findElement(By.cssSelector("span[class='title cursorPointer action-tracking-nav action-goto-listingdetail desktopBBDTabletTitle']"))
                    .click();
                    //checkForPopup(driver);
                    Thread.sleep(500);
                    WebElement banner = driver.findElement(By.className("sl-sc-modal"));
                    int x = 0;
                    String item = "";
                    while(x < 5) {
                        try {
                            item = banner.findElement(By.cssSelector(".mobileIDPContent > h1:nth-child(1)")).getText();
                            break;
                        } catch (NoSuchElementException d) {
                            System.out.println("Can't find " + x);
                            /**if(x == 4)
                                continue;**/
                            x++;
                        }
                    }
                    //Grab information and put it in a JSON Object
                    System.out.println(url);
                    listing.put("item", item);
                    String deal = banner.findElement(By.cssSelector("p[class='deal']")).getText();
                    listing.put("deal", deal);
                    String expire = banner.findElement(By.cssSelector("p[class='effectiveDate IDP_validDates']")).getText();
                    listing.put("expires", expire);
                    listing.put("store", "Publix");
                    String notes = banner.findElement(By.cssSelector("p[class='description']")).getText();
                    listing.put("notes", notes);
                    listing.put("imageURL", url);
                    //String imageURL = banner.findElement(By.cssSelector(".idpImageWrapper > div:nth-child(1)")).getAttribute("url").te
                    System.out.println(listing);
                    deals.add(listing);
                    WebElement area = banner.findElement(By.cssSelector("a[class='sl-sc-modal-close skinCloseBtn']"));
                    js.executeScript("arguments[0].click();", area);
                    Thread.sleep(1000);
                }
            }
        }
        createFile(deals);


    }

}
