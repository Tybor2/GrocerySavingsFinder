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
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;;

public class PublixScraper
{

    public PublixScraper(){
        try {
            run();
        }catch (InterruptedException ie) {
            System.out.println("Fail");
        }
    }

    public static void checkForPopup(WebDriver driver) {
        try {
            driver.findElement(By.cssSelector("a.acsInviteButton:nth-child(6)")).click();
        } catch (Exception e) {
            //System.out.println("Bitch aint here");
        }
    }

    public void run() throws InterruptedException
    {
        // TODO Auto-generated method stub
        System.setProperty("webdriver.gecko.driver","geckodriver.exe");
        WebDriver driver = new FirefoxDriver();
        driver.get("https://weeklyad.publix.com/publix");
        
        driver.findElement(By.xpath("//*[@id=\"nuepContent\"]/div[2]/div/div[2]/button")).click();
        String zipcode = "28607";
        WebElement inputZip = driver.findElement(By.xpath("//*[@id=\"pblx-txtLocation\"]"));
        inputZip.sendKeys(zipcode);
        inputZip.findElement(By.xpath("//*[@id=\"pblx-btnStoreSearch\"]")).click();
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
        driver.findElement(By.xpath("//*[@id='pblx-store-locator']/div[2]/div[2]/div[2]/div[1]/table/tbody/tr[1]/td[3]/button")).click();
        Thread.sleep(1000);
        checkForPopup(driver);
        //Opens weekly ad
        System.out.println("What's this");
        driver.findElement(By.xpath("/html/body/div[1]/div[3]/div[7]/div[1]/div[7]/div[1]/div/div[2]/div[1]/div[3]/a")).click();
        Thread.sleep(1000);
        checkForPopup(driver);
        //Select coupon page
        System.out.println("Prints something");
        driver.findElement(By.xpath("/html/body/div[1]/div[3]/div[7]/div[1]/div[6]/div[2]/div[3]/a[4]")).click();
        Thread.sleep(1000);
        checkForPopup(driver);
        HashSet<JSONObject> deals = new HashSet<JSONObject>();
        
        JavascriptExecutor js = ((JavascriptExecutor) driver);
        int menu = driver.findElement(By.xpath("/html/body/div[1]/div[3]/div[7]/div[1]/div[7]/div[1]/div[2]/div[1]/div/h1/select")).findElements(By.tagName("option")).size();
        for(int j = 3; j < menu; j++) {
            Thread.sleep(1000);
            checkForPopup(driver);
            
            //Selects options of menu options
            String optionPath = "#TitleBar > div:nth-child(2) > h1:nth-child(1) > select:nth-child(3) > option:nth-child(" + j + ")";
            if(driver.findElement(By.cssSelector(optionPath)).getText().equals("------"))
                break;
            driver.findElement(By.cssSelector(optionPath)).click();
            try {
                driver.findElement(By.xpath("/html/body/div[1]/div[3]/div[7]/div[1]/div[7]/div[1]/div[2]/div[5]/button")).click();
                Thread.sleep(1000);
            } catch (Exception e) {
                System.out.println("Nothing more to load");
            }
            
            //checkForPopup(driver);
            System.out.println(driver.findElement(By.cssSelector(optionPath)).getText());
            
            //Scroll to the bottom to load all items
            while((boolean) js.executeScript("return (parseInt(window.pageYOffset + window.innerHeight, 10) + 1) >= document.body.scrollHeight;") == false) {
                js.executeScript("window.scrollTo(0, document.body.scrollHeight)");
                Thread.sleep(2000);
            }
            
            //Retrieve individual items
            List<WebElement> items = driver.findElements(By.className("gridTileContain"));
            
            for(int i = 0; i < items.size(); i++) {
                JSONObject listing = new JSONObject();
                System.out.println(i);
                checkForPopup(driver);
                try {
                    WebElement popup = driver.findElement(By.className("acsModalBackdrop acsAbandonButton"));
                    WebElement exit = popup.findElement(By.cssSelector("a[class='acsCloseButton acsAbandonButton']"));
                    js.executeScript("arguments[0].click();", exit);
                    System.out.println("Goodbye Popup");
                } catch (NoSuchElementException e) {
                    System.out.println("Nope");
                }
                
                String url = items.get(i).findElement(By.cssSelector("img[class='image cursorPointer']")).getAttribute("src").toString();
                try {
                    items.get(i).findElement(By.cssSelector("div[class='action-print-idp btn excludeFromMobile fl']")).isDisplayed();
                    continue;
                } catch (NoSuchElementException e) {
                    items.get(i).findElement(By.cssSelector("span[class='title cursorPointer action-tracking-nav action-goto-listingdetail desktopBBDTabletTitle']"))
                    .click();
                    Thread.sleep(2000);
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
                    //String item = banner.findElement(By.cssSelector(".mobileIDPContent > h1:nth-child(1)")).getText();
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
                    
                    deals.add(listing);
                    WebElement area = banner.findElement(By.cssSelector("a[class='sl-sc-modal-close skinCloseBtn']"));
                    js.executeScript("arguments[0].click();", area);
                    Thread.sleep(1000);
                }
                
                
                /**Thread.sleep(100);
                //System.out.println();
                String item = items.get(i).findElement(By.cssSelector("span[class='title cursorPointer action-tracking-nav action-goto-listingdetail desktopBBDTabletTitle']")).getText();
                String deal = items.get(i).findElement(By.cssSelector(".deal")).getText();
                String expire = items.get(i).findElement(By.cssSelector("div[class='effectiveDate IDP_validDates']")).getText();
                
                
                String notes = 
                if (deal.equals("")) {
                    System.out.println(item);
                    deals.put(item, " ");
                    listing.put("item", item);
                    listing.put("deal", " ");
                }else {
                    System.out.println(item + " for " + deal);
                    deals.put(item, deal);
                    listing.put("item" , item);
                    listing.put("deal", deal);
                }
                
                driver
                .findElement(By.cssSelector("span[class='title cursorPointer action-tracking-nav action-goto-listingdetail desktopBBDTabletTitle']"))
                .click();
                Thread.sleep(1000);
                System.out.println(driver.findElement(By.className(".mobileIDPContent > h1")).getText());
                driver.findElement(By.cssSelector("a[class='sl-sc-modal-close skinCloseBtn']")).click();**/
            }        }
        
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
            //numItems += 1;
            //file.flush();

        } catch (IOException e) {
            e.printStackTrace();
        }
        
        /**System.out.println(deals.containsKey("Armor All� Products 16 or 64 oz. or 12 or 20 ct."));
        System.out.println(deals.containsKey("Swiffer Disposable Dusters"));
        System.out.println(deals.containsKey("Aprons Slow Cooker Meal Kit"));
        System.out.println(deals.containsKey("Campbell's Chunky Soup"));
        System.out.println(deals.containsKey("Publixs food"));**/
    }

}
