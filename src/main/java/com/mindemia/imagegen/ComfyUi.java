package com.mindemia.imagegen;

import com.mindemia.imagegen.response.HistoryResponse;
import com.mindemia.imagegen.response.ResultFile;
import com.mindemia.imagegen.response.QueueResponse;
import com.mindemia.imagegen.response.Response;
import com.mindemia.imagegen.response.Output;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mindemia.imagegen.response.HistoryItem;
import com.mindemia.imagegen.response.ResultType;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
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
    private final Map<Integer, List<Integer>> bypassedNodes = new HashMap<>();
    
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

            if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
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


    public String uploadImage(String inputImage) throws IOException {
        String boundary = Long.toHexString(System.currentTimeMillis());
        String CRLF = "\r\n";
        HttpURLConnection conn = (HttpURLConnection) URI.create(this.url + uploadImageEndpoint).toURL().openConnection();
            conn.setRequestMethod("POST");
            conn.setDoOutput(true);
        conn.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary);

        File file = new File(inputImage);
        String fileName = file.getName();
        String mimeType = URLConnection.guessContentTypeFromName(fileName);
        if (mimeType == null) mimeType = "application/octet-stream";

        try (OutputStream output = conn.getOutputStream();
             PrintWriter writer = new PrintWriter(new OutputStreamWriter(output, StandardCharsets.UTF_8), true)) {

            writer.append("--").append(boundary).append(CRLF);
            writer.append("Content-Disposition: form-data; name=\"type\"").append(CRLF);
            writer.append(CRLF);
            writer.append("input").append(CRLF);

            writer.append("--").append(boundary).append(CRLF);
            writer.append("Content-Disposition: form-data; name=\"image\"; filename=\"").append(fileName).append("\"").append(CRLF);
            writer.append("Content-Type: ").append(mimeType).append(CRLF);
            writer.append(CRLF);
            writer.flush();

            Files.copy(file.toPath(), output);
            output.flush();

            writer.append(CRLF);
            writer.append("--").append(boundary).append("--").append(CRLF);
            writer.flush();
        }

        int responseCode = conn.getResponseCode();
        if (responseCode == 200) {
            Map<String, String> response = objectMapper.readValue(conn.getInputStream(), Map.class);
            return response.get("name");
        } else {
            logger.warn("Error when uploading image",responseCode);
            return null;
        }
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

    public HistoryItem getHistory(String promptId) {
        try {
            HttpURLConnection connection = (HttpURLConnection) URI.create(this.url + historyEndpoint).toURL().openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("Content-Type", "application/json");

            if (connection.getResponseCode() == 200) {
                HistoryResponse history = objectMapper.readValue(connection.getInputStream(), HistoryResponse.class);
                final HistoryItem historyItem = history.getItem(promptId);
                if(historyItem == null) {
                    logger.warn("Error when getting history for {}", promptId);
                    return null;
                }
                return historyItem;
            }
        } catch (IOException e) {
            logger.warn("Error when getting history.", e);
        }
        return null;
    }
    
    public byte[] downloadResult(HistoryItem historyItem, ResultType type) {
        Collection<Output> outputs = historyItem.outputs().values();
        for (Output output : outputs) {
            if (type == null || type == ResultType.image && output.images() != null) {
                for (ResultFile image : output.images()) {
                    return downloadFile(image.filename(), image.subfolder(), image.type());
                }

            }
            if (type == null || type == ResultType.gif && output.gifs() != null) {
                for(ResultFile gif: output.gifs()) {
                    return downloadFile(gif.filename(), gif.subfolder(), gif.type());
                }
            }
            if (type == null || type == ResultType.latent && output.latents() != null) {
                for(ResultFile latent: output.latents()) {
                    return downloadFile(latent.filename(), latent.subfolder(), latent.type());
                }
            }
        }
        return null;
    }

    public void setEnabled(int nodeId) {
//        Node node = workflow.getNodeById(nodeId);
//        
//        List<Object> inputs = node.getInputs();
    }
    
    
    private byte[] downloadFile(String filename, String subfolder, String folderType) {
        try {
            HttpURLConnection connection = (HttpURLConnection) URI.create(this.url + "/view?filename=" + filename + "&subfolder=" + subfolder + "&type=" + folderType).toURL().openConnection();
            connection.setRequestMethod("GET");
            if (filename.endsWith(".png")) {
                connection.setRequestProperty("Content-Type", "image/png");
            } else if (filename.endsWith(".mp4")) {
                connection.setRequestProperty("Content-Type", "video/mp4");
            } else if (filename.endsWith(".latent")) {
                connection.setRequestProperty("Content-Type", "application/octet-stream");
            } else {
                logger.warn("Unknown type.");
            }
            

            if (connection.getResponseCode() == 200) {
                return connection.getInputStream().readAllBytes();
            }
        } catch (IOException e) {
            logger.warn("Error when getting image.", e);
        }
        return null;
    }

}
