package com.mindemia.imagegen;

import com.mindemia.imagegen.Node;
import com.mindemia.imagegen.ComfyUi;
import com.mindemia.imagegen.Workflow;
import java.util.List;
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
        comfyUi.setTextPrompt(nodeId, prompt);

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
        comfyUi.setOutputSize(nodeId, width, height);

        // Retrieve the node and check if the size is set correctly
        Node node = workflow.getNodeById(nodeId);
        Map<String, Object> inputs = node.getInputs();

        assertNotNull(inputs);
        assertEquals(width, inputs.get("width"));
        assertEquals(height, inputs.get("height"));
    }

    // TODO: removed load image node from workflow
//    @Test
//    public void testSetImage() {
//        int nodeId = 10;
//        String imageName = "testImage.png";
//
//        // Set the image
//        comfyUi.setImage(nodeId, imageName);
//
//        // Retrieve the node and check if the image is set correctly
//        Node node = workflow.getNodeById(nodeId);
//        Map<String, Object> inputs = node.getInputs();
//
//        assertNotNull(inputs);
//        assertEquals(imageName, inputs.get("image"));
//    }

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
}
