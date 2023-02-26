package org.example.entities;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class SeleniumWhatsApp {
    private WebDriver driver;

    public void setupDriver() {
        WebDriverManager.chromedriver().setup(); // Automatically setups the driver for Chrome.
        driver = new ChromeDriver();
    }

    public void openWhatsApp() {
        if (!Objects.equals(driver.getCurrentUrl(), "https://web.whatsapp.com/")) {
            driver.get("https://web.whatsapp.com/");
        }
    }

    public void sendMessage(String contactName, String message, int maxRetries) {
        if (!isLoggedIn(maxRetries)) {
            return;
        }
        boolean hasFoundContact = searchContact(contactName);
        if (hasFoundContact) {
            WebElement textBar = driver.findElement(By.xpath("//*[@id=\"main\"]/footer/div[1]/div/span[2]/div/div[2]/div[1]"));
            textBar.sendKeys(message);
            textBar.sendKeys(Keys.ENTER);
        }
        else {
            System.out.println("The contact " + contactName + " hasn't been found.");
        }
    }

    private boolean searchContact(String contactName) {
        WebElement searchBar = driver.findElement(By.xpath("//*[@id=\"side\"]/div[1]/div/div/div[2]/div/div[2]"));
        searchBar.sendKeys(contactName + Keys.ENTER);

        // Contact name that appears in the header of the contact.
        WebElement contact = driver.findElement(By.xpath(("//*[@id=\"main\"]/header/div[2]/div/div/span")));
        return contact.getText().equals(contactName); // The contact has or not been found if the name equals the input.
    }

    public boolean isLoggedIn(int maxRetries) {
        int retryDelay = 5;

        for (int currentRetry = 0; currentRetry < maxRetries; currentRetry++) {
            try {
                driver.findElement(By.xpath("//*[@id=\"side\"]/div[1]/div/div/div[2]/div/div[2]")); // Tries to find the search bar.
                System.out.println("The user is logged in.");
                return true; // The user is logged in.
            } catch (Exception e) {
                System.out.println("The user isn't logged in. Checking again in " + retryDelay + " seconds.");
                try {
                    TimeUnit.SECONDS.sleep(retryDelay);
                } catch (InterruptedException interruptedException) {
                    interruptedException.printStackTrace();
                }
                // The user isn't logged in.
            }
        }
        return false;
    }

    public static void appendWhatsAppNewLine(StringBuilder stringBuilder) {
        stringBuilder.append(Keys.SHIFT);
        stringBuilder.append(Keys.ENTER);
        stringBuilder.append(Keys.SHIFT);
    }

    public void closeDriver() {
        driver.close();
    }
}

