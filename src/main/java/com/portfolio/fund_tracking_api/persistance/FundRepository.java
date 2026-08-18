package com.portfolio.fund_tracking_api.persistance;

import com.portfolio.fund_tracking_api.model.Fund;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Repository;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@Repository
@AllArgsConstructor
public class FundRepository {
    private ObjectMapper mapper;

    public void save(Fund fund) {
        String rawFund = mapper.writeValueAsString(fund);
        Path path = buildPath(fund.getName());

        try {
            // Overwrites the file if it exists, or creates a new one
            Files.writeString(path, rawFund);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public Fund read(String fundName) {
        try {
            if (fundName == null || fundName.isBlank())
                throw new IllegalArgumentException("Fund Name Not Found");

            Path path = buildPath(fundName);

            String rawFund = Files.readString(path);

            return mapper.readValue(rawFund, Fund.class);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private Path buildPath(String fundName) {
        return Path.of("src/main/resources/data/" + fundName + ".json");
    }
}
