package com.example.android.webscraper;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class WalmartScraper {
    public WalmartScraper(){
        System.out.println("------------------------Currently Setting Up------------------------");
        System.out.println(System.getProperty("user.dir"));
        System.setProperty("webdriver.gecko.driver","geckodriver.exe");
        FirefoxOptions options = new FirefoxOptions();
        options.setHeadless(true);
        WebDriver driver = new FirefoxDriver();
        driver.get("https://www.walmart.com/");

        run(driver);
    }

    private void createFile(HashSet<JSONObject> deals) {
        File file = new File("app\\src\\main\\assets\\dataWalmart.json");
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

    private void run(WebDriver driver) {
        driver.manage().timeouts().implicitlyWait(10,  TimeUnit.SECONDS);
        WebDriverWait wait = new WebDriverWait(driver, 10);
        //driver.findElement(By.cssSelector(".ak_c")).click();

        driver.findElement(By.cssSelector("button[class='f_a ak_c f_c']")).click();
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        JavascriptExecutor js = ((JavascriptExecutor) driver);
        WebElement button =  driver.findElement(By.cssSelector("#vh-spark-main-menu > div:nth-child(3) > a:nth-child(5)"));
        js.executeScript("arguments[0].scrollIntoView(true);", button);
        button.click();
        //driver.findElement(By.cssSelector("#vh-spark-main-menu > div:nth-child(3) > a:nth-child(5)")).click();

        driver.findElement(By.cssSelector("div.store-button-wrapper:nth-child(2) > button:nth-child(1)")).click();
        driver.findElement(By.cssSelector("span[class='current-zip']")).click();
        WebElement inputBox = driver.findElement(By.cssSelector(".field-input"));
        inputBox.clear();
        inputBox.sendKeys("28213");
        driver.findElement(By.cssSelector(".spin-button-children")).click();
        driver.findElement(By.cssSelector("li.store-list-item:nth-child(1) > div:nth-child(1) > div:nth-child(2) > span:nth-child(2) > a:nth-child(1)")).click();
        driver.findElement(By.cssSelector(".refine-secondary > span:nth-child(1)")).click();
        driver.findElement(By.cssSelector("li.nav-panel-li:nth-child(3) > a:nth-child(1)")).click();
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        String currentFrame = (String) js.executeScript("return self.name");
        System.out.println(currentFrame);
        driver.switchTo().frame(0);
        System.out.println((String) js.executeScript("return self.name"));
        System.out.println("Frame Switched");
        //WebElement window = driver.findElement(By.cssSelector(".store-pages-children-container"));
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".flatsheettopbar-grid-view"))).click();
        //driver.findElement(By.cssSelector(".flatsheettopbar-grid-view")).click();
        //driver.switchTo().defaultContent();
        HashSet<JSONObject> deals = new HashSet<JSONObject>();
        //driver.switchTo().frame(0);
        WebElement list = driver.findElement(By.className("category-allcategories"));
        List<WebElement> items = list.findElements(By.className("item"));
        System.out.println(items.size());
        for(int i = 0; i < items.size(); i++) {
            driver.manage().timeouts().implicitlyWait(1,  TimeUnit.SECONDS);
            //System.out.println(items.get(i).getText());
            if(i <= items.size() - 5)
                js.executeScript("arguments[0].scrollIntoView(true);", items.get(i));
            items.get(i).findElement(By.tagName("a")).click();

            JSONObject listing = new JSONObject();
            //driver.switchTo().frame(1);
            //driver.findElement(By.cssSelector("div[class='item-pop-v3-item-name']")).getText();

            String title = wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("div[class='item-pop-v3-item-name']"))).getText();
            String deal = driver.findElement(By.className("item-pop-v3-item-pricing")).getText();
            String expires = driver.findElement(By.className("item-pop-v3-valid-dates")).getText();
            String notes = "";
            try {
                notes = driver.findElement(By.id("item-pop-v3-item-description")).getText();
            } catch (NoSuchElementException nse) {
                notes = "No description";
            }
            String image = driver.findElement(By.className("item-pop-v3-display-image")).getAttribute("src").toString();
            String store = "Walmart";

            System.out.println(title + " is selling for " + deal);
            System.out.println(expires);
            System.out.println("Description: " + notes);
            System.out.println("Image located at " + image);
            listing.put("item", title);
            listing.put("deal", deal);
            listing.put("expires", expires);
            listing.put("notes", notes);
            listing.put("imageURL", image);
            listing.put("store", store);
            deals.add(listing);
            //WebElement close = driver.findElement(By.className("item-pop-v3-close-x"));
            //js.executeScript("arguments[0].scrollIntoView(true);", driver.findElement(By.className("item-pop-v3-close-x")));
            driver.findElement(By.className("item-pop-v3-close-x")).click();
        }

        createFile(deals);
    }
}
