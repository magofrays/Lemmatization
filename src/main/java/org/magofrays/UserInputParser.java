package org.magofrays;

import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

@Component
public class UserInputParser {

    public List<String> parse(InputStream inputStream){
        List<String> elements = new ArrayList<>();
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        try{
            String line;
            while((line = reader.readLine()) != null){
                if(line.trim().isEmpty()){
                    break;
                }
                String[] words = line.split("[\\s,.;:!?]+");
                for (String word : words) {
                    if (!word.isEmpty()) {
                        elements.add(word.toLowerCase().replace('ё','е'));
                    }
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return elements;
    }
}
