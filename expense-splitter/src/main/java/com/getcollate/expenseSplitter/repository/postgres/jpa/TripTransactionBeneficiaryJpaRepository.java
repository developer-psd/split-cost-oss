package com.getcollate.expenseSplitter.repository.postgres.jpa;

import com.getcollate.expenseSplitter.repository.postgres.entity.TripTransactionBeneficiaryEntity;
import com.getcollate.expenseSplitter.repository.postgres.entity.id.TripTransactionBeneficiaryId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface TripTransactionBeneficiaryJpaRepository extends JpaRepository<TripTransactionBeneficiaryEntity, TripTransactionBeneficiaryId> {
    List<TripTransactionBeneficiaryEntity> findByIdTripIdAndIdTransactionIdOrderByIdBeneficiaryOrderAsc(UUID tripId, UUID transactionId);
}
