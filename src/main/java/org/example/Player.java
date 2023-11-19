package org.example;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class Player {
    private final String playerId;
    private long balance;
    private int betsPlaced;
    private int betsWon;
    private String firstIllegalOperation;

    public Player(String playerId) {
        this.playerId = playerId;
    }

    public void deposit(int coins) {
        balance += coins;
    }

    public void bet(String matchId, int coins, String side) {
        if (coins > balance) {
            firstIllegalOperation = playerId + " BET " + matchId + " " + coins + " " + side;
        } else {
            betsPlaced++;
            balance -= coins;
        }
    }

    public void withdraw(int coins) {
        if (coins > balance) {
            firstIllegalOperation = playerId + " WITHDRAW " + coins;
        } else {
            balance -= coins;
        }
    }

    public void calculateResult(Match match) {
        if (firstIllegalOperation == null) {
            if (match.getResult().equals("Draw")) {
                balance += match.getReturnRate() * betsPlaced;
            } else if (match.getResult().equals(match.getBetSide())) {
                balance += match.getReturnRate() * betsPlaced;
                betsWon++;
            }
        }
    }

    public boolean isLegitimate() {
        return firstIllegalOperation == null;
    }

    public boolean isBetWinner() {
        return betsWon > 0;
    }

    public long getBetResult() {
        return balance;
    }

    public String getFirstIllegalOperation() {
        return firstIllegalOperation;
    }

    @Override
    public String toString() {
        BigDecimal winRate = (betsPlaced == 0) ? BigDecimal.ZERO : BigDecimal.valueOf(betsWon)
                .divide(BigDecimal.valueOf(betsPlaced), 2, RoundingMode.HALF_UP);
        return playerId + " " + balance + " " + winRate;
    }
}