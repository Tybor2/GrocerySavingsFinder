package com.example.android.webscraper;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;


import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.NoSuchElementException;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;


public class HarrisTeeterScraper
{

    public HarrisTeeterScraper(){
        System.out.println("------------------------Currently Setting Up------------------------");
        System.out.println(System.getProperty("user.dir"));
        System.setProperty("webdriver.gecko.driver","geckodriver.exe");
        FirefoxOptions options = new FirefoxOptions();
        options.setHeadless(true);
        WebDriver driver = new FirefoxDriver();
        driver.get("https://www.harristeeter.com/specials/weekly-list/best-deals");

        run(driver);
    }
    
    private static int getLocations(int distance, List<WebElement> places) {
        for (int i = 0; i < places.size(); i++) {
            if (Integer.parseInt(places.get(i).toString()) > distance)
                return i;
            
        }
        return places.size();
    }

    /**
     * Navigate the webdriver to the page of coupons so they can be scraped
     * @param driver - Webdriver to navigate page
     * @return new Webdriver after getting to main coupons
     */
    private WebDriver getToAd(WebDriver driver) {
        System.out.println("--------------------------------Gotta Find that Ad-----------------------------------------");
        //TODO: Make this ask for zipcode
        System.out.print("Please enter your zipcode: ");
        Scanner input = new Scanner(System.in);
        //String zipcode = input.next();
        String zipcode = "28213";
        System.out.println(zipcode);
        WebElement inputBox = driver.findElement(By.id("weekly-list-input-enter-zip"));
        inputBox.sendKeys(zipcode);
        inputBox.submit();

        //TODO: Separate individual sections like getting to the ad, finding items, pulling info, making file, etc.

        List<WebElement> places = driver.findElements(By.className("select_number ng-binding"));
        //driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);

        //TODO: Less Xpath and more CSS Selectors

        driver.findElement(By.xpath("/html/body/div[1]/section/div/div/div/div/div/div[1]/div/div[2]/div/div[1]/div/div/a[2]")).click();
        return driver;
    }

    /**
     * Creates the JSON file that stores all the scraped deals and puts it into the file system of the app.
     * @param deals - HashSet of all the coupons available in the weekly ad
     */
    private void createFile(HashSet<JSONObject> deals) {
        System.out.println("---------------------------Creating File--------------------------");
        File file = new File("app\\src\\main\\assets\\dataHT.json");
        JSONParser parser = new JSONParser();
        JSONObject everything = new JSONObject();

        Iterator<JSONObject> it = deals.iterator();
        int count = 0;
        for(JSONObject j: deals){
            everything.put(count, j);
            count++;
        }

        try (FileWriter writer = new FileWriter(file)) {
            writer.write(everything.toJSONString());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public void run(WebDriver driver) {
        System.out.println("------------------------------------Now Running---------------------------");
        HashSet<JSONObject> deals = new HashSet<JSONObject>();
        driver.manage().timeouts().implicitlyWait(10,  TimeUnit.SECONDS);
        driver = getToAd(driver);

        List<WebElement> options = driver.findElement(By.id("departments")).findElements(By.tagName("option"));
        //int menu = driver.findElement(By.xpath("/html/body/div[1]/section/div/div/div/ui-view/weekly-specials/div/div/div[2]/div/div[2]/div[2]/div/div[1]")).findElements(By.tagName("option")).size();
        //System.out.println(menu + " Items");
        int numItems = 0;
        System.out.println("----------------------------------Time To Scrape-------------------------");
        for(int j = 1; j < options.size(); j++) {
        //for(int j = 2; j  <= menu; j++) {
            //driver.manage().timeouts().implicitlyWait(100,  TimeUnit.SECONDS);
            try
            {
                Thread.sleep(1500);
            }
            catch (InterruptedException e)
            {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            //String option = "/html/body/div[1]/section/div/div/div/ui-view/weekly-specials/div/div/div[2]/div/div[2]/div[2]/div/div[1]/select/option[" + j + "]";
            //driver.findElement(By.xpath(option)).click();
            options.get(j).click();
            //List<WebElement> items = driver.findElements(By.cssSelector("div[class='product_titleBox']"));
            List<WebElement> items = driver.findElements(By.className("product_titleBox"));
            List<WebElement> images = driver.findElements(By.className("product_image"));
            String expires = driver.findElement(By.cssSelector("div[class='pull-right valid-text']")).getText();

            //TODO: Fix the case where scraper finds no items
            System.out.println("Heres your " + items.size() + " items from option " + j + ":");
            
            for(int i = 0; i < items.size()-1; i++) {
                driver.manage().timeouts().implicitlyWait(1,  TimeUnit.SECONDS);
                try
                {
                    Thread.sleep(100);
                }
                catch (InterruptedException e)
                {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                JSONObject listing = new JSONObject();
                String product = "";
                try {
                	product = items.get(i).findElement(By.cssSelector("div[class='product_title ng-binding']")).getText();
                } catch (NoSuchElementException e) {
                    product = items.get(i).findElement(By.cssSelector("div[class='product_title']")).getText();
                }

                System.out.println(i + ".   " + product);
                listing.put("item", product);
                String[] info = null;
                try {
                	listing.put("deal", items.get(i).findElement(By.cssSelector("div[class='offer_tag ng-binding']")).getText());
                } catch (NoSuchElementException e) {
                	listing.put("deal", items.get(i).findElement(By.cssSelector("div[class='offer_tag']")).getText());
                }
                listing.put("expires", expires);
                listing.put("store", "Harris Teeter");
                listing.put("notes", items.get(i).findElement(By.cssSelector("div[class='product_text']")).getText());
                
                try {
                    listing.put("imageURL", images.get(i).findElements(By.tagName("img")).get(1).getAttribute("src").toString());
                } catch (NoSuchElementException nse) {
                    listing.put("imageURL", images.get(i).findElements(By.tagName("img")).get(0).getAttribute("src").toString());
                } catch (IndexOutOfBoundsException iob) {
                    listing.put("imageURL", images.get(i).findElements(By.tagName("img")).get(0).getAttribute("src").toString());
                }

                deals.add(listing);

            }
            
        }

        createFile(deals);



    }
}
