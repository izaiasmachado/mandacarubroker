package com.mandacarubroker.domain.position;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface StockOwnershipRepository extends JpaRepository<StockOwnership, String> {
    List<StockOwnership> findByUserId(String userId);
}
