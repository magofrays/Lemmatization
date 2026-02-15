package org.magofrays;

import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

@Component
public class UserInputParser {

    public List<String> parse(InputStream inputStream) {
        List<String> elements = new ArrayList<>();
        Scanner scanner = new Scanner(inputStream);

        System.out.println("Введите текст (пустая строка для завершения ввода):");

        while (scanner.hasNextLine()) {
            String line = scanner.nextLine();

            if (line.trim().isEmpty()) {
                break;
            }

            String[] words = line.split("[\\s,.;:!?]+");
            for (String word : words) {
                if (!word.isEmpty()) {
                    elements.add(word.toLowerCase().replace('ё', 'е'));
                }
            }
        }

        return elements;
    }
}
