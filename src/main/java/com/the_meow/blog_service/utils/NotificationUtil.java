package com.the_meow.blog_service.utils;


import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public abstract class NotificationUtil {
    public static void notify_user(Long userID) throws URISyntaxException, IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(new URI("http://notification/api/notify?id=" + userID)) // the url should be something like this
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != 200) {
            System.out.println("Status code: " + response.statusCode());
            System.out.println("Body: " + response.body());
            System.out.println("Error notifying the user with id: " + userID);
        }
   }

   public static void fake_notify_user(Long userID) {
        System.out.println("The user with id: " + userID +  " was notified");
   }
}
