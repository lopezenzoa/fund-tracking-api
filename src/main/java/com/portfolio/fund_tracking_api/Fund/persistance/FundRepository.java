package com.portfolio.fund_tracking_api.Fund.persistance;

import com.portfolio.fund_tracking_api.Fund.model.Fund;
import lombok.AllArgsConstructor;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@AllArgsConstructor
public class FundRepository {
    private final ObjectMapper mapper = new ObjectMapper();
    private final String filePath;

    public void write(Fund model) {
        String content = mapper.writeValueAsString(model);

        try {
            // Overwrites the file if it exists, or creates a new one
            Files.writeString(Path.of(filePath), content);
            System.out.println("Successfully wrote to the file.");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public Fund read() {
        try {
            String content = Files.readString(Path.of(filePath));
            return mapper.readValue(content, Fund.class);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
