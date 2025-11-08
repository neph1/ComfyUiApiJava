package com.mindemia.imagegen;

import com.mindemia.imagegen.response.HistoryResponse;
import com.mindemia.imagegen.response.ResulFile;
import com.mindemia.imagegen.response.QueueResponse;
import com.mindemia.imagegen.response.Response;
import com.mindemia.imagegen.response.Output;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.nio.file.Files;
import java.util.*;
import java.util.logging.Level;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ComfyUi {

    private final ObjectMapper objectMapper = new ObjectMapper();
    
    private final String url;
    private final String generateEndpoint = "/prompt";
    private final String uploadImageEndpoint = "/upload/image";
    private final String queueEndpoint = "/queue";
    private final String historyEndpoint = "/history";
    
    private final Logger logger = LoggerFactory.getLogger(ComfyUi.class);
    
    private Workflow workflow;

    public ComfyUi(String address, int port) {
        this.url = String.format("http://%s:%s", address, port);
    }

    public String generate(int numberBatches) {
        return sendRequest(numberBatches);
    }

    public String sendRequest(int numberBatches) {
        try {

            Map<String, Workflow> payload = new HashMap<>();
            payload.put("prompt", workflow);

            HttpURLConnection connection = (HttpURLConnection) URI.create(this.url + this.generateEndpoint).toURL().openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setDoOutput(true);

            try (OutputStream os = connection.getOutputStream()) {
                os.write(objectMapper.writeValueAsBytes(payload));
                os.flush();
            }

            if (connection.getResponseCode() != 200) {
                logger.warn("Error", connection.getResponseCode());
                return null;
            }

            Response response = objectMapper.readValue(connection.getInputStream(), Response.class);
            return response.getPromptId();

            
        } catch (IOException e) {
            logger.warn("Error in image generation. ", e);
            return null;
        }
    }


    public String uploadImage(String inputImage) {
        try {
            File file = new File(inputImage);
            HttpURLConnection connection = (HttpURLConnection) URI.create(this.url + uploadImageEndpoint).toURL().openConnection();
            connection.setRequestMethod("POST");
            connection.setDoOutput(true);

            try (OutputStream os = connection.getOutputStream()) {
                os.write(Files.readAllBytes(file.toPath()));
                os.flush();
            }

            if (connection.getResponseCode() == 200) {
                Map<String, String> response = objectMapper.readValue(connection.getInputStream(), Map.class);
                return response.get("name");
            } else {
                logger.warn("Error when uploading image", connection.getResponseCode());
                return null;
            }
        } catch (IOException e) {
            logger.warn("Error when uploading image", e);
            return null;
        }
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
    
    public Workflow loadWorkflow(String workflowPath) {
        try {
            URL source = ClassLoader.getSystemClassLoader().getResource(workflowPath);
            this.workflow = objectMapper.readValue(source, Workflow.class);
        } catch (IOException ex) {
            java.util.logging.Logger.getLogger(ComfyUi.class.getName()).log(Level.SEVERE, null, ex);
        }

        return this.workflow;
    }
    
    public boolean pollQueue(String promptId) {
        logger.info("Polling comfy ", promptId);
        try {
            HttpURLConnection connection = (HttpURLConnection) URI.create(this.url + queueEndpoint).toURL().openConnection();
            connection.setRequestMethod("GET");

            if (connection.getResponseCode() == 200) {
                QueueResponse response = objectMapper.readValue(connection.getInputStream(), QueueResponse.class);
                List<List<String>> queuePending = response.getQueuePending();
                List<List<String>> queueRunning = response.getQueueRunning();

                for (List<String> item : queuePending) {
                    if (item.get(1).equals(promptId)) {
                        return false;
                    }
                }

                for (List<String> item : queueRunning) {
                    if (item.get(1).equals(promptId)) {
                        return false;
                    }
                }
            }
        } catch (IOException e) {
            logger.warn("Error when polling queue image.", e);
            return false;
        }
        return true;
    }

    public byte[] getHistory(String promptId) {
        try {
            HttpURLConnection connection = (HttpURLConnection) URI.create(this.url + historyEndpoint).toURL().openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("Content-Type", "application/json");

            if (connection.getResponseCode() == 200) {
                HistoryResponse history = objectMapper.readValue(connection.getInputStream(), HistoryResponse.class);

                Collection<Output> outputs = history.getItem(promptId).outputs().values();
                for (Output output : outputs) {
                    if (output.getImages() != null) {
                        for (ResulFile image : output.getImages()) {
                            return downloadFile(image.getFilename(), image.getSubfolder(), image.getType());
                        }
                    }
                }
            }
        } catch (IOException e) {
            logger.warn("Error when getting history.", e);
        }
        return null;
    }

    private byte[] downloadFile(String filename, String subfolder, String folderType) {
        try {
            HttpURLConnection connection = (HttpURLConnection) URI.create(this.url + "/view?filename=" + filename + "&subfolder=" + subfolder + "&type=" + folderType).toURL().openConnection();
            connection.setRequestMethod("GET");
            if (filename.endsWith(".png")) {
                connection.setRequestProperty("Content-Type", "image/png");
            } else if (filename.endsWith(".mp4")) {
                connection.setRequestProperty("Content-Type", "video/mp4");
            }
            

            if (connection.getResponseCode() == 200) {
                return connection.getInputStream().readAllBytes();
            }
        } catch (IOException e) {
            logger.warn("Error when getting image.", e);
        }
        return null;
    }

    public void setModel(int nodeId, String model) {
        workflow.getNodeById(nodeId).setInput("ckpt_name", model);
    }
    
    public void setLora(int nodeId, String lora) {
        setLora(nodeId, lora, 1f, 1f);
    }
    
    public void setLora(int nodeId, String lora, float strength, float clipStrength) {
        Node loraNode = workflow.getNodeById(nodeId);
        loraNode.setInput("lora_name", lora);
        loraNode.setInput("strength_model", strength);
        loraNode.setInput("strength_clip", clipStrength);
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

}
