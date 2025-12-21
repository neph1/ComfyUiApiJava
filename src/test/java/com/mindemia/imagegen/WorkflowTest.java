/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/UnitTests/JUnit5TestClass.java to edit this template
 */
package com.mindemia.imagegen;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.net.URL;
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
        
    }
    
}
