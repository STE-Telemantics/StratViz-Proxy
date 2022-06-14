package com.ste.kafka;

import org.json.simple.JSONObject;

public class JSONRecord {

    JSONObject object;

    public JSONRecord() {
    }

    public JSONRecord(JSONObject jobj) {
        object = jobj;
    }

    public String toString() {
        return object.toJSONString();
    }
}
