package com.cengiz.crm.service;

import com.cengiz.crm.entity.Activity;
import com.cengiz.crm.enums.*;
import com.cengiz.crm.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Gösterge Paneli Servisi
 * Gösterge paneli istatistikleri ve metrikleri sağlar
 * 
 * @author Cengiz
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DashboardService {

    private final CustomerRepository customerRepository;
    private final LeadRepository leadRepository;
    private final OpportunityRepository opportunityRepository;
    private final ActivityRepository activityRepository;
    private final UserRepository userRepository;

    public Map<String, Object> getDashboardStats() {
        Map<String, Object> stats = new HashMap<>();

        // Customer stats
        stats.put("totalCustomers", customerRepository.count());
        stats.put("activeCustomers", customerRepository.countByStatus(CustomerStatus.ACTIVE));

        // Lead stats
        stats.put("totalLeads", leadRepository.count());
        stats.put("newLeads", leadRepository.countByStatus(LeadStatus.NEW));
        stats.put("qualifiedLeads", leadRepository.countByStatus(LeadStatus.QUALIFIED));
        stats.put("convertedLeads", leadRepository.countConverted());

        // Opportunity stats
        stats.put("totalOpportunities", opportunityRepository.count());
        stats.put("openOpportunities", opportunityRepository.countByStatus(OpportunityStatus.OPEN));
        stats.put("wonOpportunities", opportunityRepository.countByStatus(OpportunityStatus.WON));

        BigDecimal totalValue = opportunityRepository.sumValueByStatus(OpportunityStatus.OPEN);
        stats.put("pipelineValue", totalValue != null ? totalValue : BigDecimal.ZERO);

        BigDecimal wonValue = opportunityRepository.sumValueByStatus(OpportunityStatus.WON);
        stats.put("wonValue", wonValue != null ? wonValue : BigDecimal.ZERO);

        // Activity stats
        stats.put("totalActivities", activityRepository.count());
        stats.put("overdueActivities", activityRepository.findOverdueActivities(LocalDateTime.now()).size());

        // User stats
        stats.put("totalUsers", userRepository.count());
        stats.put("activeUsers", userRepository.countByStatus(UserStatus.ACTIVE));

        return stats;
    }

    public List<Activity> getRecentActivities(int limit) {
        return activityRepository.findByIsDeletedFalse().stream()
                .sorted((a1, a2) -> a2.getCreatedAt().compareTo(a1.getCreatedAt()))
                .limit(limit)
                .toList();
    }

    public List<Activity> getUpcomingActivities(int limit) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime endDate = now.plusDays(7);
        return activityRepository.findByDueDateBetween(now, endDate).stream()
                .sorted((a1, a2) -> a1.getDueDate().compareTo(a2.getDueDate()))
                .limit(limit)
                .toList();
    }

    public Map<String, Long> getPipelineData() {
        Map<String, Long> pipelineData = new HashMap<>();
        for (OpportunityStage stage : OpportunityStage.values()) {
            long count = opportunityRepository.findByStageAndIsDeletedFalse(stage).size();
            pipelineData.put(stage.name(), count);
        }
        return pipelineData;
    }
}
