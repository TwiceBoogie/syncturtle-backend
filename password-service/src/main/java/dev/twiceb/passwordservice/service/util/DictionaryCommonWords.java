package dev.twiceb.passwordservice.service.util;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

@Getter
@Slf4j
@Component
public class DictionaryCommonWords {

    private static final String FILE_PATH = "src/main/resources/utils/hashed_common_words.txt";
    private final Set<String> hashedDictionaryWords = new HashSet<>();

    public DictionaryCommonWords() {
        readHashedDictionaryWords();
    }

    private void readHashedDictionaryWords() {
        try (BufferedReader reader = new BufferedReader(new FileReader(DictionaryCommonWords.FILE_PATH))) {
            String line;
            while ((line = reader.readLine()) != null) {
                hashedDictionaryWords.add(line.trim()); // Add the hashed word to the set
            }
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }
}
