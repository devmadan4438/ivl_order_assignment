package com.infovision.orderserviceapp.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
public class OrderWithDetailsDTO {
    private Long id;
    private String orderNo = "";
    private String orderStatus;
    private float subTotal;
    private float taxAmount;
    private float netAmount;

    private String createdBy;
    private LocalDateTime createdOn;
    private String updatedBy;

    private int orderItemCount;
    private List<Item> orderDetails = new ArrayList<>();


    // Inner class to represent Item structure
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Item {
        private Long id;
        private Long orderId;
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

