package com.damoim.club;

import com.damoim.account.CurrentAccount;
import com.damoim.club.form.ClubDescriptionForm;
import com.damoim.domain.Account;
import com.damoim.domain.Club;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Controller
@RequestMapping("/study/{path}/settings")
@RequiredArgsConstructor
public class ClubSettingsController {

    private final ClubService clubService;
    private final ModelMapper modelMapper;

    @GetMapping("/description")
    public String viewClubSetting(@CurrentAccount Account account, @PathVariable String path, Model model) {
        Club club = clubService.getClubToUpdate(account, path);
        model.addAttribute(account);
        model.addAttribute(club);
        model.addAttribute(modelMapper.map(club, ClubDescriptionForm.class));
        return "club/settings/description";
    }

    @PostMapping("/description")
    public String updateClubInfo(@CurrentAccount Account account, @PathVariable String path,
                                 @Valid ClubDescriptionForm clubDescriptionForm, Errors errors,
                                 Model model, RedirectAttributes attributes) {
        Club club = clubService.getClubToUpdate(account, path);

        if (errors.hasErrors()) {
            model.addAttribute(attributes);
            model.addAttribute(club);
            return "club/settings/description";
        }

        clubService.updateClubDescription(club, clubDescriptionForm);
        attributes.addFlashAttribute("message", "동호회 소개를 수정했습니다.");
        return "redirect:/club/" + getPath(path) + "/settings/description";
    }

    private String getPath(String path) {
        return URLEncoder.encode(path, StandardCharsets.UTF_8);
    }


}
