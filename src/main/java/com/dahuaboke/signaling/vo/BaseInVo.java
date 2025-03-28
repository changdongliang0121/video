package com.dahuaboke.signaling.vo;

import com.dahuaboke.signaling.model.Person;

public class BaseInVo {

    private String type;

    private String userId;

    private String roomId;

    private Person person;

    private String question;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getRoomId() {
        return roomId;
    }

    public void setRoomId(String roomId) {
        this.roomId = roomId;
    }

    public Person getPerson() {
        return person;
    }

    public void setPerson(Person person) {
        this.person = person;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    @Override
    public String toString() {
        return "BaseInVo{" +
                "type='" + type + '\'' +
                ", userId='" + userId + '\'' +
                ", roomId='" + roomId + '\'' +
                ", person=" + person +
                ", question='" + question + '\'' +
                '}';
    }
}
