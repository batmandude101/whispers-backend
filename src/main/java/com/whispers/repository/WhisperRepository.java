package com.whispers.repository;

import com.whispers.model.Whisper;
import com.whispers.model.Emotion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WhisperRepository extends JpaRepository<Whisper, Long> {

    /**
     * Find whispers within a radius (in kilometers) from a given point
     * Uses basic distance calculation for MVP (not PostGIS yet)
     */
    @Query("SELECT w FROM Whisper w WHERE " +
            "(6371 * acos(cos(radians(:lat)) * cos(radians(w.latitude)) * " +
            "cos(radians(w.longitude) - radians(:lng)) + sin(radians(:lat)) * " +
            "sin(radians(w.latitude)))) <= :radiusKm " +
            "ORDER BY w.createdAt DESC")
    List<Whisper> findWhispersWithinRadius(
            @Param("lat") Double latitude,
            @Param("lng") Double longitude,
            @Param("radiusKm") Double radiusKm
    );

    /**
     * Find whispers within radius filtered by emotion
     */
    @Query("SELECT w FROM Whisper w WHERE " +
            "(6371 * acos(cos(radians(:lat)) * cos(radians(w.latitude)) * " +
            "cos(radians(w.longitude) - radians(:lng)) + sin(radians(:lat)) * " +
            "sin(radians(w.latitude)))) <= :radiusKm " +
            "AND w.emotion = :emotion " +
            "ORDER BY w.createdAt DESC")
    List<Whisper> findWhispersWithinRadiusByEmotion(
            @Param("lat") Double latitude,
            @Param("lng") Double longitude,
            @Param("radiusKm") Double radiusKm,
            @Param("emotion") Emotion emotion
    );

    /**
     * Find recent whispers (for testing/admin)
     */
    List<Whisper> findTop20ByOrderByCreatedAtDesc();
}