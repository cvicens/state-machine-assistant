package com.redhat.his.service;

import java.util.Map;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.apache.kafka.common.serialization.Serializer;

public class EventSerializer implements Serializer<Event> {
    @Override public void configure(Map map, boolean b) {
    }
    @Override public byte[] serialize(String arg0, Event arg1) {
      byte[] retVal = null;
      ObjectMapper objectMapper = new ObjectMapper();
      try {
        retVal = objectMapper.writeValueAsString(arg1).getBytes();
      } catch (Exception e) {
        e.printStackTrace();
      }
      return retVal;
    }
    @Override public void close() {
    }
  }