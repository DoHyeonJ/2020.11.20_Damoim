package com.damoim.modules.club;


import com.damoim.modules.account.CurrentAccount;
import com.damoim.modules.club.form.ClubForm;
import com.damoim.modules.club.validator.ClubFormValidator;
import com.damoim.modules.account.Account;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import javax.validation.Valid;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Controller
@RequiredArgsConstructor
public class ClubController {

    private final ClubService clubService;
    private final ModelMapper modelMapper;
    private final ClubFormValidator clubFormValidator;
    private final ClubRepository clubRepository;

    // data 바인딩 처리 ( 기준은 : clubFormValidator )
    @InitBinder("clubForm")
    public void clubFormInitBinder(WebDataBinder webDataBinder) {
        webDataBinder.addValidators(clubFormValidator);
    }

    // 새로운 동호회 생성
    @GetMapping("/new-club") //form.html에서 post 방식으로 받아온 값을 넣어준다.
    public String newClubSubmit(@CurrentAccount Account account, Model model) {
        model.addAttribute(account);
        model.addAttribute(new ClubForm());
        return "club/form";
    }

    @PostMapping("/new-club")
    public String newClubSubmit(@CurrentAccount Account account, @Valid ClubForm clubForm, Errors errors) {
        if (errors.hasErrors()) {
            return "club/form";
        }

        Club newClub = clubService.createNewClub(modelMapper.map(clubForm, Club.class), account);
        return "redirect:/club/" + URLEncoder.encode(newClub.getPath(), StandardCharsets.UTF_8);
    }

    @GetMapping("/club/{path}")
    public String viewClub(@CurrentAccount Account account, @PathVariable String path, Model model) {
        Club club = clubService.getClub(path);
        model.addAttribute(account);
        model.addAttribute(club);
        return "club/view";
    }

    @GetMapping("/club/{path}/members")
    public String viewClubMembers(@CurrentAccount Account account, @PathVariable String path, Model model) {
        Club club = clubService.getClub(path);
        model.addAttribute(account);
        model.addAttribute(club);
        return "club/members";
    }

    @GetMapping("/club/{path}/join")
    public String joinClub(@CurrentAccount Account account, @PathVariable String path, Model model) {
        Club club = clubRepository.findClubWithManagersByPath(path);
        clubService.addMember(club, account);
        return "redirect:/club/" + club.getEncodedPath() + "/members";
    }

    @GetMapping("/club/{path}/leave")
    public String leaveClub(@CurrentAccount Account account, @PathVariable String path, Model model) {
        Club club = clubRepository.findClubWithManagersByPath(path);
        clubService.removeMember(club, account);
        return "redirect:/club/" + club.getEncodedPath() + "/members";
    }

}
