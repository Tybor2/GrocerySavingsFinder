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

import org.openqa.selenium.NoSuchElementException;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;;

public class HarrisTeeterScraper
{

    public HarrisTeeterScraper(){
        run();
    }
    
    private static int getLocations(int distance, List<WebElement> places) {
        for (int i = 0; i < places.size(); i++) {
            if (Integer.parseInt(places.get(i).toString()) > distance)
                return i;
            
        }
        return places.size();
    }
    
    
    public void run() {
    	System.out.println(System.getProperty("user.dir"));
        System.setProperty("webdriver.gecko.driver","geckodriver.exe");
        WebDriver driver = new FirefoxDriver();
        driver.get("https://www.harristeeter.com/specials/weekly-list/best-deals");
        
        System.out.print("Please enter your zipcode: ");
        Scanner input = new Scanner(System.in);
        //String zipcode = input.next();
        String zipcode = "28607";
        System.out.println(zipcode);
        WebElement inputBox = driver.findElement(By.id("weekly-list-input-enter-zip"));
        inputBox.sendKeys(zipcode);
        inputBox.submit();

        HashSet<JSONObject> deals = new HashSet<JSONObject>();
        List<WebElement> places = driver.findElements(By.className("select_number ng-binding"));
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        driver.findElement(By.xpath("/html/body/div[1]/section/div/div/div/div/div/div[1]/div/div[2]/div/div[1]/div/div/a[2]")).click();
        driver.manage().timeouts().implicitlyWait(3,  TimeUnit.SECONDS);
        int menu = driver.findElement(By.xpath("/html/body/div[1]/section/div/div/div/ui-view/weekly-specials/div/div/div[2]/div/div[2]/div[2]/div/div[1]")).findElements(By.tagName("option")).size();
        System.out.println(menu + " Items");
        int numItems = 0;
        for(int j = 2; j  <= menu; j++) {
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
            String option = "/html/body/div[1]/section/div/div/div/ui-view/weekly-specials/div/div/div[2]/div/div[2]/div[2]/div/div[1]/select/option[" + j + "]";
            driver.findElement(By.xpath(option)).click();

            List<WebElement> items = driver.findElements(By.cssSelector("div[class='product_titleBox']"));
            List<WebElement> images = driver.findElements(By.className("product_image"));
            String expires = driver.findElement(By.cssSelector("div[class='pull-right valid-text']")).getText();
            System.out.println("Heres your " + items.size() + " items from option " + j + ":");
            
            for(int i = 0; i < items.size()-1; i++) {
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
                	product = items.get(i).findElement(By.cssSelector("div[class='product_title']")).getText();
                } catch (NoSuchElementException e) {
                	product = items.get(i).findElement(By.cssSelector("div[class='product_title ng-binding']")).getText();
                }

                System.out.println(product);
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


        //driver.execute_script()
        File file = new File("app\\src\\main\\assets\\dataHT.json");
        //File file = new File("D:\\Programming\\workspace\\BasicWEbScraper\\src\\PublixData\\data.json");
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

    }
}
