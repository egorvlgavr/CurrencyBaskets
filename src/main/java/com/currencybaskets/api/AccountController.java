package com.currencybaskets.api;

import com.currencybaskets.dao.model.User;
import com.currencybaskets.dao.repository.UserRepository;
import com.currencybaskets.dto.AccountUpdate;
import com.currencybaskets.dto.AggregatedAmountDto;
import com.currencybaskets.services.AccountService;
import com.currencybaskets.view.LatestAccountsView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
public class AccountController {

    // TODO use spring security
    private static final long USER_ID_FIXTURE = 1L;

    @Autowired
    private AccountService accountService;

    @Autowired
    private UserRepository userRepository;

    @GetMapping("/account")
    public String getAccountForUser(Model model) {
        User user = userRepository.findOne(USER_ID_FIXTURE);
        model.addAttribute("fullName", user.getName() + " " + user.getSurname());
        model.addAttribute("accountsWithRates",
                accountService.getUserLatestAccounts(USER_ID_FIXTURE));
        model.addAttribute("accountUpdate", new AccountUpdate());
        return "account";
    }

    @GetMapping("/aggregated/amount")
    @ResponseBody
    public List<AggregatedAmountDto> getAggregatedAmount() {
        return accountService.getAggregatedAmount(USER_ID_FIXTURE);
    }

    @PostMapping("/updateAccount")
    public String accountUpdate(@ModelAttribute("accountUpdate") AccountUpdate accountUpdate, Model model) {
        accountService.updateAccountAmount(accountUpdate);
        LatestAccountsView updated = accountService.getUserLatestAccounts(USER_ID_FIXTURE);
        model.addAttribute("accountsWithRates", updated);
        // TODO update only part of page
        return "redirect:/account";
    }
}
