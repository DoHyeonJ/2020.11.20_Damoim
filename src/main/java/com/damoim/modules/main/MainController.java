package com.damoim.modules.main;

import com.damoim.modules.account.CurrentAccount;
import com.damoim.modules.account.Account;
import com.damoim.modules.club.Club;
import com.damoim.modules.club.ClubRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class MainController {

    private final ClubRepository clubRepository;

    @GetMapping("/")
    public String home(@CurrentAccount Account account, Model model) {
        if (account != null) {
            model.addAttribute(account);
        }

        return "index";
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/search/club")
    public String searchClub(String keyword, Model model) {
        List<Club> clubList = clubRepository.findByKeyword(keyword);
        model.addAttribute(clubList);
        model.addAttribute("keyword", keyword);
        return "search";
    }

}
