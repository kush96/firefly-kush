package org.firefly;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.firefly.service.EssayAnalysingService;
import org.firefly.service.EssayFetchingService;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Collectors;


public class Main {
    private static final int MAX_THREADS = 10; // Assuming MAX_THREADS is defined

    public static void main(String[] args) {
        List<String> urls = readUrlsFromFile("/endg_urls");
        int numOfUrlsToProcess = args.length > 0 ? parseArg(args[0], urls.size()) : urls.size();
        urls = urls.subList(0, Math.min(numOfUrlsToProcess, urls.size()));

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
            System.out.println("*********************XXXXXXXXXXX*********************");
            System.out.println("Most Frequent Valid Words");
            // count and print top 10 frequent valid words
            // Convert the top 10 entries of the map into a JSON string
            Map<String, Integer> topEntries = new LinkedHashMap<>();
            EssayAnalysingService.wordCntMap.entrySet().stream()
                    .sorted(Collections.reverseOrder(Map.Entry.comparingByValue()))
                    .limit(10)
                    .forEachOrdered(entry -> topEntries.put(entry.getKey(), entry.getValue()));

            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            String prettyJson = gson.toJson(topEntries);
            System.out.println(prettyJson);


        } catch (InterruptedException ie) {
            // This tells the main thread to halt all the threads waiting to be executed.
            executor.shutdownNow();
            Thread.currentThread().interrupt();
            System.out.println("Thread interrupted...halting all waiting threads");
        }

    }

    private static List<String> readUrlsFromFile(String resourcePath) {
        List<String> urls = new ArrayList<>();

        try (InputStream inputStream = Main.class.getResourceAsStream(resourcePath);
             BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
            String line;
            while ((line = reader.readLine()) != null) {
                urls.add(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return urls;
    }
    private static int parseArg(String arg, int defaultValue) {
        try {
            return Integer.parseInt(arg);
        } catch (NumberFormatException e) {
            System.err.println("Invalid number format. Using default value: " + defaultValue);
            return defaultValue;
        }
    }
}


