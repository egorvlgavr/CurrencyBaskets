package com.currencybaskets.api;

import com.currencybaskets.dao.repository.UserRepository;
import com.currencybaskets.dto.AmountHistoryDto;
import com.currencybaskets.services.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.time.ZonedDateTime;
import java.util.Date;
import java.util.List;

import static com.currencybaskets.api.AccountController.USER_ID_FIXTURE;

@Controller
public class HistoryController {

    @Autowired
    private AccountService accountService;

    @Autowired
    private UserRepository userRepository;

    @GetMapping("/history")
    public String getHistory() {
        return "history";
    }

    @GetMapping("/history/amount")
    @ResponseBody
    public List<AmountHistoryDto> getAmountHistory(@RequestParam("from")String from) {
        List<Long> ids = userRepository.getUserIdsInSameGroup(USER_ID_FIXTURE);
        return accountService.getAggregatedAmountHistory(ids, parseDateFromParam(from));
    }

    private static Date parseDateFromParam(String from) {
        ZonedDateTime now = ZonedDateTime.now();
        switch (from) {
            case "year":
                return Date.from(now.minusYears(1).toInstant());
            case "month":
                return Date.from(now.minusMonths(1).toInstant());
            case "week":
                return Date.from(now.minusWeeks(1).toInstant());
            default:
                throw new IllegalArgumentException("Param should be [year, month, week] but was:" + from);
        }
    }
}
