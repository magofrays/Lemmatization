package org.magofrays;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Scanner;

@Component
@RequiredArgsConstructor
public class CommandLineRunner {
    private final Lemmatization lemmatizationWorker;
    private final UserInputParser userInputParser;

    public void run(){
        System.out.println("Если ввод пустой, программа завершит свою работу.");
        while(true){
            System.out.println("Введите текст (Ctrl + D чтобы закончить ввод):");
            List<String> words = userInputParser.parse(System.in);
            if(words.isEmpty()){
                return;
            }
            var result = lemmatizationWorker.lemmatize(words);
            int count = 0;
            for(var word : words){
                System.out.print(word + result.getRight().get(word) + " ");
                count++;
                if(count % 5 == 0){
                    System.out.println();
                }
            }
            System.out.println();
            System.out.println("Точность: " + result.getLeft());
        }
    }
}
