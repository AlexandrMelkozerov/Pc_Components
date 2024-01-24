package ru.melkozerovau.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import ru.melkozerovau.config.AuthenticationFacade;
import ru.melkozerovau.entity.Component;
import ru.melkozerovau.entity.Store;
import ru.melkozerovau.repository.StoreRepository;
import ru.melkozerovau.service.UserActionLogService;
import ru.melkozerovau.repository.ComponentRepository;

import java.util.List;
import java.util.Optional;
@Slf4j
@Controller
public class StoreController {
    @Autowired
    private StoreRepository storeRepository;

    @Autowired
    private AuthenticationFacade authenticationFacade;

    @Autowired
    private UserActionLogService userActionLogService;

    @Autowired
    private ComponentRepository componentRepository;

    @GetMapping("/list-stores")
    public ModelAndView getAllStores() {
        log.info("/list -> connection");
        ModelAndView mav = new ModelAndView("list-stores");
        mav.addObject("stores", storeRepository.findAll());
        return mav;
    }

    @GetMapping("/addStoreForm")
    public ModelAndView addStoreForm() {
        if (authenticationFacade.getAuthentication().getAuthorities().stream()
                .anyMatch(r -> r.getAuthority().equals("READ_ONLY"))) {
            return new ModelAndView("redirect:/list-stores");
        } else {
            ModelAndView mav = new ModelAndView("add-store-form");
            Store store = new Store();
            mav.addObject("store", store);
            return mav;
        }
    }
    @PostMapping("/saveStore")
    public String saveStore(@ModelAttribute Store store) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentPrincipalName = authentication.getName();
        store.setCreated(currentPrincipalName);
        storeRepository.save(store);
        userActionLogService.logAction(currentPrincipalName, "Создание магазина");
        return "redirect:/list-stores";
    }
    @GetMapping("/showUpdateFormShop")
    public ModelAndView showUpdateForm(@RequestParam Long storeId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentPrincipalName = authentication.getName();
        Optional<Store> optionalStore = storeRepository.findById(storeId);
        if (optionalStore.isPresent()) {
            Store store = optionalStore.get();
            if (authentication.getAuthorities().stream()
                    .anyMatch(r -> r.getAuthority().equals("ADMIN")) ||
                    (store.getCreated().equals(currentPrincipalName))) {
                ModelAndView mav = new ModelAndView("add-store-form");
                mav.addObject("store", store);
                userActionLogService.logAction(currentPrincipalName, "Изменение магазина");
                return mav;
            } else {
                return new ModelAndView("redirect:/list-stores");
            }
        } else {
            return new ModelAndView("redirect:/list-stores");
        }
    }
    @GetMapping("/deleteStore")
    public String deleteStore(@RequestParam Long storeId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentPrincipalName = authentication.getName();
        Optional<Store> optionalStore = storeRepository.findById(storeId);
        if (optionalStore.isPresent()) {
            Store store = optionalStore.get();
            if (authentication.getAuthorities().stream()
                    .anyMatch(r -> r.getAuthority().equals("ADMIN")) ||
                    (store.getCreated().equals(currentPrincipalName))) {
                storeRepository.deleteById(storeId);
                userActionLogService.logAction(currentPrincipalName, "Удаление магазина");
            }
        }
        return "redirect:/list-stores";
    }

    @GetMapping ("/showStoreDetails")
    public ModelAndView showStoreDetails(@RequestParam Long storeId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentPrincipalName = authentication.getName();
        Optional<Store> optionalStore = storeRepository.findById(storeId);
        List<Component> allComponents = componentRepository.findAll();
        if (optionalStore.isPresent()) {
            Store store = optionalStore.get();
            if (authentication.getAuthorities().stream()
                    .anyMatch(r -> r.getAuthority().equals("ADMIN")) ||
                    (store.getCreated().equals(currentPrincipalName))) {
                ModelAndView mav = new ModelAndView("store-details");
                mav.addObject("store", store);
                mav.addObject("components", store.getComponents());
                mav.addObject("allComponents", allComponents);
                userActionLogService.logAction(currentPrincipalName, "Просмотр списка доступных комплектующих");
                return mav;
            } else {
                return new ModelAndView("redirect:/list-stores");
            }
        } else {
            return new ModelAndView("redirect:/list-stores");
        }
    }

    @PostMapping("/addExistingComponentToStore")
    public String addExistingComponentToStore(@RequestParam Long storeId, @RequestParam Long componentId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentPrincipalName = authentication.getName();
        Optional<Store> storeOptional = storeRepository.findById(storeId);
        Optional<Component> componentOptional = componentRepository.findById(componentId);

        if (storeOptional.isPresent() && componentOptional.isPresent()) {
            Store store = storeOptional.get();
            Component component = componentOptional.get();

            if (!store.getComponents().contains(component)) {
                store.getComponents().add(component);
                storeRepository.save(store);
                userActionLogService.logAction(currentPrincipalName, "Добавление товара в список");
                return "redirect:/showStoreDetails?storeId=" + storeId;
            } else {
                return "redirect:/list-stores";
            }
        }
        return "redirect:/list-stores";
    }
}