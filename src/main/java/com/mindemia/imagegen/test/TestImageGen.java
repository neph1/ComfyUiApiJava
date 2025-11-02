/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mindemia.imagegen.test;

import com.mindemia.imagegen.ComfyUi;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author rickard
 */
public class TestImageGen {

    public static void main(String[] args) {
        TestImageGen test = new TestImageGen();
    }

    public TestImageGen() {
        ComfyUi comfyUi = new ComfyUi("localhost", 8189);
        comfyUi.loadWorkflow("default_comfy_workflow.json");
        
        comfyUi.setOutputSize(5, 512, 384);
        comfyUi.setTextPrompt(6, "giant sand castle, sunny, beach");
        comfyUi.setTextPrompt(7, "text, watermark");

        String promptId = comfyUi.sendRequest(0);

        while (!comfyUi.pollQueue(promptId)) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException ex) {
                Logger.getLogger(TestImageGen.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        byte[] imageData = comfyUi.getHistory(promptId);
        if (imageData == null) {
            return;
        }
        //String imageBytes = Base64.getEncoder().encodeToString(imageData);

        try (FileOutputStream fos = new FileOutputStream(System.getProperty("user.dir") + "/test.png")) {
           fos.write(imageData);
        } catch (IOException ex) {
            Logger.getLogger(TestImageGen.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

}
