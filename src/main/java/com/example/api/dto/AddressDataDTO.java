package com.example.api.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AddressDataDTO {

    @ApiModelProperty(position = 0)
    private String street;

    @ApiModelProperty(position = 1)
    private String city;

    @ApiModelProperty(position = 2)
    private String state;

    @ApiModelProperty(position = 3)
    @NotBlank(message = "O CEP é obrigatório")
    @Size(min = 5, max = 10, message = "O CEP deve ter entre 5 e 10 caracteres")
    private String zipCode;

    @ApiModelProperty(position = 4)
    private String country;
}
