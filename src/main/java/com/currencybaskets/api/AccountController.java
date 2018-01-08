package com.currencybaskets.api;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class AccountController {

    @RequestMapping("/account")
    public String getAccountForUser(@RequestParam(value="userId") String userId, Model model) {
        model.addAttribute("name", userId);
        return "account";
    }
}
