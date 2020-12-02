package com.damoim.modules.club;

import com.damoim.modules.account.CurrentAccount;
import com.damoim.modules.club.form.ClubDescriptionForm;
import com.damoim.modules.account.Account;
import com.damoim.modules.tag.Tag;
import com.damoim.modules.zone.Zone;
import com.damoim.modules.tag.TagForm;
import com.damoim.modules.tag.TagRepository;
import com.damoim.modules.tag.TagService;
import com.damoim.modules.zone.ZoneForm;
import com.damoim.modules.zone.ZoneRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/club/{path}/settings")
@RequiredArgsConstructor
public class ClubSettingsController {

    private final ClubService clubService;
    private final ModelMapper modelMapper;
    private final TagService tagService;
    private final TagRepository tagRepository;
    private final ZoneRepository zoneRepository;
    private final ObjectMapper objectMapper;
    private final ClubRepository clubRepository;

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
        return "redirect:/club/" + club.getEncodedPath() + "/settings/description";
    }

    @GetMapping("/banner")
    public String clubImageForm(@CurrentAccount Account account, @PathVariable String path, Model model) {
        Club club = clubService.getClubToUpdate(account, path);
        model.addAttribute(account);
        model.addAttribute(club);
        return "club/settings/banner";
    }

    @PostMapping("/banner")
    public String clubImageSubmit(@CurrentAccount Account account, @PathVariable String path,
                                  String image, RedirectAttributes attributes) {
        Club club = clubService.getClubToUpdate(account, path);
        clubService.updateClubImage(club, image);
        attributes.addFlashAttribute("message", "동호회 이미지를 수정했습니다.");
        return "redirect:/club/" + club.getEncodedPath() + "/settings/banner";
    }

    @PostMapping("/banner/enable")
    public String enableClubBanner(@CurrentAccount Account account, @PathVariable String path) {
        Club club = clubService.getClubToUpdate(account, path);
        clubService.enableClubBanner(club);
        return "redirect:/club/" + club.getEncodedPath() + "/settings/banner";
    }

    @PostMapping("/banner/disable")
    public String disableClubBanner(@CurrentAccount Account account, @PathVariable String path) {
        Club club = clubService.getClubToUpdate(account, path);
        clubService.disableClubBanner(club);
        return "redirect:/club/" + club.getEncodedPath() + "/settings/banner";
    }

    @GetMapping("/tags")
    public String clubTagsForm(@CurrentAccount Account account, @PathVariable String path, Model model) throws JsonProcessingException {
        Club club = clubService.getClubToUpdate(account, path);
        model.addAttribute(account);
        model.addAttribute(club);

        model.addAttribute("tags", club.getTags().stream()
                .map(Tag::getTitle).collect(Collectors.toList()));
        List<String> allTagTitles = tagRepository.findAll().stream()
                .map(Tag::getTitle).collect(Collectors.toList());
        model.addAttribute("whitelist", objectMapper.writeValueAsString(allTagTitles));
        return "club/settings/tags";
    }

    @PostMapping("/tags/add")
    @ResponseBody
    public ResponseEntity addTag(@CurrentAccount Account account, @PathVariable String path,
                                 @RequestBody TagForm tagForm) {
        Club club = clubService.getClubToUpdateTag(account, path);
        Tag tag = tagService.findOrCreateNew(tagForm.getTagTitle());
        clubService.addTag(club, tag);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/tags/remove")
    @ResponseBody
    public ResponseEntity removeTag(@CurrentAccount Account account, @PathVariable String path, @RequestBody TagForm tagForm) {
        Club club = clubService.getClubToUpdateTag(account, path);
        Tag tag = tagRepository.findByTitle(tagForm.getTagTitle());
        if (tag == null) {
            return ResponseEntity.badRequest().build();
        }

        clubService.removeTag(club, tag);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/zones")
    public String clubZonesForm(@CurrentAccount Account account, @PathVariable String path, Model model) throws JsonProcessingException {
        Club club = clubService.getClubToUpdate(account, path);
        model.addAttribute(account);
        model.addAttribute(club);
        model.addAttribute("zones", club.getZones().stream()
                .map(Zone::toString).collect(Collectors.toList()));
        List<String> allZones = zoneRepository.findAll().stream().map(Zone::toString).collect(Collectors.toList());
        model.addAttribute("whitelist", objectMapper.writeValueAsString(allZones));
        return "club/settings/zones";
    }

    @PostMapping("/zones/add")
    @ResponseBody
    public ResponseEntity addZone(@CurrentAccount Account account, @PathVariable String path,
                                  @RequestBody ZoneForm zoneForm) {
        Club club = clubService.getClubToUpdateZone(account, path);
        Zone zone = zoneRepository.findByCityAndProvince(zoneForm.getCityName(), zoneForm.getProvinceName());
        if (zone == null) {
            return ResponseEntity.badRequest().build();
        }

        clubService.addZone(club, zone);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/zones/remove")
    @ResponseBody
    public ResponseEntity removeZone(@CurrentAccount Account account, @PathVariable String path, @RequestBody ZoneForm zoneForm) {
        Club club = clubService.getClubToUpdateZone(account, path);
        Zone zone = zoneRepository.findByCityAndProvince(zoneForm.getCityName(), zoneForm.getProvinceName());
        if (zone == null) {
            return ResponseEntity.badRequest().build();
        }

        clubService.removeZone(club, zone);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/club")
    public String clubSettingForm(@CurrentAccount Account account, @PathVariable String path, Model model) {
        Club club = clubService.getClubToUpdateZone(account, path);
        model.addAttribute(account);
        model.addAttribute(club);
        return "club/settings/club";
    }

    @PostMapping("/club/publish")
    public String publishClub(@CurrentAccount Account account, @PathVariable String path,
                              RedirectAttributes attributes) {
        Club club = clubService.getClubToUpdateStatus(account, path);
        clubService.publish(club);
        attributes.addFlashAttribute("message", "동호회를 공개했습니다.");
        return "redirect:/club/" + club.getEncodedPath() + "/settings/club";
    }

    @PostMapping("/club/close")
    public String closeClub(@CurrentAccount Account account, @PathVariable String path,
                              RedirectAttributes attributes) {
        Club club = clubService.getClubToUpdateStatus(account, path);
        clubService.close(club);
        attributes.addFlashAttribute("message", "동호회를 종료했습니다.");
        return "redirect:/club/" + club.getEncodedPath() + "/settings/club";
    }

    @PostMapping("/recruit/start")
    public String startRecruit(@CurrentAccount Account account, @PathVariable String path, Model model,
                            RedirectAttributes attributes) {
        Club club = clubService.getClubToUpdateStatus(account, path);
        if (!club.canUpdateRecruiting()) {
            attributes.addFlashAttribute("message", "인원모집 설정은 한시간에 한번만 변경할 수 있습니다.");
            return "redirect:/club/" + club.getEncodedPath() + "/settings/club";
        }
        clubService.startRecruit(club);
        attributes.addFlashAttribute("message", "인원 모집을 시작합니다.");
        return "redirect:/club/" + club.getEncodedPath() + "/settings/club";
    }

    @PostMapping("/recruit/stop")
    public String stopRecruit(@CurrentAccount Account account, @PathVariable String path, Model model,
                               RedirectAttributes attributes) {
        Club club = clubService.getClubToUpdateStatus(account, path);
        if (!club.canUpdateRecruiting()) {
            attributes.addFlashAttribute("message", "인원모집 설정은 한시간에 한번만 변경할 수 있습니다.");
            return "redirect:/club/" + club.getEncodedPath() + "/settings/club";
        }
        clubService.stopRecruit(club);
        attributes.addFlashAttribute("message", "인원 모집을 시작합니다.");
        return "redirect:/club/" + club.getEncodedPath() + "/settings/club";
    }

    @PostMapping("/club/path")
    public String updateClubPath(@CurrentAccount Account account, @PathVariable String path, String newPath,
                                 Model model, RedirectAttributes attributes) {
        Club club = clubService.getClubToUpdateStatus(account, path);
        if (!clubService.isValidPath(newPath)) {
            model.addAttribute(attributes);
            model.addAttribute(club);
            model.addAttribute("clubPathError", "해당 동호회 경로는 사용할 수 없습니다. 다른 값을 입력하세요.");
            return "club/settings/club";
        }

        clubService.updateClubPath(club, newPath);
        attributes.addFlashAttribute("message", "동호회 경로를 수정했습니다.");
        return "redirect:/club/" + club.getEncodedPath() + "/settings/club";
    }

    @PostMapping("/club/title")
    public String updateClubTitle(@CurrentAccount Account account, @PathVariable String path, String newTitle,
                                 Model model, RedirectAttributes attributes) {
        Club club = clubService.getClubToUpdateStatus(account, path);
        if (!clubService.isValidTitle(newTitle)) {
            model.addAttribute(attributes);
            model.addAttribute(club);
            model.addAttribute("clubTitleError", "동호회 이름을 다시 입력하세요");
            return "club/settings/club";
        }

        clubService.updateClubTitle(club, newTitle);
        attributes.addFlashAttribute("message", "동호회 이름을 수정했습니다.");
        return "redirect:/club/" + club.getEncodedPath() + "/settings/club";
    }

    @PostMapping("/club/remove")
    public String removeClub(@CurrentAccount Account account, @PathVariable String path, Model model) {
        Club club = clubService.getClubToUpdateStatus(account, path);
        clubService.remove(club);
        return "redirect:/";
    }

}
