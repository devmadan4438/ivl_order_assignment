package com.infovision.orderserviceapp.entities;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Table(name= "sequence_generators")
@Data
@NoArgsConstructor
public class SequenceGenerator {
    @Id
    private Long id;

    private String sequenceName;

    private int year;

    private int counter;

}
