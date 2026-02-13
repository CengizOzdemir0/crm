package com.cengiz.crm.controller;

import com.cengiz.crm.entity.Customer;
import com.cengiz.crm.enums.CustomerStatus;
import com.cengiz.crm.enums.CustomerType;
import com.cengiz.crm.service.CustomerService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;

@Controller
@RequestMapping("/crm/customers")
@RequiredArgsConstructor
public class CustomerController {

    private final CustomerService customerService;

    @ModelAttribute("customerTypes")
    public CustomerType[] customerTypes() {
        return CustomerType.values();
    }

    @ModelAttribute("customerStatuses")
    public CustomerStatus[] customerStatuses() {
        return CustomerStatus.values();
    }

    @GetMapping
    public String listCustomers(Model model) {
        model.addAttribute("customers", customerService.findAll());
        return "customers/list";
    }

    @GetMapping("/new")
    public String showAddForm(Model model) {
        model.addAttribute("customer", new Customer());
        return "customers/form";
    }

    @PostMapping("/save")
    public String saveCustomer(@Valid @ModelAttribute("customer") Customer customer,
            BindingResult result,
            RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            return "customers/form";
        }

        customerService.save(customer);
        redirectAttributes.addFlashAttribute("successMessage", "Müşteri başarıyla kaydedildi.");
        return "redirect:/crm/customers";
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        return customerService.findById(id)
                .map(customer -> {
                    model.addAttribute("customer", customer);
                    return "customers/form";
                })
                .orElseGet(() -> {
                    redirectAttributes.addFlashAttribute("errorMessage", "Müşteri bulunamadı.");
                    return "redirect:/crm/customers";
                });
    }

    @GetMapping("/delete/{id}")
    public String deleteCustomer(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            customerService.deleteById(id);
            redirectAttributes.addFlashAttribute("successMessage", "Müşteri başarıyla silindi.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Müşteri silinirken hata oluştu.");
        }
        return "redirect:/crm/customers";
    }
}
