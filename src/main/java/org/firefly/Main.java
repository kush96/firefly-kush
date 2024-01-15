package org.firefly;

import org.firefly.dto.EssayData;
import org.firefly.service.EssayAnalysingService;
import org.firefly.service.EssayFetchingService;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;


public class Main {
    private static final int MAX_THREADS = 10; // Assuming MAX_THREADS is defined

    public static void main(String[] args) {
        List<String> urls = readUrlsFromFile("src/main/resources/endg_urls");
        // executor will manage MAX_THREADS number of threads. A fixed Thread Pool
        // will initialise an executor with a set of threads, the executor then in
        // its execution, based on which thread is free, will assign tasks for threads
        // from the pool. Spawning threads is expensive process, and that's why its a good
        // idea to have a thread pool being managed by executor.
        ExecutorService executor = Executors.newFixedThreadPool(MAX_THREADS);

        for (String url : urls) {
            Runnable task = () -> {
                // Broken down process of fetching paragraphs , then analysing them into
                // two services, for seperation of concerns. eFS is concerned with parsing
                // urls and rate limiting, meanwhile eAS is more concerned with analysing the
                // paragraphs and checking for valid words
                EssayFetchingService eFS = new EssayFetchingService();
                String essay = eFS.getEssayFromUrl(url);
                EssayAnalysingService eAS = new EssayAnalysingService();
                eAS.getEssayData(url, essay);
            };
            // Non blocking call which hands task to executor to execute
            executor.execute(task);
        }
        // This is just telling exector that no more threads come in further,
        // and an ordered shutdown of thread can be inititated. Not a blocking op
        executor.shutdown();

        try {
            // Waits for tasks to complete, and if they do within 1 hour, returns true, otherwise
            // returns false and continues to wait in loop
            while (!executor.awaitTermination(1, TimeUnit.HOURS)) {
                System.out.println("Still waiting for tasks to complete...");
            }
            // count and print top 10 frequent valid words
            EssayAnalysingService.wordCntMap.entrySet().stream()
                    .sorted(Collections.reverseOrder(Map.Entry.comparingByValue()))
                    .limit(10)
                    .forEach(entry -> System.out.println(entry.getKey() + ": " + entry.getValue()));


        } catch (InterruptedException ie) {
            // This tells the main thread to halt all the threads waiting to be executed.
            executor.shutdownNow();
            Thread.currentThread().interrupt();
            System.out.println("Thread interrupted...halting all waiting threads");
        }

    }

    private static List<String> readUrlsFromFile(String filePath) {
        List<String> urls = new ArrayList<>();
        Path path = Paths.get(filePath);

        try (BufferedReader reader = Files.newBufferedReader(path)) {
            String line;
            while ((line = reader.readLine()) != null) {
                urls.add(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return urls;
    }
}


