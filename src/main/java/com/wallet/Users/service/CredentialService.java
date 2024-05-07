package com.wallet.Users.service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.DeflaterOutputStream;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

@Service
public class CredentialService {

    public List<String> listCredencialSpecific(String wallet, String credential, RestTemplate rest) {

        String url = "http://localhost:7001/wallet-api/wallet/{wallet}/credentials/{credential}";
        ObjectMapper objectMapper = new ObjectMapper();

        ResponseEntity<Object> responseEntity = rest.getForEntity(url, Object.class, wallet, credential);

        List<String> lista = prueba2(responseEntity.getBody());

        try {
            generateQRCode(responseEntity.getBody().toString(), 1000, 1000,
                    "src/main/resources/static/images/qr_code.png");
        } catch (WriterException | IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return lista;

    }

    public Map<String, List<String>> listCredencials(String wallet, RestTemplate rest) {

        String url = "http://localhost:7001/wallet-api/wallet/{wallet}/credentials";

        ResponseEntity<Object> responseEntity = rest.getForEntity(url, Object.class, wallet);

        return listadoCredencial(responseEntity.getBody());
    }

    private List<String> prueba2(Object value) {

        System.out.println("--------------------");
        // System.out.println(value);
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        String string = gson.toJson(value);

        // Parseamos el JSON a un objeto JsonObject
        JsonObject jsonObject = new Gson().fromJson(string, JsonObject.class);

        // Obtenemos la lista de billeteras

        List<String> list = new ArrayList<>();
        list.add(jsonObject.toString());

        return list;
    }

    private Map<String, List<String>> listadoCredencial(Object value) {

        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        String string = gson.toJson(value);

        // Parseamos el JSON a un objeto JsonObject
        JsonElement jsonElement = JsonParser.parseString(string);

        // Obtenemos la lista de billeteras
        JsonArray walletsArray = jsonElement.getAsJsonArray();
        Map<String, List<String>> map = new HashMap();

        // Iteramos sobre cada billetera para extraer name y permission
        for (JsonElement walletElement : walletsArray) {
            JsonObject object = walletElement.getAsJsonObject();

            List<String> list = new ArrayList<>();

            String id = object.get("id").getAsString();
            list.add(
                    object.getAsJsonObject("parsedDocument").getAsJsonObject("issuer").get("name").getAsString());
            JsonArray array = object.getAsJsonObject("parsedDocument").get("type").getAsJsonArray();

            StringBuilder stringBuilder = new StringBuilder();

            for (JsonElement element : array) {
                stringBuilder.append(element.getAsString());
                stringBuilder.append(",");
            }
            list.add(stringBuilder.toString());

            if (object.getAsJsonObject("parsedDocument").getAsJsonObject("credentialSubject")
                    .getAsJsonObject("achievement") != null) {
                list.add(object.getAsJsonObject("parsedDocument").getAsJsonObject("credentialSubject")
                        .getAsJsonObject("achievement").get("description").getAsString());
            }

            map.put(id, list);

        }
        return map;
    }

    public Object addCredencial(String wallet, String credential, RestTemplate rest) {

        String url = "http://localhost:7001/wallet-api/wallet/{wallet}/exchange/useOfferRequest";

        ResponseEntity<Object> responseEntity = rest.postForEntity(url, credential, Object.class, wallet);

        return responseEntity.getBody();
    }

    public String deleteCredential(String wallet, String credential, RestTemplate rest) {

        String url = "http://localhost:7001/wallet-api/wallet/{wallet}/credentials/{credential}?permanent=false";

        rest.delete(url, wallet, credential);

        return "HOLA";
    }

    private void generateQRCode(String text, int width, int height, String filePath)
            throws WriterException, IOException {
        Map<EncodeHintType, Object> hintMap = new HashMap<>();
        hintMap.put(EncodeHintType.CHARACTER_SET, "UTF-8");

        QRCodeWriter qrCodeWriter = new QRCodeWriter();

        byte[] compressedData = compress(text);

        BitMatrix bitMatrix = qrCodeWriter.encode(new String(compressedData), BarcodeFormat.QR_CODE, width, height,
                hintMap);

        Path path = FileSystems.getDefault().getPath(filePath);

        MatrixToImageWriter.writeToPath(bitMatrix, "PNG", path);
        System.out.println("Código QR generado y guardado en: " + path.toAbsolutePath());
    }

    private byte[] compress(String text) throws IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        DeflaterOutputStream deflaterOutputStream = new DeflaterOutputStream(outputStream);
        deflaterOutputStream.write(text.getBytes("UTF-8"));
        deflaterOutputStream.close();
        return outputStream.toByteArray();
    }

}
