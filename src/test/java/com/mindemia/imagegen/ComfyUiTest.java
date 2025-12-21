package com.mindemia.imagegen;

import java.util.Map;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class ComfyUiTest {

    private ComfyUi comfyUi;
    private Workflow workflow;

    @BeforeEach
    public void setUp() {
        // Initialize ComfyUi with test parameters
        comfyUi = new ComfyUi("localhost", 8188);

        // Load a test workflow
        workflow = comfyUi.loadWorkflow("default_comfy_workflow.json");
    }

    @Test
    public void testSetTextPrompt() {
        int nodeId = 6;
        String prompt = "Test prompt";

        // Set the text prompt
        workflow.getNodeById(nodeId).setInput("text", prompt);

        // Retrieve the node and check if the prompt is set correctly
        Node node = workflow.getNodeById(nodeId);
        Map<String, Object> inputs = node.getInputs();

        assertEquals(prompt, inputs.get("text"));
    }

    @Test
    public void testSetOutputSize() {
        int nodeId = 5;
        int width = 512;
        int height = 512;

        // Set the output size
        workflow.getNodeById(nodeId).setInput("width", width);
        workflow.getNodeById(nodeId).setInput("height", height);

        // Retrieve the node and check if the size is set correctly
        Node node = workflow.getNodeById(nodeId);
        Map<String, Object> inputs = node.getInputs();

        assertNotNull(inputs);
        assertEquals(width, inputs.get("width"));
        assertEquals(height, inputs.get("height"));
    }

    @Test
    public void testLoadWorkflow() {
        String workflowPath = "default_comfy_workflow.json";

        // Load the workflow
        Workflow loadedWorkflow = comfyUi.loadWorkflow(workflowPath);

        // Check if the workflow is loaded correctly
        assertNotNull(loadedWorkflow);

        // Optionally, you can add more assertions to verify specific parts of the workflow
        // For example, check if a specific node exists
        Node node = loadedWorkflow.getNodeById(6);
        assertNotNull(node);
    }
    
        @Test
    public void testSetLora() {
        String workflowPath = "lora_workflow.json";

        Workflow loadedWorkflow = comfyUi.loadWorkflow(workflowPath);

        assertNotNull(loadedWorkflow);
        loadedWorkflow.getNodeById(11).setInput("lora_name", "test_lora");
    }
}
