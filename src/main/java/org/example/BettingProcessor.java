package org.example;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

public class BettingProcessor {

    public static void main(String[] args) {
        String playerDataFile = "src/main/resources/player_data.txt";
        String matchDataFile = "src/main/resources/match_data.txt";
        String resultFile = "src/main/result.txt";

        try {
            Map<String, Player> players = processPlayerData(playerDataFile);
            processMatchData(matchDataFile, players);
            writeResults(resultFile, players);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static Map<String, Player> processPlayerData(String filePath) throws IOException {
        Map<String, Player> players = new HashMap<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] values = line.split(",");
                String playerId = values[0];
                String operation = values[1];
                String matchId = values.length > 2 ? values[2] : null;
                int coins = Integer.parseInt(values[3]);
                String side = values.length > 4 ? values[4] : null;

                players.computeIfAbsent(playerId, Player::new);
                Player player = players.get(playerId);

                switch (operation) {
                    case "DEPOSIT":
                        player.deposit(coins);
                        break;
                    case "BET":
                        player.bet(matchId, coins, side);
                        break;
                    case "WITHDRAW":
                        player.withdraw(coins);
                        break;
                }
            }
        }

        return players;
    }

    private static void processMatchData(String filePath, Map<String, Player> players) throws IOException {
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] values = line.split(",");
                String matchId = values[0];
                double rateA = Double.parseDouble(values[1]);
                double rateB = Double.parseDouble(values[2]);
                String result = values[3];

                Match match = new Match(matchId, rateA, rateB, result);
                calculateResults(match, players);
            }
        }
    }

    private static void calculateResults(Match match, Map<String, Player> players) {
        for (Player player : players.values()) {
            player.calculateResult(match);
        }
    }

    private static void writeResults(String filePath, Map<String, Player> players) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
            Map<String, Player> legitimatePlayers = new TreeMap<>(players);
            for (Player player : legitimatePlayers.values()) {
                if (player.isLegitimate()) {
                    writer.write(player.toString());
                    writer.newLine();
                }
            }

            if (!legitimatePlayers.isEmpty()) {
                writer.newLine();
            }

            Map<String, Player> illegitimatePlayers = new TreeMap<>(players);
            for (Player player : illegitimatePlayers.values()) {
                if (!player.isLegitimate()) {
                    writer.write(player.getFirstIllegalOperation());
                    writer.newLine();
                }
            }

            long hostBalance = calculateHostBalance(players);
            writer.write("HOST_BALANCE " + hostBalance);
        }
    }

    private static long calculateHostBalance(Map<String, Player> players) {
        return players.values().stream()
                .filter(Player::isBetWinner)
                .mapToLong(Player::getBetResult)
                .sum();
    }
}
