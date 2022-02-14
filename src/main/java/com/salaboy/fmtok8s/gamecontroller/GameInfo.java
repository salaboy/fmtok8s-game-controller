package com.salaboy.fmtok8s.gamecontroller;

import com.fasterxml.jackson.annotation.JsonClassDescription;

@JsonClassDescription("GameInfo")
public class GameInfo {
    private String sessionId;
    private int levelId;

    public GameInfo() {
    }

    public GameInfo(String sessionId) {
        this.sessionId = sessionId;
        this.levelId = 1;
    }

    public GameInfo(String sessionId, int levelId) {
        this.sessionId = sessionId;
        this.levelId = levelId;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public int getLevelId() {
        return levelId;
    }

    public void setLevelId(int levelId) {
        this.levelId = levelId;
    }
}
