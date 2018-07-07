package com.currencybaskets.api;

import com.currencybaskets.dao.repository.UserRepository;
import com.currencybaskets.dto.AccountUpdate;
import com.currencybaskets.dto.AggregatedAmountDto;
import com.currencybaskets.services.AccountService;
import com.currencybaskets.view.AccountView;
import com.currencybaskets.view.LatestAccountsView;
import com.currencybaskets.view.RateUpdate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;
import java.util.stream.Collectors;

@Controller
public class AccountController {

    // TODO use spring security
    static final long USER_ID_FIXTURE = 1L;

    @Autowired
    private AccountService accountService;

    @Autowired
    private UserRepository userRepository;

    @GetMapping("/account")
    public String getAccountForUser(Model model) {
        List<Long> ids = userRepository.getUserIdsInSameGroup(USER_ID_FIXTURE);
        LatestAccountsView accountsView = accountService.getUserLatestAccounts(ids);
        model.addAttribute("accountsWithRates", accountsView);
        String fullNames = accountsView.getAccounts()
                .stream()
                .map(AccountView::getUserFullName)
                .distinct()
                .collect(Collectors.joining(", "));
        model.addAttribute("fullNames", fullNames);
        model.addAttribute("accountUpdate", new AccountUpdate());
        model.addAttribute("rateUpdate", new RateUpdate());
        return "account";
    }

    @GetMapping("/aggregated/amount")
    @ResponseBody
    public List<AggregatedAmountDto> getAggregatedAmount() {
        List<Long> ids = userRepository.getUserIdsInSameGroup(USER_ID_FIXTURE);
        return accountService.getAggregatedAmount(ids);
    }

    @PostMapping("/updateAccount")
    public String accountUpdate(@ModelAttribute("accountUpdate") AccountUpdate accountUpdate) {
        accountService.updateAccountAmount(accountUpdate);
        // TODO update only part of page
        return "redirect:/account";
    }

    @PostMapping("/updateRate")
    public String rateUpdate(@ModelAttribute("rateUpdate") RateUpdate rateUpdate) {
        accountService.updateAccountsRate(rateUpdate);
        // TODO update only part of page
        return "redirect:/account";
    }
}
