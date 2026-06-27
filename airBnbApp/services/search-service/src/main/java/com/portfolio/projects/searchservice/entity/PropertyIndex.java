package com.portfolio.projects.searchservice.entity;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.index.Indexed;

import java.math.BigDecimal;

@Data
@RedisHash("PropertyIndex")
public class PropertyIndex {
    @Id
    private Long propertyId;
    private String name;
    @Indexed
    private String city;
    private BigDecimal minPrice;
    private Boolean active;
}
