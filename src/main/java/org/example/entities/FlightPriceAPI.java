package org.example.entities;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import kong.unirest.HttpResponse;
import kong.unirest.JsonNode;
import kong.unirest.Unirest;
import kong.unirest.json.JSONException;
import kong.unirest.json.JSONObject;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class FlightPriceAPI {
    private HttpResponse<JsonNode> response;
    private JsonArray flights;
    private JSONObject data;

    public void request(String startDate, String endDate, int maxPrice, int maxFlyDurationHours, int flightsLimit) {
        response = Unirest.get("https://api.tequila.kiwi.com/v2/search?fly_from=SAO&fly_to=SLZ&dateFrom=" + startDate +
                        "&dateTo=" + endDate +
                        "&curr=BRL&max_fly_duration=" + maxFlyDurationHours +
                        "&price_to=" + maxPrice +
                        "&limit=" + flightsLimit)
                .header("apikey", System.getenv("flightsApiKey"))
                .asJson();
    }

    public void getFlightsDataFromResponse() {
        try {
            data = response.getBody().getObject();
            clearData();

            flights = JSONParser.clearFlightData(data.getJSONArray("data"));
        } catch (JSONException e) {
            System.out.println("No flights found");
        }
    }

    public String getFlightsDataAsString() {
        DecimalFormat df = new DecimalFormat("#.00");
        DateTimeFormatter isoDateTimeFormatter = DateTimeFormatter.ISO_OFFSET_DATE_TIME;
        DateTimeFormatter simplerPattern = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

        StringBuilder dataStringBuilder = new StringBuilder();
        for (JsonElement el : flights) {
            JsonObject flightData =  el.getAsJsonObject();
            int durationMinute = (flightData.get("duration").getAsInt())/60;
            int durationHour = durationMinute/60;
            int durationMinuteRest = durationMinute%60;

            dataStringBuilder.append("Flight departure airport: ").append(flightData.get("flyFrom").getAsString());
            SeleniumWhatsApp.appendWhatsAppNewLine(dataStringBuilder);
            dataStringBuilder.append("Flight arrival airport: ").append(flightData.get("flyTo").getAsString());
            SeleniumWhatsApp.appendWhatsAppNewLine(dataStringBuilder);
            dataStringBuilder.append("Flight duration: ").append(durationHour).append("h").append(durationMinuteRest).append("m");
            SeleniumWhatsApp.appendWhatsAppNewLine(dataStringBuilder);

            double price = flightData.get("price").getAsInt() * 0.84; // The API returns the price with their service fee, so I'm removing them (approximate value)
            dataStringBuilder.append("Flight price: *R$").append(df.format(price)).append("*");
            SeleniumWhatsApp.appendWhatsAppNewLine(dataStringBuilder);

            LocalDateTime departureDate = LocalDateTime.parse(flightData.get("local_departure").getAsString(), isoDateTimeFormatter);
            LocalDateTime arrivalDate = LocalDateTime.parse(flightData.get("local_arrival").getAsString(), isoDateTimeFormatter);

            dataStringBuilder.append("Flight departure date: ").append(departureDate.format(simplerPattern));
            SeleniumWhatsApp.appendWhatsAppNewLine(dataStringBuilder);
            dataStringBuilder.append("Flight arrival date: ").append(arrivalDate.format(simplerPattern));
            SeleniumWhatsApp.appendWhatsAppNewLine(dataStringBuilder);

//            flightsDataString.append("Link: ").append(flightData.getString("deep_link"));
//            SeleniumWhatsApp.appendWhatsAppNewLine(flightsDataString);
            dataStringBuilder.append("-------------------------------------------------");
            SeleniumWhatsApp.appendWhatsAppNewLine(dataStringBuilder);
            SeleniumWhatsApp.appendWhatsAppNewLine(dataStringBuilder);
        }
        dataStringBuilder.append("Total flights: ").append(flights.size());

        return dataStringBuilder.toString();
    }

    public void saveFlightsData() {
        int sumPrice = 0;
        for (JsonElement el : flights) {
            JsonObject flightData = el.getAsJsonObject();
            sumPrice += flightData.get("price").getAsInt();
        }
        data.put("averagePrice", sumPrice/flights.size());

        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy_HH-mm-ss"));
        data.put("timestamp", timestamp);

        data.put("data", flights.asList());

        String fileName = timestamp + ".json";

        String folderPath = "C:\\Users\\Suporte\\OneDrive - Fatec Centro Paula Souza\\Programs\\Java\\FlightAPI\\src\\main\\java\\org\\example\\Searches\\";
        try (PrintWriter writer = new PrintWriter(folderPath + fileName)) {
            writer.write(data.toString(4));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void clearData() {
        data.remove("search_params");
        data.remove("currency");
        data.remove("fx_rate");
        data.remove("_results");
        data.remove("all_stopover_airports");
        data.remove("search_id");
        data.remove("sort_version");
    }

    public HttpResponse<JsonNode> getResponse() {
        return response;
    }

    public JsonArray getFlights() {
        return flights;
    }
}
