package com.arthmatic.shumelahire.scheduler;

import com.arthmatic.shumelahire.service.JobAdService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * Scheduled job to automatically expire job ads that have passed their closing date
 */
@Component
@ConditionalOnProperty(name = "job-ad.scheduler.enabled", havingValue = "true", matchIfMissing = true)
public class JobAdExpirationScheduler {
    
    private static final Logger logger = LoggerFactory.getLogger(JobAdExpirationScheduler.class);
    
    @Autowired
    private JobAdService jobAdService;
    
    /**
     * Runs nightly at 2:00 AM to expire job ads
     * Cron expression: second minute hour day month dayOfWeek
     */
    @Scheduled(cron = "0 0 2 * * *", zone = "UTC")
    public void expireJobAds() {
        logger.info("Starting nightly job ad expiration task");
        
        try {
            int expiredCount = jobAdService.expireAds();
            logger.info("Nightly job ad expiration completed. Expired {} ads", expiredCount);
        } catch (Exception e) {
            logger.error("Error during nightly job ad expiration", e);
        }
    }
    
    /**
     * Alternative method for testing - runs every 5 minutes
     * Uncomment @Scheduled annotation for testing purposes
     */
    // @Scheduled(fixedDelay = 300000) // 5 minutes
    public void expireJobAdsForTesting() {
        logger.info("Running job ad expiration for testing");
        
        try {
            int expiredCount = jobAdService.expireAds();
            logger.info("Test job ad expiration completed. Expired {} ads", expiredCount);
        } catch (Exception e) {
            logger.error("Error during test job ad expiration", e);
        }
    }
}