package com.wallet.Users.controller;

import com.wallet.Users.model.User;
import com.wallet.Users.model.UserCreation;
import com.wallet.Users.service.CredentialService;
import com.wallet.Users.service.DidService;
import com.wallet.Users.service.UsersService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

@Controller
public class UsersController {

    @Autowired
    private UsersService usersService;

    @Autowired
    private CredentialService credentialService;

    @Autowired
    private DidService didService;

    public RestTemplate rest = new RestTemplate();

    public String walletUSER;

    @GetMapping("/login")
    public String login(Model model) {
        model.addAttribute("loginForm", new User());
        return "login";
    }

    @PostMapping("/login")
    private String login(User user) {
        System.out.println(user);
        Object value = usersService.login(user, rest);
        System.out.println(value);
        return "redirect:/wallet";

    }

    @RequestMapping("/create")
    public String create(Model model) {
        model.addAttribute("CreateForm", new UserCreation());
        return "create";
    }

    @PostMapping("/create")
    private String create(UserCreation user) {
        System.out.println(user);
        Object value = usersService.create(user, rest);
        System.out.println(value);
        return "redirect:/";

    }

    @RequestMapping("/wallet")
    public String wallet(Model model) {
        List<String> wallet = new ArrayList<>();
        List<String> value = usersService.wallet(rest);
        for (String x : value) {
            wallet.add(x);
        }
        walletUSER = wallet.get(1);
        // Agregar los datos al modelo
        model.addAttribute("wallet", wallet);
        return "wallet";
    }

    @GetMapping("/logout")
    private String logout() {
        usersService.logout(rest);
        return "redirect:/";

    }

    /* -----------------------DID--------------------------- */

    @RequestMapping("/DID/listDIDs")
    public String listDIDs(Model model) {
        Map<String, List<Map<String, String>>> listDIDs = new HashMap();

        // Aquí recuperas los datos de alguna fuente (base de datos, servicio, etc.)
        Map<String, List<String>> value = didService.listDIDs(walletUSER, rest);

        for (String s : value.keySet()) {
            List<Map<String, String>> list = new ArrayList<>();
            Map<String, String> map = new HashMap<>();

            map.put("alias", value.get(s).get(0));
            map.put("type", value.get(s).get(1));
            map.put("kty", value.get(s).get(2));
            map.put("crv", value.get(s).get(3));
            list.add(map);
            listDIDs.put(s, list);
        }

        // Agregar los datos al modelo
        model.addAttribute("listDIDs", listDIDs);
        return "/DID/listDIDs";
    }

    @RequestMapping("/DID/CreateDID")
    public String CreateDID(Model model) {
        return "/DID/CreateDID";
    }

    @PostMapping("/DID/CreateDID")
    public String CreateDID(String texto) {
        // Aquí recuperas los datos de alguna fuente (base de datos, servicio, etc.)
        didService.createDID(walletUSER, rest, texto);

        return "redirect:/DID/listDIDs";
    }

    @RequestMapping("/DID/DeleteDIDs")
    public String DeleteDIDs(Model model) {
        return "/DID/DeleteDIDs";
    }

    @PostMapping("/DID/DeleteDIDs")
    private String DeleteDIDs(@RequestParam("did") String did) {
        didService.deleteDIDs(walletUSER, did, rest);

        return "redirect:/DID/listDIDs";

    }

    @RequestMapping("/DID/listDIDSpecific")
    public String listDIDSpecific(Model model) {
        return "/DID/listDIDSpecific";
    }

    @PostMapping("/DID/listDIDSpecific")
    public String listDIDSpecific(Model model, String did) {
        List<String> listDID = new ArrayList<>();
        // Aquí recuperas los datos de alguna fuente (base de datos, servicio, etc.)
        List<String> value = didService.listDIDSpecific(walletUSER, did, rest);
        for (String x : value) {
            listDID.add(x);
        }

        // Agregar los datos al modelo
        model.addAttribute("resultado", listDID);
        return "/DID/listDIDSpecific";
    }

    /*------------------------CREDENTIALS---------------------------------- */

    @RequestMapping("/Credential/listCredentials")
    public String listCredentials(Model model) {
        Map<String, List<Map<String, String>>> listCredentials = new HashMap();
        // Aquí recuperas los datos de alguna fuente (base de datos, servicio, etc.)
        Map<String, List<String>> value = credentialService.listCredencials(walletUSER, rest);
        System.out.println(value);
        for (String s : value.keySet()) {
            List<Map<String, String>> list = new ArrayList<>();
            Map<String, String> map = new HashMap<>();
            if (value.get(s).size() == 2) {
                map.put("name", value.get(s).get(0));
                map.put("type", value.get(s).get(1));
                list.add(map);
            } else {
                map.put("name", value.get(s).get(0));
                map.put("type", value.get(s).get(1));
                map.put("description", value.get(s).get(2));
                list.add(map);
            }
            listCredentials.put(s, list);
        }

        // Agregar los datos al modelo
        model.addAttribute("listCredentials", listCredentials);
        return "/Credential/listCredentials";
    }

    @RequestMapping("/Credential/AddCredential")
    public String addCredential(Model model) {
        return "/Credential/AddCredential";
    }

    @PostMapping("/Credential/AddCredential")
    private Object addCredential(String credential) {
        credentialService.addCredencial(walletUSER, credential, rest);

        return "redirect:/Credential/listCredentials";

    }

    @RequestMapping("/Credential/DeleteCredential")
    public String deleteCredential(Model model) {
        return "/Credential/DeleteCredential";
    }

    @PostMapping("/Credential/DeleteCredential")
    private String deleteCredential(@RequestParam("credential") String credential) {
        credentialService.deleteCredential(walletUSER, credential, rest);

        return "redirect:/Credential/listCredentials";

    }

    @RequestMapping("/Credential/ListCredentialSpecific")
    public String listCredentialSpecific(Model model) {
        return "/Credential/ListCredentialSpecific";
    }

    @PostMapping("/Credential/ListCredentialSpecific")
    private Object listCredentialSpecific(Model model, String credential) {
        List<String> listCredential = new ArrayList<>();
        // Aquí recuperas los datos de alguna fuente (base de datos, servicio, etc.)

        List<String> value = credentialService.listCredencialSpecific(walletUSER, credential, rest);

        System.out.println("----------------------------");
        System.out.println(value);
        for (String x : value) {
            listCredential.add(x);
        }

        // Agregar los datos al modelo
        model.addAttribute("resultadoC", listCredential);
        return "/Credential/ListCredentialSpecific";

    }

}
