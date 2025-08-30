package com.dpm05.common.sample;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class sample {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
}
