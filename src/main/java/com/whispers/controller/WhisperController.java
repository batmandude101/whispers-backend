package com.whispers.controller;

import com.whispers.model.Whisper;
import com.whispers.model.Emotion;
import com.whispers.repository.WhisperRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/whispers")
@CrossOrigin(origins = "*", allowedHeaders = "*", methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.OPTIONS})
public class WhisperController {

    @Autowired
    private WhisperRepository whisperRepository;

    /**
     * Create a new whisper
     * POST /api/whispers
     */
    @PostMapping
    public ResponseEntity<Whisper> createWhisper(@RequestBody CreateWhisperRequest request) {
        try {
            // Validate input
            if (request.getText() == null || request.getText().trim().isEmpty()) {
                return ResponseEntity.badRequest().build();
            }

            if (request.getText().length() > 300) {
                return ResponseEntity.badRequest().build();
            }

            // Create whisper
            Whisper whisper = new Whisper(
                    request.getText().trim(),
                    Emotion.fromValue(request.getEmotion()),
                    request.getLatitude(),
                    request.getLongitude()
            );

            Whisper savedWhisper = whisperRepository.save(whisper);
            return ResponseEntity.ok(savedWhisper);

        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * Get nearby whispers for the feed
     * GET /api/whispers/feed?lat=X&lng=Y&emotion=optional
     */
    @GetMapping("/feed")
    public ResponseEntity<List<Whisper>> getNearbyWhispers(
            @RequestParam Double lat,
            @RequestParam Double lng,
            @RequestParam(required = false) String emotion) {

        try {
            Double radiusKm = 1.0; // 1km radius for MVP
            List<Whisper> whispers;

            if (emotion != null && !emotion.isEmpty()) {
                Emotion emotionEnum = Emotion.fromValue(emotion);
                whispers = whisperRepository.findWhispersWithinRadiusByEmotion(lat, lng, radiusKm, emotionEnum);
            } else {
                whispers = whisperRepository.findWhispersWithinRadius(lat, lng, radiusKm);
            }
            System.out.println(whispers);
            return ResponseEntity.ok(whispers);

        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * Get a single whisper by ID (for detail view)
     * GET /api/whispers/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<Whisper> getWhisperById(@PathVariable Long id) {
        Optional<Whisper> whisper = whisperRepository.findById(id);
        return whisper.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }



    /**
     * Request DTO for creating whispers
     */
    public static class CreateWhisperRequest {
        private String text;
        private String emotion;
        private Double latitude;
        private Double longitude;

        // Getters and setters
        public String getText() { return text; }
        public void setText(String text) { this.text = text; }

        public String getEmotion() { return emotion; }
        public void setEmotion(String emotion) { this.emotion = emotion; }

        public Double getLatitude() { return latitude; }
        public void setLatitude(Double latitude) { this.latitude = latitude; }

        public Double getLongitude() { return longitude; }
        public void setLongitude(Double longitude) { this.longitude = longitude; }
    }
}

