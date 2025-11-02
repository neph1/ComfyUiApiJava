package com.mindemia.imagegen;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Workflow {
    
    @JsonProperty
    private Map<String, Node> nodes;

    public Workflow() {
        this.nodes = new HashMap<>();
    }

    public Node getNodeById(int id) {
        return nodes.get(""+id);
    }
    
    /**
     * Returns *first* node of type
     * @param type
     * @return node
     */
    public Node getNodeByType(String type) {
        for(Node node: nodes.values()) {
            if(node.getClassType().equals(type)) {
                return node;
            }
        }
        return null;
    }
        
    @JsonAnySetter
    public void addNode(String key, Node value) {
        value.setId(Integer.valueOf(key));
        nodes.put(key, value);
    }

    
    
    @JsonAnyGetter
    public Map<String, Node> getNodes() {
        return nodes;
    }

    @JsonAnySetter
    public void setNodes(Map<String, Node> nodes) {
        this.nodes = nodes;
    }
}
