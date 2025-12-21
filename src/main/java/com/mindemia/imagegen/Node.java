/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mindemia.imagegen;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import java.util.Map;

/**
 *
 * @author rickard
 */

@JsonIgnoreProperties(ignoreUnknown = true)
public class Node {
    
    private Map<String, Object> inputs;
    @JsonIgnore
    private int id;
    
    @JsonProperty("class_type")
    private String classType;
    
    @JsonProperty("_meta")
    private Map<String, String> meta;
    
    public Node() {
        
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getClassType() {
        return classType;
    }

    public void setClassType(String type) {
        this.classType = type;
    }
    
    public String getTitle() {
        if(meta != null) {
            return meta.getOrDefault("title", classType);
        }
        return classType;
    }
    
    @JsonProperty("inputs")
    public Map<String, Object> getInputs() {
        return inputs;
    }

    public void setInputs(Map<String, Object> inputs) {
        this.inputs = inputs;
    }
    
    public Object getInput(String name) {
        return inputs.get(name);
    }
    
    public void setInput(String key, Object value) {
        this.inputs.put(key, value);
    }

}
