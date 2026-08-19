package com.portfolio.fund_tracking_api.persistance;

import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
import com.opencsv.exceptions.CsvValidationException;
import com.portfolio.fund_tracking_api.model.Fund;
import com.portfolio.fund_tracking_api.model.Holding;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Repository;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

@AllArgsConstructor
@Repository
public class FundRepository_CSV implements FundRepository {
    private final String FUND_INFORMATION_FILE_PATH = "src/main/resources/csv/fund_info.csv";
    private final String BREAKDOWN_FILE_PATH = "src/main/resources/csv/breakdown.csv";
    private final String HOLDINGS_FILE_PATH = "src/main/resources/csv/holdings.csv";

    @Override
    public void save(Fund fund) {
        if (fund == null) {
            throw new IllegalArgumentException("Fund model cannot be null");
        }

        writeFundInfo(fund);
        writeBreakdown(fund);
        writeHoldings(fund);
    }

    @Override
    public List<Fund> findAll() {
        Path infoPath = Path.of(FUND_INFORMATION_FILE_PATH);
        Path breakdownPath = Path.of(BREAKDOWN_FILE_PATH);
        Path holdingsPath = Path.of(HOLDINGS_FILE_PATH);

        if (!Files.exists(infoPath)) {
            return Collections.emptyList();
        }

        Map<String, Map<String, Double>> breakdownsByFund = readBreakdowns(breakdownPath);
        Map<String, Set<Holding>> holdingsByFund = readHoldings(holdingsPath);

        List<Fund> funds = new ArrayList<>();

        try (CSVReader reader = new CSVReader(new FileReader(infoPath.toFile()))) {
            reader.readNext(); // Skip header row

            String[] line;
            while ((line = reader.readNext()) != null) {
                String fundName = line[0];
                String date = line[1];
                Integer totalHoldings = Integer.parseInt(line[2]);
                Double shareValue = Double.valueOf(line[3]);

                Map<String, Double> breakdown = breakdownsByFund.getOrDefault(fundName, new HashMap<>());
                Set<Holding> holdings = holdingsByFund.getOrDefault(fundName, new HashSet<>());

                funds.add(new Fund(fundName, date, totalHoldings, shareValue, breakdown, holdings));
            }
        } catch (IOException | CsvValidationException e) {
            throw new RuntimeException("Error reading fund_info.csv", e);
        }

        return funds;
    }

    @Override
    public Optional<Fund> findByName(String name) {
        Path infoPath = Path.of(FUND_INFORMATION_FILE_PATH);
        Path breakdownPath = Path.of(BREAKDOWN_FILE_PATH);
        Path holdingsPath = Path.of(HOLDINGS_FILE_PATH);

        if (!Files.exists(infoPath) || name == null) {
            return Optional.empty();
        }

        // 1. Locate Fund Info
        String[] infoLine = findLineByFundName(infoPath, name);
        if (infoLine == null) {
            return Optional.empty(); // Fund not found
        }

        String date = infoLine[1];
        Integer totalHoldings = Integer.parseInt(infoLine[2]);
        Double shareValue = Double.valueOf(infoLine[3]);

        // 2. Read matched Breakdown and Holdings
        Map<String, Double> breakdown = readBreakdownByFundName(breakdownPath, name);
        Set<Holding> holdings = readHoldingsByFundName(holdingsPath, name);

        return Optional.of(new Fund(name, date, totalHoldings, shareValue, breakdown, holdings));
    }

    private void writeFundInfo(Fund fund) {
        Path filePath = Path.of(FUND_INFORMATION_FILE_PATH);
        File file = filePath.toFile();
        boolean fileExists = file.exists();

        try (CSVWriter writer = new CSVWriter(new FileWriter(file, true))) {
            // Write Header
            if (!fileExists)
                writer.writeNext(new String[]{"Name", "Date", "TotalHoldings", "Share Value"});

            // Write Data
            writer.writeNext(new String[]{
                    fund.getName(),
                    fund.getDate(),
                    String.valueOf(fund.getTotalHoldings()),
                    String.valueOf(fund.getShareValue())
            });
        } catch (IOException e) {
            throw new RuntimeException(
                    "Error writing " + Arrays.stream(FUND_INFORMATION_FILE_PATH.split("/")).toList().getLast(),
                    e
            );
        }
    }

    private void writeBreakdown(Fund fund) {
        Path filePath = Path.of(BREAKDOWN_FILE_PATH);
        File file = filePath.toFile();
        boolean fileExists = file.exists();

        try (CSVWriter writer = new CSVWriter(new FileWriter(file, true))) {
            // Write Header
            if (!fileExists)
                writer.writeNext(new String[]{"Fund Name", "Category", "Percentage", "Date"});

            // Write Rows
            if (fund.getBreakdown() != null) {
                for (Map.Entry<String, Double> entry : fund.getBreakdown().entrySet()) {
                    writer.writeNext(new String[]{
                            fund.getName(),
                            entry.getKey(),
                            String.valueOf(entry.getValue()),
                            fund.getDate()
                    });
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(
                    "Error writing " + Arrays.stream(BREAKDOWN_FILE_PATH.split("/")).toList().getLast(),
                    e
            );
        }
    }

    private void writeHoldings(Fund fund) {
        Path filePath = Path.of(HOLDINGS_FILE_PATH);
        File file = filePath.toFile();
        boolean fileExists = file.exists();

        try (CSVWriter writer = new CSVWriter(new FileWriter(file, true))) {
            // Write Header
            if (!fileExists)
                writer.writeNext(new String[]{"Fund Name", "Asset", "Percentage", "Date"});

            // Write Rows
            if (fund.getHoldings() != null) {
                for (Holding holding : fund.getHoldings()) {
                    writer.writeNext(new String[]{
                            fund.getName(),
                            holding.getName(),
                            String.valueOf(holding.getPercentage()),
                            fund.getDate()
                    });
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(
                    "Error writing " + Arrays.stream(HOLDINGS_FILE_PATH.split("/")).toList().getLast(),
                    e
            );
        }
    }

    private Map<String, Map<String, Double>> readBreakdowns(Path breakdownPath) {
        Map<String, Map<String, Double>> resultMap = new HashMap<>();
        if (!Files.exists(breakdownPath)) return resultMap;

        try (CSVReader reader = new CSVReader(new FileReader(breakdownPath.toFile()))) {
            reader.readNext(); // Skip header

            String[] line;
            while ((line = reader.readNext()) != null) {
                String fundName = line[0];
                String category = line[1];
                Double percentage = Double.parseDouble(line[2]);

                resultMap.computeIfAbsent(fundName, k -> new HashMap<>())
                        .put(category, percentage);
            }
        } catch (IOException | CsvValidationException e) {
            throw new RuntimeException(
                    "Error reading " + Arrays.stream(BREAKDOWN_FILE_PATH.split("/")).toList().getLast(),
                    e
            );
        }

        return resultMap;
    }

    private Map<String, Set<Holding>> readHoldings(Path holdingsPath) {
        Map<String, Set<Holding>> resultMap = new HashMap<>();
        if (!Files.exists(holdingsPath)) return resultMap;

        try (CSVReader reader = new CSVReader(new FileReader(holdingsPath.toFile()))) {
            reader.readNext(); // Skip header

            String[] line;
            while ((line = reader.readNext()) != null) {
                String fundName = line[0];
                String holdingName = line[1];
                Double percentage = Double.parseDouble(line[2]);

                resultMap.computeIfAbsent(fundName, k -> new HashSet<>())
                        .add(new Holding(holdingName, percentage));
            }
        } catch (IOException | CsvValidationException e) {
            throw new RuntimeException(
                    "Error reading " + Arrays.stream(HOLDINGS_FILE_PATH.split("/")).toList().getLast(),
                    e
            );
        }

        return resultMap;
    }

    private String[] findLineByFundName(Path infoPath, String fundName) {
        try (CSVReader reader = new CSVReader(new FileReader(infoPath.toFile()))) {
            reader.readNext(); // Skip header

            String[] line;
            while ((line = reader.readNext()) != null) {
                if (line.length > 0 && line[0].equalsIgnoreCase(fundName)) {
                    return line;
                }
            }
        } catch (IOException | CsvValidationException e) {
            throw new RuntimeException(
                    "Error reading " + Arrays.stream(FUND_INFORMATION_FILE_PATH.split("/")).toList().getLast(),
                    e
            );
        }
        return null;
    }

    private Map<String, Double> readBreakdownByFundName(Path breakdownPath, String fundName) {
        Map<String, Double> breakdown = new HashMap<>();
        if (!Files.exists(breakdownPath)) return breakdown;

        try (CSVReader reader = new CSVReader(new FileReader(breakdownPath.toFile()))) {
            reader.readNext(); // Skip header

            String[] line;
            while ((line = reader.readNext()) != null) {
                if (line.length >= 3 && line[0].equalsIgnoreCase(fundName)) {
                    breakdown.put(line[1], Double.parseDouble(line[2]));
                }
            }
        } catch (IOException | CsvValidationException e) {
            throw new RuntimeException(
                    "Error reading " + Arrays.stream(BREAKDOWN_FILE_PATH.split("/")).toList().getLast(),
                    e
            );
        }

        return breakdown;
    }

    private Set<Holding> readHoldingsByFundName(Path holdingsPath, String fundName) {
        Set<Holding> holdings = new HashSet<>();
        if (!Files.exists(holdingsPath)) return holdings;

        try (CSVReader reader = new CSVReader(new FileReader(holdingsPath.toFile()))) {
            reader.readNext(); // Skip header

            String[] line;
            while ((line = reader.readNext()) != null) {
                if (line.length >= 3 && line[0].equalsIgnoreCase(fundName)) {
                    holdings.add(new Holding(line[1], Double.parseDouble(line[2])));
                }
            }
        } catch (IOException | CsvValidationException e) {
            throw new RuntimeException(
                    "Error reading " + Arrays.stream(HOLDINGS_FILE_PATH.split("/")).toList().getLast(),
                    e
            );
        }

        return holdings;
    }
}
