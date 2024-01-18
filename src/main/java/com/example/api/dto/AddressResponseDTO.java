package com.example.api.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AddressResponseDTO {

    @ApiModelProperty(position = 1)
    private String street;

    @ApiModelProperty(position = 2)
    private String city;

    @ApiModelProperty(position = 3)
    private String state;

    @ApiModelProperty(position = 4)
    private String zipCode;

    @ApiModelProperty(position = 5)
    private String country;

    @ApiModelProperty(position = 6)
    private Long customerId;

    @ApiModelProperty(position = 6)
    private String neighborhood;
}
