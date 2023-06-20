package org.example.StockRepository;

import org.example.Entity.StockData;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StockRepository extends JpaRepository<StockData,Long> {

}
