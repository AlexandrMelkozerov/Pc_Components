package ru.melkozerovau.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import ru.melkozerovau.config.AuthenticationFacade;
import ru.melkozerovau.entity.Component;
import ru.melkozerovau.service.UserActionLogService;
import ru.melkozerovau.repository.ComponentRepository;

import java.util.Optional;

@Slf4j
@Controller
public class ComponentController {
    @Autowired
    private ComponentRepository componentRepository;

    @Autowired
    private AuthenticationFacade authenticationFacade;

    @Autowired
    private UserActionLogService userActionLogService;

    @GetMapping("/list-components")
    public ModelAndView getAllComponents() {
        log.info("/list -> connection");
        ModelAndView mav = new ModelAndView("list-components");
        mav.addObject("components", componentRepository.findAll());
        return mav;
    }

    @GetMapping("/addComponentForm")
    public ModelAndView addComponentForm() {
        if (authenticationFacade.getAuthentication().getAuthorities().stream()
                .anyMatch(r -> r.getAuthority().equals("READ_ONLY"))) {
            return new ModelAndView("redirect:/list-components");
        } else {
            ModelAndView mav = new ModelAndView("add-component-form");
            Component component = new Component();
            mav.addObject("component", component);
            return mav;
        }
    }

    @PostMapping("/saveComponent")
    public String saveComponent(@ModelAttribute Component component) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentPrincipalName = authentication.getName();
        component.setCreated(currentPrincipalName);
        componentRepository.save(component);
        userActionLogService.logAction(currentPrincipalName, "Создание товара");
        return "redirect:/list-components";
    }

    @GetMapping("/showUpdateForm")
    public ModelAndView showUpdateForm(@RequestParam Long componentId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentPrincipalName = authentication.getName();
        Optional<Component> optionalComponent = componentRepository.findById(componentId);
        if (optionalComponent.isPresent()) {
            Component component = optionalComponent.get();
            if (authentication.getAuthorities().stream()
                    .anyMatch(r -> r.getAuthority().equals("ADMIN")) ||
                    (component.getCreated().equals(currentPrincipalName))) {
                ModelAndView mav = new ModelAndView("add-component-form");
                mav.addObject("component", component);
                userActionLogService.logAction(currentPrincipalName, "Изменение товара");
                return mav;
            } else {
                return new ModelAndView("redirect:/list-components");
            }
        } else {
            return new ModelAndView("redirect:/list-components");
        }
    }

    @GetMapping("/deleteComponent")
    public String deleteComponent(@RequestParam Long componentId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentPrincipalName = authentication.getName();
        Optional<Component> optionalComponent = componentRepository.findById(componentId);
        if (optionalComponent.isPresent()) {
            Component component = optionalComponent.get();
            if (authentication.getAuthorities().stream()
                    .anyMatch(r -> r.getAuthority().equals("ADMIN")) ||
                    (component.getCreated().equals(currentPrincipalName))) {
                componentRepository.deleteById(componentId);
                userActionLogService.logAction(currentPrincipalName, "Удаление товара");
            }
        }
        return "redirect:/list-components";
    }
}