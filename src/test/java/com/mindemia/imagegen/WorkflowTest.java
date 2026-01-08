/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/UnitTests/JUnit5TestClass.java to edit this template
 */
package com.mindemia.imagegen;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 *
 * @author rickard
 */
public class WorkflowTest {
    
    private final ObjectMapper objectMapper;
    
    public WorkflowTest() {
        objectMapper = new ObjectMapper();
    }
    
    @Test
    public void testLoadWorkflow() {
        URL source = ClassLoader.getSystemClassLoader().getResource("default_comfy_workflow.json");
        Workflow workflow = null;
        try {
            workflow = objectMapper.readValue(source, Workflow.class);
        } catch (IOException ex) {
            Logger.getLogger(WorkflowTest.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        assertNotNull(workflow);
        assertEquals(7, workflow.getNodes().size());
        assertEquals("CLIPTextEncode", workflow.getNodeById(6).getClassType());
        assertEquals(3, workflow.getNodeByType("KSampler").getId());
        assertEquals(4, workflow.getNodeByTitle("Load Checkpoint").getId());
        
        workflow.disconnectNode(workflow.getNodeById(4));
        
        assertEquals(1, workflow.getNodeByType("KSampler").getInputs().size());
    }
    
    @Test
    public void testDisconnectNode() {
        Workflow workflow = new Workflow();
        Node node1 = new Node();
        node1.setId(1);
        Node node2 = new Node();
        node2.setId(2);
        Map<String, Object> inputs = new HashMap<>();
        inputs.put("input1", List.of("1", 0));

        node2.setInputs(inputs);

        workflow.addNode("1", node1);
        workflow.addNode("2", node2);

        workflow.disconnectNode(node1);

        assertFalse(node2.getInputs().containsValue(List.of("1", 0)));
    }
    
}
