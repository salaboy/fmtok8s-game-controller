package com.salaboy.fmtok8s.gamecontroller;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class Leaderboard {
    @JsonProperty("Sessions")
    private List<SessionScore> sessionScores;


    public List<SessionScore> getSessionScores() {
        return sessionScores;
    }

    public void setSessionScores(List<SessionScore> sessionScores) {
        this.sessionScores = sessionScores;
    }

    @Override
    public String toString() {
        return "Leaderboard{" +
                "sessionScores=" + sessionScores +
                '}';
    }
}
