package org.example;

public class Match {
    private final String matchId;
    private final double returnRateA;
    private final double returnRateB;
    private final String result;
    private String betSide;

    public Match(String matchId, double returnRateA, double returnRateB, String result) {
        this.matchId = matchId;
        this.returnRateA = returnRateA;
        this.returnRateB = returnRateB;
        this.result = result;
    }

    public String getResult() {
        return result;
    }

    public String getBetSide() {
        return betSide;
    }

    public double getReturnRate() {
        return (betSide != null && betSide.equals("A")) ? returnRateA : returnRateB;
    }
}