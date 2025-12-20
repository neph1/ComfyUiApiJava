/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mindemia.imagegen.test;

import com.mindemia.imagegen.ComfyUi;
import com.mindemia.imagegen.Workflow;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author rickard
 */
public class TestUploadImage {

    public static void main(String[] args) {
        TestUploadImage test = new TestUploadImage();
    }

    public TestUploadImage() {
        ComfyUi comfyUi = new ComfyUi("localhost", 8188);
        Workflow workflow = comfyUi.loadWorkflow("i2v_video_workflow.json");
        
        String imageName = null;
        try {
            imageName = comfyUi.uploadImage(ClassLoader.getSystemClassLoader().getResource("cyberpunk.jpg").getFile());
        } catch (IOException ex) {
            Logger.getLogger(TestUploadImage.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        assert(imageName != null);
        
    }

}
