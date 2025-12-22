package com.mindemia.imagegen.workpackage;

import com.mindemia.imagegen.ComfyUi;
import com.mindemia.imagegen.Workflow;
import com.mindemia.imagegen.response.HistoryItem;
import com.mindemia.imagegen.response.ResultType;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

public abstract class BaseWorkPackage {
    private final ComfyUi comfyUi;
    private final Workflow workflow;
    private final String inputFile;
    private final String outputFile;
    private final ResultType resultType;
    
    private final Logger logger = Logger.getLogger(BaseWorkPackage.class.getName());

    public BaseWorkPackage(ComfyUi comfyUi, Workflow workflow, String inputImage, String outputFile, ResultType type) {
        this.comfyUi = comfyUi;
        this.workflow = workflow;
        this.inputFile = inputImage;
        this.outputFile = outputFile;
        this.resultType = type;
    }

    public void execute() {
        if(inputFile != null) {
            uploadFile(inputFile);
        }
        prePrompt();
        String promptId = prompt();
        download(promptId, outputFile);
    }
    
    protected abstract void prePrompt();
    
    protected abstract void postDownload();

    protected final void uploadFile(String fileName) {
        try {
            comfyUi.uploadImage(new File(System.getProperty("user.dir") + "/" + fileName).getPath());
        } catch (IOException ex) {
            logger.log(Level.SEVERE, null, ex);
        }
    }

    protected final String prompt() {
        String promptId = comfyUi.sendRequest(0);

        while (!comfyUi.pollQueue(promptId)) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException ex) {
                logger.log(Level.SEVERE, null, ex);
            }
        }
        return promptId;
    }

    protected final void download(String promptId, String fileName) {
        HistoryItem history = comfyUi.getHistory(promptId);
        byte[] data = comfyUi.downloadResult(history, resultType);
        if (data == null) {
            logger.log(Level.SEVERE, "Failed to find image data");
            return;
        }
        try (FileOutputStream fos = new FileOutputStream(System.getProperty("user.dir") + "/" + fileName)) {
            fos.write(data);
        } catch (IOException ex) {
            logger.log(Level.SEVERE, null, ex);
        }
    }
    
    protected Workflow getWorkflow() {
        return workflow;
    }
    
    protected String getInputFile() {
        return inputFile;
    }

}
