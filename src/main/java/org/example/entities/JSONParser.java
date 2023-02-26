package org.example.entities;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import kong.unirest.json.JSONArray;

public class JSONParser {
    public static JsonArray clearFlightData(JSONArray flightsUnirest) {
        Gson gson = new Gson();
        JsonArray flights = gson.fromJson(flightsUnirest.toString(), JsonArray.class);

        JsonArray flightsJson = new JsonArray();

        for (JsonElement el : flights) {
            JsonObject flight = el.getAsJsonObject();
            JsonObject temp = new JsonObject();

            temp.addProperty("flyFrom", flight.get("flyFrom").getAsString());
            temp.addProperty("flyTo", flight.get("flyTo").getAsString());
            temp.addProperty("duration", flight.get("duration").getAsJsonObject().get("total").getAsInt());
            temp.addProperty("price", flight.get("price").getAsInt());
            temp.addProperty("local_departure", flight.get("local_departure").getAsString());
            temp.addProperty("local_arrival", flight.get("local_arrival").getAsString());
            temp.addProperty("deep_link", flight.get("deep_link").getAsString());

            flightsJson.add(temp);
        }
        return flightsJson;
    }
}
