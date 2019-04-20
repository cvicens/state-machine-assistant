package com.redhat.his.service;

import java.util.Map;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.apache.kafka.common.serialization.Deserializer;

public class EventDeserializer implements Deserializer<Event> {
    @Override public void close() {
    }
    @Override public void configure(Map arg0, boolean arg1) {
    }
    @Override
    public Event deserialize(String arg0, byte[] arg1) {
      ObjectMapper mapper = new ObjectMapper();
      Event event = null;
      try {
        event = mapper.readValue(arg1, Event.class);
      } catch (Exception e) {
        e.printStackTrace();
      }
      return event;
    }
  }