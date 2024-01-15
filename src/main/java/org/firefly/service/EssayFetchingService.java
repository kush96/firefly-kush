package org.firefly.service;

import com.google.common.util.concurrent.RateLimiter;
import org.jsoup.HttpStatusException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class EssayFetchingService {
    private static final Double MAX_REQUESTS_PER_SECOND = 15.0;
    private static final Double COOLDOWN_REQUESTS_PER_SECOND = 1.0/25.0;
    // I have made use of Guava's Rate Limiter, as this is thread safe, easy to use implementation
    // of a rate limiter. The rate of rate limiter can be dynamically changed as well during
    // execution if needed.
    private static RateLimiter rateLimiter = RateLimiter.create(MAX_REQUESTS_PER_SECOND);
    private static boolean isRateLimitEnabled = true;
    // this below variable is used to mantain the last time since rate limit had been exceeded. Used to reset rateLimiter
    // back to default rate limit. Also AtomicLong used to make this thread safe
    private static final AtomicLong lastRateLimitExceededTime = new AtomicLong(0);

    private static AtomicInteger successfulUrlFetchCount = new AtomicInteger(0);

    public String getEssayFromUrl(String url) {
        StringBuilder allParagraphsTextInUrl = new StringBuilder();
        try {
            // If rate limiting is enabled, each time ratLimit.acquire() is called from any thread,
            // The rateLimiter checks if the rate limiting set is honoured, if not, the thread is blocked
            // till it can call the API again.
            if (isRateLimitEnabled) {
                rateLimiter.acquire();
                if(shouldResetRateLimiter()){
                    resetRateLimiter();
                }
            }
            // Fetch the HTML document from the URL
            Document document;
            try {
                document = Jsoup.connect(url).get();
            } catch (HttpStatusException e) {
                System.out.println("url --> " + url + " returns status code " + e.getStatusCode());
                if(e.getStatusCode() == 999){
                    System.out.println("Error due to rate limiting, setting rate to " + COOLDOWN_REQUESTS_PER_SECOND + " Req per seconds");
                    updateRateLimitExceededTime();
                    Thread.sleep(60*1000);
                    return getEssayFromUrl(url);
                }
                return "";
            }
            successfulUrlFetchCount.incrementAndGet();
            System.out.println("url --> " + url + " fetched succesfully, success url fetch cnt --> " + successfulUrlFetchCount.get());
            // Select all paragraph elements from the document
            Elements paragraphs = document.select("p");
            // Iterate through the paragraphs and append their text content
            for (Element paragraph : paragraphs) {
                allParagraphsTextInUrl.append(paragraph.text()).append(" ");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return allParagraphsTextInUrl.toString();
    }
    private static void resetRateLimiter() {
        System.out.println("Resetting Rate Limit to " + MAX_REQUESTS_PER_SECOND + " Requests per Second");
        rateLimiter = RateLimiter.create(MAX_REQUESTS_PER_SECOND);
    }

    private boolean shouldResetRateLimiter() {
        long currentTime = System.currentTimeMillis();
        if (rateLimiter.getRate() == COOLDOWN_REQUESTS_PER_SECOND)
            System.out.println("Checking if rate limit can be brought back up again...");
        return rateLimiter.getRate() == COOLDOWN_REQUESTS_PER_SECOND &&
                currentTime - lastRateLimitExceededTime.get() >= TimeUnit.SECONDS.toMillis((long)(COOLDOWN_REQUESTS_PER_SECOND* 1000));
    }
    private void updateRateLimitExceededTime() {
        long currentTime = System.currentTimeMillis();
        lastRateLimitExceededTime.updateAndGet(x -> Math.max(x, currentTime));
    }

}
