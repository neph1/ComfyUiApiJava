package com.mindemia.imagegen.response;


import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;


public class QueueResponse {
    @JsonProperty("queue_pending")
    private List<List<String>> queuePending;
    @JsonProperty("queue_running")
    private List<List<String>> queueRunning;

    // Getters and Setters
    public List<List<String>> getQueuePending() {
        return queuePending;
    }

    public void setQueuePending(List<List<String>> queuePending) {
        this.queuePending = queuePending;
    }

    public List<List<String>> getQueueRunning() {
        return queueRunning;
    }

    public void setQueueRunning(List<List<String>> queueRunning) {
        this.queueRunning = queueRunning;
    }
}
