package com.dahuaboke.signaling.model;

import com.alibaba.fastjson2.annotation.JSONField;

import java.util.*;

public class Room {

    private String roomId;

    private String ruuid;

    private Set<Person> personSet = new HashSet<>();

    @JSONField(serialize = false)
    private Process process;

    public Room(String roomId) {
        this.roomId = roomId;
        this.ruuid = UUID.randomUUID().toString().replace("-","");
    }

    public String getRoomId() {
        return roomId;
    }

    public void setRoomId(String roomId) {
        this.roomId = roomId;
    }

    public Set<Person> getPersonSet() {
        return personSet;
    }

    public void setPersonSet(Set<Person> personSet) {
        this.personSet = personSet;
    }

    public String getRuuid() {
        return ruuid;
    }

    public void setRuuid(String ruuid) {
        this.ruuid = ruuid;
    }

    public Process getProcess() {
        return process;
    }

    public void setProcess(Process process) {
        this.process = process;
    }
}
