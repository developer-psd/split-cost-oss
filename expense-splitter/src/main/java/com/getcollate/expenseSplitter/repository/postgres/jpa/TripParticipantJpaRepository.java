package com.getcollate.expenseSplitter.repository.postgres.jpa;

import com.getcollate.expenseSplitter.repository.postgres.entity.TripParticipantEntity;
import com.getcollate.expenseSplitter.repository.postgres.entity.id.TripParticipantId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TripParticipantJpaRepository extends JpaRepository<TripParticipantEntity, TripParticipantId> {
    List<TripParticipantEntity> findByIdTripIdOrderByParticipantOrderAsc(UUID tripId);
    List<TripParticipantEntity> findByIdTripIdAndActiveTrueOrderByParticipantOrderAsc(UUID tripId);
    Optional<TripParticipantEntity> findByIdTripIdAndParticipantName(UUID tripId, String participantName);
    boolean existsByIdTripIdAndActiveTrue(UUID tripId);
}
