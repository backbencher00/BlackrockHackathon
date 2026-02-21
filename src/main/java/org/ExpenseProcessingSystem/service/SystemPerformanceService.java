package org.ExpenseProcessingSystem.service;

import org.ExpenseProcessingSystem.response.PerformanceResponse;
import org.springframework.stereotype.Service;

import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.MemoryUsage;
import java.lang.management.ThreadMXBean;
import java.time.Duration;

@Service
public class SystemPerformanceService {

    private final long startTime;

    public SystemPerformanceService() {
        this.startTime = System.currentTimeMillis();
    }

    public PerformanceResponse getPerformanceMetrics() {

        long uptimeMillis = System.currentTimeMillis() - startTime;

        Duration duration = Duration.ofMillis(uptimeMillis);

        long hours = duration.toHours();
        long minutes = duration.toMinutesPart();
        long seconds = duration.toSecondsPart();
        long millis = duration.toMillisPart();

        String formattedTime = String.format(
                "%02d:%02d:%02d.%03d",
                hours, minutes, seconds, millis
        );

        MemoryMXBean memoryBean = ManagementFactory.getMemoryMXBean();
        MemoryUsage heapUsage = memoryBean.getHeapMemoryUsage();

        double usedMB = heapUsage.getUsed() / (1024.0 * 1024.0);
        String memory = String.format("%.2f MB", usedMB);

        ThreadMXBean threadBean = ManagementFactory.getThreadMXBean();
        int threads = threadBean.getThreadCount();

        return PerformanceResponse.builder()
                .time(formattedTime)
                .memory(memory)
                .threads(threads)
                .build();
    }
}