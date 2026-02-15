package org.magofrays;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.tuple.MutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.text.similarity.LevenshteinDistance;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
@RequiredArgsConstructor
public class Lemmatization {

    @Data
    @AllArgsConstructor
    public static class Lemma{
        private String name;
        private List<MorphDict.LemmataDesc> rules;
        @Override
        public String toString() {
            return "{" +name+ ": " + rules +"}";
        }
    }

    private final MorphDict morphDict;

    public Pair<Float, Map<String, Lemma>> lemmatize(List<String> words){
        Map<String, Lemma> result = new HashMap<>();
        List<Float> scores = new ArrayList<>();
        for(var word: words){
            if(morphDict.getForm().containsKey(word)){
                var lemma = morphDict.getForm().get(word);
                result.put(word, new Lemma(lemma, extractRulesFromLemma(lemma)));
                scores.add(1.0f);
            }
            else{
                Integer minDistance = Integer.MAX_VALUE;
                String minKey = null;
                for(var lemma: morphDict.getLemmata().keySet()){
                    var distance = LevenshteinDistance.getDefaultInstance().apply(lemma, word);
                    if(distance < minDistance){
                        minDistance = distance;
                        minKey = lemma;
                    }
                }
                assert minKey != null;
                float score = (float) minDistance / Integer.max(word.length(), minKey.length());
                scores.add(1.0f - score);
                result.put(word, new Lemma(minKey, extractRulesFromLemma(minKey)));
            }
        }
        Float scoreSum = 0.0f;
        for(var score : scores){
            scoreSum += score;
        }
        Float score = scoreSum / scores.size();
        return new MutablePair<>(score, result);
    }

    private List<MorphDict.LemmataDesc> extractRulesFromLemma(String lemma) {

        var rulesSlang = morphDict.getLemmata().get(lemma);
        List<MorphDict.LemmataDesc> rules = new ArrayList<>();
        for(var rule : rulesSlang){
            rules.add(morphDict.getGrammemes().get(rule));
        }
        return rules;
    }

}
