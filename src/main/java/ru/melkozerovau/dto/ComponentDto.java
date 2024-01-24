package ru.melkozerovau.dto;

import lombok.Getter;
import lombok.Setter;
@Getter
@Setter
public class ComponentDto {
    private double ManufacturersPrice;
    private double LogisticsCosts;
    private double CustomDuties;
    private int Quantity;
}
