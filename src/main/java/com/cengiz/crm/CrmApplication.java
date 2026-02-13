package com.cengiz.crm;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Kurumsal CRM Uygulaması
 * 
 * CRM sistemi için ana uygulama sınıfı.
 * Özellikler:
 * - Müşteri İlişkileri Yönetimi
 * - Potansiyel Müşteri ve Fırsat Takibi
 * - Satış Hunisi Yönetimi
 * - Aktivite ve Görev Yönetimi
 * - Raporlama ve Analitik
 * 
 * @author Cengiz
 * @version 1.0.0
 */
@SpringBootApplication
@EnableCaching
@EnableAsync
@EnableScheduling
public class CrmApplication {

    public static void main(String[] args) {
        SpringApplication.run(CrmApplication.class, args);
    }
}
