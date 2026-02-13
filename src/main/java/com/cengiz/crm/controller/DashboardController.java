package com.cengiz.crm.controller;

import com.cengiz.crm.service.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Gösterge Paneli Controller
 * Ana gösterge paneli KPI'ları ve metrikleri
 * 
 * @author Cengiz
 */
@Controller
@RequestMapping("/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final DashboardService dashboardService;

    @GetMapping
    public String dashboard(Model model, Authentication authentication) {
        model.addAttribute("stats", dashboardService.getDashboardStats());
        model.addAttribute("recentActivities", dashboardService.getRecentActivities(10));
        model.addAttribute("upcomingActivities", dashboardService.getUpcomingActivities(10));
        model.addAttribute("pipelineData", dashboardService.getPipelineData());
        model.addAttribute("currentUser", authentication.getName());
        return "dashboard/index";
    }
}
