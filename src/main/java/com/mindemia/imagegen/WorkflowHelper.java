/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mindemia.imagegen;

/**
 *
 * @author rickard
 */
public class WorkflowHelper {
    
    private final Workflow workflow;

    public WorkflowHelper(Workflow workflow) {
        this.workflow = workflow;
    }

    public void setModel(int nodeId, String model) {
        workflow.getNodeById(nodeId).setInput("ckpt_name", model);
    }

    public void setKsampler(int nodeId, long seed, int steps, float cfg, String sampler, String scheduler, float denoise) {
        Node ksampler = workflow.getNodeById(nodeId);
        ksampler.setInput("seed", seed);
        ksampler.setInput("steps", steps);
        ksampler.setInput("cfg", cfg);
        ksampler.setInput("sampler_name", sampler);
        ksampler.setInput("scheduler", scheduler);
        ksampler.setInput("denoise", denoise);
    }

    public void setLora(int nodeId, String lora, float strength, float clipStrength) {
        Node loraNode = workflow.getNodeById(nodeId);
        loraNode.setInput("lora_name", lora);
        loraNode.setInput("strength_model", strength);
        loraNode.setInput("strength_clip", clipStrength);
    }

    public void setLora(int nodeId, String lora) {
        setLora(nodeId, lora, 1.0F, 1.0F);
    }

    /**
     * Use this to set values on any node.
     *
     * @param nodeId
     * @param key
     * @param value
     */
    public void setInput(int nodeId, String key, Object value) {
        workflow.getNodeById(nodeId).setInput(key, value);
    }

    public void setTextPrompt(int nodeId, String prompt) {
        workflow.getNodeById(nodeId).setInput("text", prompt);
    }

    public void setOutputSize(int nodeId, int width, int height) {
        workflow.getNodeById(nodeId).setInput("width", width);
        workflow.getNodeById(nodeId).setInput("height", height);
    }

    public void setImage(int nodeId, String imageName) {
        workflow.getNodeById(nodeId).setInput("image", imageName);
    }
    
}
