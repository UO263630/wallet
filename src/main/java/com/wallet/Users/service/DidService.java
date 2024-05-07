package com.wallet.Users.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

@Service
public class DidService {

    public List<String> listDIDSpecific(String wallet, String did, RestTemplate rest) {

        String url = "http://localhost:7001/wallet-api/wallet/{wallet}/dids/{did}";

        ResponseEntity<Object> responseEntity = rest.getForEntity(url, Object.class, wallet, did);

        return listSpecific(responseEntity.getBody());
    }

    public Map<String, List<String>> listDIDs(String wallet, RestTemplate rest) {

        String url = "http://localhost:7001/wallet-api/wallet/{wallet}/dids";

        ResponseEntity<Object> responseEntity = rest.getForEntity(url, Object.class, wallet);

        return mapValue(responseEntity.getBody());
    }

    private List<String> listSpecific(Object value) {

        System.out.println("--------------------");
        System.out.println(value);
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        String string = gson.toJson(value);

        // Parseamos el JSON a un objeto JsonObject
        JsonObject jsonObject = new Gson().fromJson(string, JsonObject.class);

        // Obtenemos la lista de billeteras

        List<String> list = new ArrayList<>();
        list.add(jsonObject.toString());

        return list;
    }

    public Object createDID(String wallet, RestTemplate rest, String texto) {

        String url = "http://localhost:7001/wallet-api/wallet/" + wallet + "/dids/create/key?useJwkJcsPub=true&alias="
                + texto;

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<String> requestEntity = new HttpEntity<>(null, headers);

        ResponseEntity<String> responseEntity = rest.postForEntity(url, requestEntity, String.class);

        return responseEntity.getBody();

    }

    public String deleteDIDs(String wallet, String did, RestTemplate rest) {

        String url = "http://localhost:7001/wallet-api/wallet/{wallet}/dids/{did}";

        rest.delete(url, wallet, did);

        return "HOLA";
    }

    private Map<String, List<String>> mapValue(Object value) {

        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        String string = gson.toJson(value);

        // Parseamos el JSON a un objeto JsonObject
        JsonElement jsonElement = JsonParser.parseString(string);

        // Obtenemos la lista de billeteras
        JsonArray walletsArray = jsonElement.getAsJsonArray();

        Map<String, List<String>> map = new HashMap();

        // Iteramos sobre cada billetera para extraer name y permission
        for (JsonElement walletElement : walletsArray) {

            List<String> list = new ArrayList<>();

            JsonObject walletObject = walletElement.getAsJsonObject();

            String id = (walletObject.get("did").getAsString());
            list.add(walletObject.get("alias").getAsString());

            JsonElement json = JsonParser.parseString(walletObject.get("document").getAsString());

            JsonArray array = json.getAsJsonObject().getAsJsonObject("content").getAsJsonArray("verificationMethod");

            for (JsonElement element : array) {
                JsonObject object = element.getAsJsonObject();

                list.add(object.get("type").getAsString());
                list.add(object.getAsJsonObject("publicKeyJwk").get("kty").getAsString());
                list.add(object.getAsJsonObject("publicKeyJwk").get("crv").getAsString());

            }

            map.put(id, list);

        }
        return map;
    }

}
