package org.firefly.service;

import org.firefly.dto.EssayData;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.StringTokenizer;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class EssayAnalysingService {
    public static ConcurrentHashMap<String, Integer> wordCntMap = new ConcurrentHashMap<>();

    static {
        try (InputStream is = EssayAnalysingService.class.getResourceAsStream("/valid_words");
             BufferedReader reader = new BufferedReader(new InputStreamReader(is))) {
             wordCntMap.putAll(reader.lines().collect(Collectors.toMap(word -> word, word -> 0)));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public EssayData getEssayData(String url, String paragraph){
        EssayData essayData = new EssayData();
        essayData.setParagraph(paragraph);
        essayData.setUrl(url);
        essayData.setTotalTokens(getTotalTokens(paragraph));
        essayData.setTotalValidWords(getTotalValidWords(paragraph));
        return essayData;
    }
    private Integer getTotalTokens(String paragraph){
        StringTokenizer tokenizer = new StringTokenizer(paragraph);
        return tokenizer.countTokens();
    }

    /***
     * Function which checks the validity of the word by the given rules
     * @param paragraph : Text inside paragraph
     * @return total valid words
     */
    private Integer getTotalValidWords(String paragraph){
        StringTokenizer tokenizer = new StringTokenizer(paragraph);
        Integer totalValidWords = 0;
        while (tokenizer.hasMoreTokens()) {
            String token = tokenizer.nextToken();
            if(isValidWrd(token)){
                wordCntMap.compute(token, (k, v) -> (v + 1));
                totalValidWords++;
            }
        }
        return totalValidWords;
    }

    private Boolean isValidWrd(String token){
        boolean isCharCntValid = isCharCntValid(token);
        boolean areAllCharsAlphabets = isAllCharsAlphabets(token);
        boolean isContainedInWordBank = isContainedInWordBank(token);

        return isCharCntValid && areAllCharsAlphabets && isContainedInWordBank;
    }

    private Boolean isCharCntValid(String token) {
        return token.length()>=3;
    }
    private Boolean isAllCharsAlphabets(String token) {
        return token.chars().allMatch(Character::isLetter);
    }
    private Boolean isContainedInWordBank(String token) {
        return wordCntMap.containsKey(token);
    }
}
