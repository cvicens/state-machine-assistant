/*
 * Copyright 2016-2017 Red Hat, Inc, and individual contributors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.redhat.his.service;

// tag::snippet-greeting[]
public class Event {
    private String type;
    private String trigger;
    private String timestamp;

    public Event() {
        this.type = null;
        this.trigger = null;
        this.timestamp = null;
    }

    public Event(String type, String trigger, String timestamp) {
        this.type = type;
        this.trigger = trigger;
        this.timestamp = timestamp;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setTrigger(String trigger) {
        this.trigger = trigger;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getType() {
        return type;
    }

    public String getTrigger() {
        return trigger;
    }

    public String getTimestamp() {
        return timestamp;
    }
}
// end::snippet-greeting[]
