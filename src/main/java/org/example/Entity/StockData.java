package org.example.Entity;

import jakarta.persistence.*;
import jdk.jfr.DataAmount;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name="stock_data")
public class StockData {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "import_ts")
    private LocalDateTime importTs;

    @Column(name = "date")
    private LocalDate date;

    @Column(name = "open")
    private BigDecimal open;

    @Column(name = "high")
    private BigDecimal high;

    @Column(name = "low")
    private BigDecimal low;

    @Column(name = "close")
    private BigDecimal close;

    @Column(name = "adjusted_close")
    private BigDecimal adjustedClose;

    @Column(name = "volume")
    private Long volume;
}
