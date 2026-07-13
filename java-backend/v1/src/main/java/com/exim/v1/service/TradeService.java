package com.exim.v1.service;



import com.exim.v1.model.Trade;
import com.exim.v1.repository.TradeRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TradeService {

    private final TradeRepository repo;

    public TradeService(TradeRepository repo) {
        this.repo = repo;
    }

    public Trade createTrade(Trade trade) {
        return repo.save(trade);
    }

    public List<Trade> getAllTrades() {
        return repo.findAll();
    }
}