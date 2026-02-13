package com.cengiz.crm.service;

import com.cengiz.crm.entity.Lead;
import com.cengiz.crm.repository.LeadRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class LeadService {

    private final LeadRepository leadRepository;

    public List<Lead> findAll() {
        return leadRepository.findAll();
    }

    public Optional<Lead> findById(Long id) {
        return leadRepository.findById(id);
    }

    @Transactional
    public Lead save(Lead lead) {
        if (lead.getId() == null) {
            lead.setCreatedAt(LocalDateTime.now());
        }
        lead.setUpdatedAt(LocalDateTime.now());
        return leadRepository.save(lead);
    }

    @Transactional
    public void deleteById(Long id) {
        leadRepository.deleteById(id);
    }

    public long count() {
        return leadRepository.count();
    }
}
