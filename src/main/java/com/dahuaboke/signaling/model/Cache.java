package com.dahuaboke.signaling.model;

import com.alibaba.fastjson2.JSONObject;
import com.dahuaboke.signaling.util.VideoUtils;
import com.dahuaboke.signaling.util.WriteUtil;
import com.dahuaboke.signaling.vo.Result;
import io.netty.channel.Channel;

import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Cache {

    //全部房间
    public static Map<String, Room> rooms = new ConcurrentHashMap();

    //全部人
    public static Map<Channel, Person> persons = new ConcurrentHashMap();


    public static void pushRoomMessage(Person person, boolean flag) {
        Room room = rooms.get(person.getRoomId());
        Result pushRoomMessage = Result.success("roomMessage", JSONObject.toJSONString(room));
        String s = JSONObject.toJSONString(pushRoomMessage);
        room.getPersonSet().forEach(p -> {
            if (p != person || flag) {
                WriteUtil.write(p.getChannel(), s);
            }
        });
    }

    public static void pushRoomMessage(Person person, String type, boolean flag) {
        Room room = rooms.get(person.getRoomId());
        Result pushRoomMessage = Result.success(type, JSONObject.toJSONString(room));
        String s = JSONObject.toJSONString(pushRoomMessage);
        room.getPersonSet().forEach(p -> {
            if (p != person || flag) {
                WriteUtil.write(p.getChannel(), s);
            }
        });
    }

    public static void pushMessage(Person person, String message) {
        Room room = rooms.get(person.getRoomId());
        room.getPersonSet().forEach(p -> {
            WriteUtil.write(p.getChannel(), message);
        });
    }

    public static void clearRoom() {
        Iterator<Map.Entry<String, Room>> iterator = rooms.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, Room> entry = iterator.next();
            if (entry.getValue() == null || entry.getValue().getPersonSet().size() == 0) {
                String roomId = entry.getValue().getRoomId();
                iterator.remove();
                new Thread(() -> VideoUtils.AddWatermark(roomId)).start();
            }
        }
    }

    public static Room createOrEnterRoom(String roomId, Person person) {
        Room room = null;
        if (Cache.rooms.containsKey(roomId)) {
            room = Cache.rooms.get(roomId);
            synchronized (room) {
                room.getPersonSet().add(person);
            }
        } else {
            room = new Room(roomId);
            synchronized (room) {
                room.getPersonSet().add(person);
            }
            Cache.rooms.put(roomId, room);
        }
        person.setRoomId(room.getRoomId());
        return room;
    }

    public static void levelRoom(Person person) {
        rooms.forEach((k, v) -> {
            if (v != null) {
                v.getPersonSet().remove(person);
            }
        });
        clearRoom();
    }

    public static void removePerson(Person person) {
        levelRoom(person);
        persons.remove(person);
    }


}
