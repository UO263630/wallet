package com.wallet.Users.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpRequest;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.wallet.Users.model.User;
import com.wallet.Users.model.UserCreation;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import jakarta.servlet.http.HttpServletRequest;

@Service
public class UsersService {

    private HttpServletRequest request;

    private final ObjectMapper objectMapper = new ObjectMapper();

    public Object login(User user, RestTemplate rest) {

        String url = "http://localhost:7001/wallet-api/auth/login";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<User> requestEntity = new HttpEntity<>(user, headers);

        ResponseEntity<Object> responseEntity = rest.postForEntity(url, requestEntity, Object.class);

        String[] test = responseEntity.getHeaders().get("Set-Cookie").get(0).split(";");
        System.out.println(test[0]);

        rest.getInterceptors().add(new CookieInterceptor(test[0]));

        return responseEntity.getBody();

    }

    public Object logout(RestTemplate rest) {

        String url = "http://localhost:7001/wallet-api/auth/logout";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<String> requestEntity = new HttpEntity<>(null, headers);

        ResponseEntity<String> responseEntity = rest.postForEntity(url, requestEntity, String.class);

        return responseEntity.getBody();

    }

    public Object create(UserCreation user, RestTemplate rest) {

        String url = "http://localhost:7001/wallet-api/auth/create";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<UserCreation> requestEntity = new HttpEntity<>(user, headers);

        ResponseEntity<Object> responseEntity = rest.postForEntity(url, requestEntity, Object.class);

        return responseEntity.getBody();

    }

    public List<String> wallet(RestTemplate rest) {

        String urlW = "http://localhost:7001/wallet-api/wallet/accounts/wallets";

        ResponseEntity<Object> responseEntity2 = rest.getForEntity(urlW, Object.class);

        System.out.println(responseEntity2.getBody());

        return prueba(responseEntity2.getBody());

    }

    private List<String> prueba(Object value) {

        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        String string = gson.toJson(value);

        // Parseamos el JSON a un objeto JsonObject
        JsonObject jsonObject = new Gson().fromJson(string, JsonObject.class);

        // Obtenemos la lista de billeteras
        JsonArray walletsArray = jsonObject.getAsJsonArray("wallets");

        List<String> list = new ArrayList<>();

        // Iteramos sobre cada billetera para extraer name y permission
        for (JsonElement walletElement : walletsArray) {

            JsonObject walletObject = walletElement.getAsJsonObject();

            list.add(walletObject.get("name").getAsString());
            list.add(walletObject.get("id").getAsString());
            list.add(walletObject.get("createdOn").getAsString());
            list.add(walletObject.get("addedOn").getAsString());
            list.add(walletObject.get("permission").getAsString());
        }
        return list;
    }

    private static class CookieInterceptor implements ClientHttpRequestInterceptor {

        private final String cookie;

        public CookieInterceptor(String cookie) {
            this.cookie = cookie;
        }

        @Override
        public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution)
                throws IOException {
            HttpHeaders headers = request.getHeaders();
            headers.addAll("Cookie", Collections.singletonList(cookie));
            return execution.execute(request, body);
        }
    }

}
