package com.cengiz.crm.controller;

import com.cengiz.crm.entity.Lead;
import com.cengiz.crm.enums.LeadSource;
import com.cengiz.crm.enums.LeadStatus;
import com.cengiz.crm.service.LeadService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;

@Controller
@RequestMapping("/crm/leads")
@RequiredArgsConstructor
public class LeadController {

    private final LeadService leadService;

    @ModelAttribute("leadStatuses")
    public LeadStatus[] leadStatuses() {
        return LeadStatus.values();
    }

    @ModelAttribute("leadSources")
    public LeadSource[] leadSources() {
        return LeadSource.values();
    }

    @GetMapping
    public String listLeads(Model model) {
        model.addAttribute("leads", leadService.findAll());
        return "leads/list";
    }

    @GetMapping("/new")
    public String showAddForm(Model model) {
        model.addAttribute("lead", new Lead());
        return "leads/form";
    }

    @PostMapping("/save")
    public String saveLead(@Valid @ModelAttribute("lead") Lead lead,
            BindingResult result,
            RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            return "leads/form";
        }

        leadService.save(lead);
        redirectAttributes.addFlashAttribute("successMessage", "Potansiyel müşteri başarıyla kaydedildi.");
        return "redirect:/crm/leads";
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        return leadService.findById(id)
                .map(lead -> {
                    model.addAttribute("lead", lead);
                    return "leads/form";
                })
                .orElseGet(() -> {
                    redirectAttributes.addFlashAttribute("errorMessage", "Potansiyel müşteri bulunamadı.");
                    return "redirect:/crm/leads";
                });
    }

    @GetMapping("/delete/{id}")
    public String deleteLead(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            leadService.deleteById(id);
            redirectAttributes.addFlashAttribute("successMessage", "Potansiyel müşteri başarıyla silindi.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Silme işlemi sırasında hata oluştu.");
        }
        return "redirect:/crm/leads";
    }
}
