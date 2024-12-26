package com.infovision.orderserviceapp.entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Table("order_details")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderDetails {

    @Id
    private Long id;

    private Long orderId;

    private String itemName;
    private float length;
    private float weight;
    private float breadth;
    private float height;
    private String uom;
    private String size;
    private float price;
    private float taxPer;
    private float taxValue;

    public <T> OrderDetails(T orderId, T itemName) {
    }
}
