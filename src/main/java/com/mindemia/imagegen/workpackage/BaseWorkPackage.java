package com.mindemia.imagegen.workpackage;

import com.mindemia.imagegen.ComfyUi;
import com.mindemia.imagegen.Node;
import com.mindemia.imagegen.Workflow;
import com.mindemia.imagegen.response.HistoryItem;
import com.mindemia.imagegen.response.ResultType;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;

public class BaseWorkPackage {

    private final ComfyUi comfyUi;
    private final Workflow workflow;
    private final List<String> inputFiles;
    private final Consumer<Workflow> setupAction;
    private final Runnable postDownloadAction;
    private final List<OutputSpec> outputSpecs;
    
    public record OutputSpec(String fileName, ResultType type) {}

    private final Logger logger = Logger.getLogger(BaseWorkPackage.class.getName());

    BaseWorkPackage(Builder builder) {
        this.comfyUi = builder.comfyUi;
        this.workflow = builder.workflow;
        this.inputFiles = builder.inputFiles;
        this.setupAction = builder.setupAction;
        this.postDownloadAction = builder.postDownloadAction;
        this.outputSpecs = builder.outputSpecs;
    }

    public void execute() {
        long startTime = System.currentTimeMillis();
        for(String inputFile: inputFiles){
            uploadFile(inputFile);
        }
        setup();
        String promptId = prompt();
        for (OutputSpec spec : outputSpecs) {
            download(promptId, spec.fileName(), spec.type());
        }
        postDownload();
        
        logger.log(Level.INFO, "Work package execution time: {0}", (System.currentTimeMillis() - startTime) / 1000);
    }

    public void setup() {
        if (setupAction != null) {
            setupAction.accept(workflow);
        }
    }
    
    public void postDownload() {
        if (postDownloadAction != null) {
            postDownloadAction.run();
        }
    }

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
                Thread.currentThread().interrupt(); // Good practice to restore interrupt flag
            }
        }
        return promptId;
    }

    protected final void download(String promptId, String fileName, ResultType type) {
        HistoryItem history = comfyUi.getHistory(promptId);
        if(history == null) {
            logger.log(Level.SEVERE, "Failed to find history for {0}", promptId);
            return;
        }
        byte[] data = comfyUi.downloadResult(history, type);
        if (data == null) {
            logger.log(Level.SEVERE, "Failed to find image data for {0}", fileName);
            return;
        }
        try (FileOutputStream fos = new FileOutputStream(System.getProperty("user.dir") + "/" + fileName)) {
            fos.write(data);
        } catch (IOException ex) {
            logger.log(Level.SEVERE, null, ex);
        }
    }

    public static class Builder {

        private ComfyUi comfyUi;
        private Workflow workflow;
        private List<String> inputFiles = new ArrayList<>();
        private Consumer<Workflow> setupAction;
        private Runnable postDownloadAction;
        private final List<OutputSpec> outputSpecs = new ArrayList<>();

        public Builder onSetup(Consumer<Workflow> action) {
            this.setupAction = action;
            return this;
        }
        
        public Builder onPostDownload(Runnable action) {
            this.postDownloadAction = action;
            return this;
        }

        public Builder comfyUi(ComfyUi comfyUi) {
            this.comfyUi = comfyUi;
            return this;
        }

        public Builder workflow(Workflow workflow) {
            this.workflow = workflow;
            return this;
        }

        public Builder addInput(String inputFile) {
            this.inputFiles.add(inputFile);
            return this;
        }

        public Builder addOutput(String fileName, ResultType type) {
            this.outputSpecs.add(new OutputSpec(fileName, type));
            return this;
        }
        
        public Builder setLora(int nodeId, String loraName) {
            this.workflow.getNodeById(nodeId).setInput("lora", loraName);
            return this;
        }
        
        /**
         * This will remove any forward nodes as it disconnects the node
         * 
         * @param nodeId
         * @return 
         */
        public Builder disableLora(int nodeId) {
            Collection<Node> nodes = this.workflow.getNodes().values();
            for(Node node: nodes) {
                Object input = node.getInput("lora");
                if(input != null && input.equals(nodeId)) {
                    node.setInput("lora", null);
                }
            }
            return this;
        }

        public BaseWorkPackage build() {
            return new BaseWorkPackage(this);
        }
    }
}
