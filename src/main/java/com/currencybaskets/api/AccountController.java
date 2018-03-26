package com.currencybaskets.api;

import com.currencybaskets.dao.model.User;
import com.currencybaskets.dao.repository.UserRepository;
import com.currencybaskets.services.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class AccountController {

    @Autowired
    private AccountService accountService;

    @Autowired
    private UserRepository userRepository;

    @GetMapping("/account")
    public String getAccountForUser(@RequestParam(value="userId") Long userId, Model model) {
        User user = userRepository.findOne(userId);
        model.addAttribute("fullName", user.getName() + " " + user.getSurname());
        model.addAttribute("accountsWithRates",
                accountService.getUserLatestAccounts(userId));
        return "account";
    }
}
