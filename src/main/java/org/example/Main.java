package org.example;

import org.example.entities.FlightPriceAPI;
import org.example.entities.SeleniumWhatsApp;

import java.time.LocalDateTime;

public class Main {
    public static void main(String[] args) {

        System.setProperty("webdriver.chrome.driver", "lib/chromedriver");

        String startDate = "24/06/2023";
        String endDate = "10/07/2023";
        int maxPrice = 1000;
        int maxFlyDurationHours = 4;
        int flightsLimit = 2;

        FlightPriceAPI flightPriceAPI = new FlightPriceAPI();
        SeleniumWhatsApp seleniumWhatsApp = new SeleniumWhatsApp();
        seleniumWhatsApp.setupDriver();


        int i = 0;
        LocalDateTime nextIteration = LocalDateTime.now().plusDays(1);
        while (true) {

            while (LocalDateTime.now().equals(nextIteration) || LocalDateTime.now().isAfter(nextIteration) || i == 0) {

                flightPriceAPI.request(startDate, endDate, maxPrice, maxFlyDurationHours, flightsLimit);
                flightPriceAPI.getFlightsDataFromResponse();
                String message = flightPriceAPI.getFlightsDataAsString();
                flightPriceAPI.saveFlightsData();

                seleniumWhatsApp.openWhatsApp();
                seleniumWhatsApp.sendMessage("FlightInfo", message, 5);
                nextIteration = nextIteration.plusDays(1);
                i++;
            }
        }
    }
}