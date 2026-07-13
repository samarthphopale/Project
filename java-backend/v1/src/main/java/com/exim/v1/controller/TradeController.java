package com.exim.v1.controller;

import com.exim.v1.model.Trade;
import com.exim.v1.service.TradeService;

import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/trades")
public class TradeController {

    private final TradeService service;

    public TradeController(TradeService service) {
        this.service = service;
    }

    @PostMapping
    public Trade create(@Valid @RequestBody Trade trade) {
        return service.createTrade(trade);
    }

    @GetMapping
    public List<Trade> getAll() {
        return service.getAllTrades();
    }
}