/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mindemia.imagegen.test;

import com.mindemia.imagegen.ComfyUi;
import com.mindemia.imagegen.Node;
import com.mindemia.imagegen.Workflow;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author rickard
 */
public class TestVideoGen {

    public static void main(String[] args) {
        TestVideoGen test = new TestVideoGen();
    }

    public TestVideoGen() {
        ComfyUi comfyUi = new ComfyUi("localhost", 8188);
        Workflow workflow = comfyUi.loadWorkflow("i2v_video_workflow.json");
        
        Node encodeNode = workflow.getNodeByType("WanVideoImageToVideoEncode");
        encodeNode.setInput("num_frames", 33);
        
        Node clipEncode = workflow.getNodeByType("CLIPTextEncode");
        
        clipEncode.setInput("text", "a rainy cyberpunk city at night. neon signs blink. the camera flies into the picture, past the tall buildings.");
        
        comfyUi.uploadImage(ClassLoader.getSystemClassLoader().getResource("cyberpunk.jpg").getFile());
        
        workflow.getNodeByType("LoadImage").setInput("image", "cyberpunk.jpg");
        
        Node upscaleNode = workflow.getNodeByType("ImageScale");
        upscaleNode.setInput("width", 416);
        upscaleNode.setInput("height", 256);
        
        workflow.getNodeByType("WanVideoSampler").setInput("seed", Math.abs(new Random().nextInt()));
        
        String promptId = comfyUi.sendRequest(0);

        while (!comfyUi.pollQueue(promptId)) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException ex) {
                Logger.getLogger(TestVideoGen.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        byte[] imageData = comfyUi.getHistory(promptId);
        if (imageData == null) {
            Logger.getLogger(TestVideoGen.class.getName()).log(Level.SEVERE, "Failed to find image data");
            return;
        }
        try (FileOutputStream fos = new FileOutputStream(System.getProperty("user.dir") + "/test.mp4")) {
           fos.write(imageData);
        } catch (IOException ex) {
            Logger.getLogger(TestVideoGen.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

}
