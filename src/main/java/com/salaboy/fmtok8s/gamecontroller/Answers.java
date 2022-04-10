package com.salaboy.fmtok8s.gamecontroller;

public class Answers {
    private String question1;
    private String question2;
    private String question3;
    private String sessionId;
    private String nickname;

    public Answers() {
    }


    public String getQuestion1() {
        return question1;
    }

    public void setQuestion1(String question1) {
        this.question1 = question1;
    }

    public String getQuestion2() {
        return question2;
    }

    public void setQuestion2(String question2) {
        this.question2 = question2;
    }

    public String getQuestion3() {
        return question3;
    }

    public void setQuestion3(String question3) {
        this.question3 = question3;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    @Override
    public String toString() {
        return "Answers{" +
                "question1='" + question1 + '\'' +
                ", question2='" + question2 + '\'' +
                ", question3='" + question3 + '\'' +
                ", sessionId='" + sessionId + '\'' +
                ", nickname='" + nickname + '\'' +
                '}';
    }
}
