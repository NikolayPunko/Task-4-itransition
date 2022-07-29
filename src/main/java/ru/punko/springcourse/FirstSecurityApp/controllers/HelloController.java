package ru.punko.springcourse.FirstSecurityApp.controllers;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ru.punko.springcourse.FirstSecurityApp.models.Person;
import ru.punko.springcourse.FirstSecurityApp.security.PersonDetails;
import ru.punko.springcourse.FirstSecurityApp.services.AdminService;
import ru.punko.springcourse.FirstSecurityApp.services.PeopleService;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Controller
public class HelloController {

    private final AdminService adminService;
    private final PeopleService peopleService;

    @Autowired
    public HelloController(AdminService adminService, PeopleService peopleService) {
        this.adminService = adminService;
        this.peopleService = peopleService;
    }

    @GetMapping("/hello")
    public String listPeople(
            Model model,
            @RequestParam("page") Optional<Integer> page,
            @RequestParam("size") Optional<Integer> size) {
        int currentPage = page.orElse(1);
        int pageSize = size.orElse(5);

        Page<Person> personPage = peopleService.findPaginated(PageRequest.of(currentPage - 1, pageSize));

        model.addAttribute("bookPage", personPage);

        int totalPages = personPage.getTotalPages();
        if (totalPages > 0) {
            List<Integer> pageNumbers = IntStream.rangeClosed(1, totalPages)
                    .boxed()
                    .collect(Collectors.toList());
            model.addAttribute("pageNumbers", pageNumbers);
        }

        return "hello";
    }



    @PostMapping("/delete")
    public String delete(@RequestParam(value = "list", required = false) ArrayList<Integer> list) {
        if (list != null) {
            System.out.println("delete");
            for (int id : list) {
                peopleService.delete(id);
                if (Integer.parseInt(SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString()) == (id)) {
                    SecurityContextHolder.getContext().setAuthentication(null);
                }
            }
        }
        return "redirect:hello";
    }

    @PostMapping("/block")
    public String block(@RequestParam(value = "list", required = false) ArrayList<Integer> list) {
        if (list != null) {
            System.out.println("block");
            for (int id : list) {
                Person updatedPerson = peopleService.findById(id).get();
                updatedPerson.setStatus("Block");
                peopleService.save(updatedPerson);
                if (Integer.parseInt(SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString()) == (id)) {
                    SecurityContextHolder.getContext().setAuthentication(null);
                }
            }
        }
        return "redirect:hello";
    }

    @PostMapping("/unblock")
    public String unblock(@RequestParam(value = "list", required = false) ArrayList<Integer> list) {
        if (list != null) {
            System.out.println("unblock");
            for (int id : list) {
                Person updatedPerson = peopleService.findById(id).get();
                updatedPerson.setStatus("Unblock");
                peopleService.save(updatedPerson);
            }
        }
        return "redirect:hello";
    }

    @GetMapping("/showUserInfo")
    public String showUserInfo() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        PersonDetails personDetails = (PersonDetails) authentication.getPrincipal();
        System.out.println(personDetails.getPerson());
        return "hello";
    }

    @GetMapping("/admin")
    public String adminPage() {
        adminService.doAdminStuff();
        return "admin";
    }

}
