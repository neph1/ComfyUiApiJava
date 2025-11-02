package com.mindemia.imagegen.response;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import java.util.HashMap;
import java.util.Map;


public class HistoryResponse {
    
    private Map<String, HistoryItem> items;

    @JsonAnyGetter
    public Map<String, HistoryItem> getOutputs() {
        return items;
    }
    
    public HistoryItem getItem(String key) {
        return this.items.get(key);
    }
    
    @JsonAnySetter
    public void setItems(Map<String, HistoryItem> outputs) {
        this.items = outputs;
    }
    
    @JsonAnySetter
    public void addItem(String key, HistoryItem output) {
        if(items == null) {
            items = new HashMap<>();
        }
        this.items.put(key, output);
    }
}
