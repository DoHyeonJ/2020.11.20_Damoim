package com.damoim.event;

import com.damoim.account.CurrentAccount;
import com.damoim.club.ClubRepository;
import com.damoim.club.ClubService;
import com.damoim.club.validator.ClubFormValidator;
import com.damoim.domain.Account;
import com.damoim.domain.Club;
import com.damoim.domain.Event;
import com.damoim.event.form.EventForm;
import com.damoim.event.validator.EventValidator;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

import javax.print.DocFlavor;
import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/club/{path}")
@RequiredArgsConstructor
public class EventController {

    private final ClubService clubService;
    private final EventService eventService;
    private final ModelMapper modelMapper;
    private final EventValidator eventValidator;
    private final EventRepository eventRepository;
    private final ClubRepository clubRepository;

    @InitBinder("eventForm")
    public void initBinder(WebDataBinder webDataBinder) {
        webDataBinder.addValidators(eventValidator);
    }

    @GetMapping("/new-event")
    public String newEventForm(@CurrentAccount Account account, @PathVariable String path, Model model) {
        Club club = clubService.getClubToUpdateStatus(account, path);
        model.addAttribute(club);
        model.addAttribute(account);
        model.addAttribute(new EventForm());
        return "event/form";
    }

    @PostMapping("/new-event")
    public String newEventSubmit(@CurrentAccount Account account, @PathVariable String path,
                                 @Valid EventForm eventForm, Errors errors, Model model) {
        Club club = clubService.getClubToUpdateStatus(account, path);
        if (errors.hasErrors()) {
            model.addAttribute(account);
            model.addAttribute(club);
            return "event/form";
        }

        Event event = eventService.createEvent(modelMapper.map(eventForm, Event.class), club, account);
        return "redirect:/club/" + club.getEncodedPath() + "/events/" + event.getId();
    }

    @GetMapping("/events/{id}")
    public String getEvent(@CurrentAccount Account account, @PathVariable String path, @PathVariable Long id,
                           Model model) {
        model.addAttribute(account);
        model.addAttribute(eventRepository.findById(id).orElseThrow());
        model.addAttribute(clubRepository.findClubWithManagersByPath(path));
        return "event/view";
    }

    @GetMapping("/events")
    public String viewClubEvents(@CurrentAccount Account account, @PathVariable String path, Model model) {
        Club club = clubService.getClub(path);
        model.addAttribute(account);
        model.addAttribute(club);

        List<Event> events = eventRepository.findByClubOrderByStartDateTime(club);
        List<Event> newEvents = new ArrayList<>();
        List<Event> oldEvents = new ArrayList<>();
        events.forEach(e -> {
            if (e.getEndDateTime().isBefore(LocalDateTime.now())) {
                oldEvents.add(e);
            } else {
                newEvents.add(e);
            }
        });

        model.addAttribute("newEvents", newEvents);
        model.addAttribute("oldEvents", oldEvents);

        return "club/events";
    }

    @GetMapping("/events/{id}/edit")
    public String updateEventForm(@CurrentAccount Account account, @PathVariable String path,
                                  @PathVariable Long id, Model model) {
        Club club = clubService.getClubToUpdate(account, path);
        Event event = eventRepository.findById(id).orElseThrow();
        model.addAttribute(club);
        model.addAttribute(account);
        model.addAttribute(event);
        model.addAttribute(modelMapper.map(event, EventForm.class));
        return "event/update-form";
    }

    @PostMapping("/events/{id}/edit")
    public String updateEventSubmit(@CurrentAccount Account account, @PathVariable String path,
                                    @PathVariable Long id, @Valid EventForm eventForm, Errors errors,
                                    Model model) {
        Club club = clubService.getClubToUpdate(account, path);
        Event event = eventRepository.findById(id).orElseThrow();
        eventForm.setEventType(event.getEventType());
        eventValidator.validateUpdateForm(eventForm, event, errors);

        if (errors.hasErrors()) {
            model.addAttribute(account);
            model.addAttribute(club);
            model.addAttribute(event);
            return "event/update-form";
        }

        eventService.updateEvent(event, eventForm);
        return "redirect:/club/" + club.getEncodedPath() + "/events/" + event.getId();
    }

}
