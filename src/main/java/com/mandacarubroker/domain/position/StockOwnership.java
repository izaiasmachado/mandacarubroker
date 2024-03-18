package com.mandacarubroker.domain.position;

import com.mandacarubroker.domain.stock.Stock;
import com.mandacarubroker.domain.user.User;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Table(name = "stock_ownership")
@Entity(name = "stock_ownership")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
public class StockOwnership {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;
    private int shares;
    @OneToOne
    private Stock stock;
    @OneToOne
    private User user;

    public StockOwnership(final RequestStockOwnershipDTO requestStockPositionDTO, final Stock receivedStock, final User receivedUser) {
        this.shares = requestStockPositionDTO.shares();
        this.stock = receivedStock;
        this.user = receivedUser;
    }

    public double getTotalValue() {
        return shares * stock.getPrice();
    }

    public Stock getStock() {
        return stock;
    }

    public void buyShares(final int sharesToBuy) {
        if (sharesToBuy < 0) {
            throw new IllegalArgumentException("Invalid number of shares");
        }

        shares += sharesToBuy;
    }

    public void sellShares(final int sharesToSell) {
        if (sharesToSell < 0) {
            throw new IllegalArgumentException("Invalid number of shares");
        }

        if (shares < sharesToSell) {
            throw new IllegalArgumentException("Insufficient shares");
        }

        shares -= sharesToSell;
    }
}
