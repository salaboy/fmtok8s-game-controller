package com.salaboy.fmtok8s.gamecontroller;

import com.fasterxml.jackson.annotation.*;

import java.util.Date;

@JsonClassDescription("GameInfo")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class GameInfo {
    @JsonProperty("gameSessionId")
    private String sessionId;
    @JsonProperty("levelId")
    private int currentLevelId = 0;
    private int nextLevelId = 0;
    private String currentAnswer;
    @JsonProperty("started")
    private boolean currentLevelStarted = false;
    private Date startedDate;
    @JsonProperty("completed")
    private boolean currentLevelCompleted = false;
    private Date completedDate;

    public GameInfo() {
    }

    public GameInfo(String sessionId) {
        this.sessionId = sessionId;
        this.currentLevelId = 1;
    }

    public GameInfo(String sessionId, int levelId) {
        this.sessionId = sessionId;
        this.currentLevelId = levelId;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public int getCurrentLevelId() {
        return currentLevelId;
    }

    public void setCurrentLevelId(int currentLevelId) {
        this.currentLevelId = currentLevelId;
    }

    public String getCurrentAnswer() {
        return currentAnswer;
    }

    public void setCurrentAnswer(String currentAnswer) {
        this.currentAnswer = currentAnswer;
    }

    public boolean isCurrentLevelCompleted() {
        return currentLevelCompleted;
    }

    public void setCurrentLevelCompleted(boolean currentLevelCompleted) {
        this.currentLevelCompleted = currentLevelCompleted;
    }

    public boolean isCurrentLevelStarted() {
        return currentLevelStarted;
    }

    public void setCurrentLevelStarted(boolean currentLevelStarted) {
        this.currentLevelStarted = currentLevelStarted;
    }

    public Date getStartedDate() {
        return startedDate;
    }

    public void setStartedDate(Date startedDate) {
        this.startedDate = startedDate;
    }

    public Date getCompletedDate() {
        return completedDate;
    }

    public void setCompletedDate(Date completedDate) {
        this.completedDate = completedDate;
    }

    public int getNextLevelId() {
        return nextLevelId;
    }

    public void setNextLevelId(int nextLevelId) {
        this.nextLevelId = nextLevelId;
    }
}
