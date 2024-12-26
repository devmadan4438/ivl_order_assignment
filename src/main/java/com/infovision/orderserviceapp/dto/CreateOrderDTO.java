package com.infovision.orderserviceapp.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
public class CreateOrderDTO {
    private Long id;
    private String orderNo = "";
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

    private List<Item> items;


    // Inner class to represent Item structure
    @Data
    @NoArgsConstructor
    public static class Item {
        private String name;
        private float length;
        private float breadth;
        private float height;
        private String size;
        private float price;
        private float taxPer;
        private float taxValue;
        private String uom;
    }
}

