/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mindemia.imagegen.test;

import com.mindemia.imagegen.ComfyUi;
import com.mindemia.imagegen.Workflow;
import com.mindemia.imagegen.WorkflowHelper;
import com.mindemia.imagegen.response.ResultType;
import com.mindemia.imagegen.workpackage.BaseWorkPackage;
import java.io.File;

/**
 *
 * @author rickard
 */
public class TestWorkPackage {

    public static void main(String[] args) {
        TestWorkPackage test = new TestWorkPackage();
    }

    public TestWorkPackage() {
        ComfyUi comfyUi = new ComfyUi("localhost", 8188);
        Workflow workflow = comfyUi.loadWorkflow("default_comfy_workflow.json");
        
        BaseWorkPackage workPackage = new BaseWorkPackage.Builder()
        .comfyUi(comfyUi)
        .workflow(workflow)
        .addOutput("test.png", ResultType.image)
                
        .onSetup(wf -> {
            WorkflowHelper workflowHelper = new WorkflowHelper(workflow);

            workflowHelper.setOutputSize(5, 512, 384);
            workflowHelper.setTextPrompt(6, "giant sand castle, sunny, beach");
            workflowHelper.setTextPrompt(7, "text, watermark");
        })
        .onPostDownload(() -> {
            assert(new File("test.png").exists());
        })
        .build();
        
        workPackage.execute();
        
    }

}
