package com.the_meow.blog_service.utils;


import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;

public abstract class NotificationUtil {
    public static void notify_users(ArrayList<String> emails) throws URISyntaxException, IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();

        ObjectMapper mapper = new ObjectMapper();
        String json = mapper.writeValueAsString(emails);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(new URI("http://notification/api/notify" )) // the url should be something like this
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != 200) {
            System.out.println("Status code: " + response.statusCode());
            System.out.println("Body: " + response.body());
            System.out.println("Error notifying the users with the following ids: " + emails);
        }
   }

   public static void fake_notify_users(ArrayList<String> userIDs) {
        System.out.println("The user with id: " + userIDs +  " was notified");
   }
}
