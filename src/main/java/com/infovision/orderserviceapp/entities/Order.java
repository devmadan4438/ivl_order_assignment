package com.infovision.orderserviceapp.entities;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;

@Table(name= "orders")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Order {

    @Id
    private Long id;

    private String orderNo;

    private String orderStatus;

    private float subTotal;
    private float taxAmount;
    private float netAmount;

    private String createdBy;
    private LocalDateTime createdOn;

    private String updatedBy;
    private LocalDateTime updatedOn;

    private String deletedBy;
    private LocalDateTime deletedOn;

    private Boolean isDeleted = false;

}
