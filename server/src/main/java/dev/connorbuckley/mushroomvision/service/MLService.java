package dev.connorbuckley.mushroomvision.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.connorbuckley.mushroomvision.dto.ClassificationResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;

@Service
public class MLService {

    @Value("${ml.python.path:python3}")
    private String pythonPath;

    @Value("${ml.predict.script.path}")
    private String predictScriptPath;

    @Value("${ml.model.path}")
    private String modelPath;

    @Value("${ml.process.timeout.ms:15000}")
    private long processTimeoutMs;

    private final ObjectMapper objectMapper = new ObjectMapper();

    public ClassificationResponse classifyImage(String imageData) {
        Process process = null;
        try {
            ProcessBuilder pb = new ProcessBuilder(pythonPath, predictScriptPath);
            pb.redirectErrorStream(true);
            process = pb.start();

            // Build JSON payload for stdin
            String payload = objectMapper.createObjectNode()
                    .put("image", imageData)
                    .put("model_path", modelPath)
                    .toString();

            try (BufferedWriter writer = new BufferedWriter(
                    new OutputStreamWriter(process.getOutputStream(), StandardCharsets.UTF_8))) {
                writer.write(payload);
                writer.flush();
            }

            boolean finished = process.waitFor(processTimeoutMs, java.util.concurrent.TimeUnit.MILLISECONDS);
            if (!finished) {
                process.destroyForcibly();
                return ClassificationResponse.builder()
                        .success(false)
                        .message("ML prediction timed out")
                        .build();
            }

            StringBuilder output = new StringBuilder();
            try (BufferedReader reader = new BufferedReader(
                    new InputStreamReader(process.getInputStream(), StandardCharsets.UTF_8))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    output.append(line);
                }
            }

            String json = output.toString().trim();

            if (json.isEmpty()) {
                return ClassificationResponse.builder()
                        .success(false)
                        .message("Empty response from Python predictor")
                        .build();
            }

            JsonNode root = objectMapper.readTree(json);
            if (!root.path("success").asBoolean(false)) {
                String error = root.path("error").asText("Unknown error from predictor");
                return ClassificationResponse.builder()
                        .success(false)
                        .message("Prediction failed: " + error)
                        .build();
            }

            JsonNode result = root.path("result");
            String prediction = result.path("prediction").asText();
            double confidence = result.path("confidence").asDouble();
            JsonNode probs = result.path("probabilities");
            double edible = probs.path("edible").asDouble();
            double poisonous = probs.path("poisonous").asDouble();

            ClassificationResponse.ClassificationResult.Probabilities probObj = ClassificationResponse.ClassificationResult.Probabilities
                    .builder()
                    .edible(edible)
                    .poisonous(poisonous)
                    .build();

            ClassificationResponse.ClassificationResult classificationResult = ClassificationResponse.ClassificationResult
                    .builder()
                    .prediction(prediction)
                    .confidence(confidence)
                    .probabilities(probObj)
                    .build();

            return ClassificationResponse.builder()
                    .success(true)
                    .message("Classification successful")
                    .result(classificationResult)
                    .build();

        } catch (IOException | InterruptedException e) {
            if (process != null) {
                process.destroyForcibly();
            }
            e.printStackTrace();
            return ClassificationResponse.builder()
                    .success(false)
                    .message("Error running Python predictor: " + e.getMessage())
                    .build();
        }
    }
}
