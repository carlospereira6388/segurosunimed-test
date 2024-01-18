package com.example.api.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;

@Data
@NoArgsConstructor
public class CustomerDataDTO {

    @ApiModelProperty(position = 0)
    @NotEmpty(message = "O nome é obrigatório")
    private String name;

    @ApiModelProperty(position = 1)
    @NotEmpty(message = "O email é obrigatório")
    @Email(message = "Email inválido")
    private String email;

    @ApiModelProperty(position = 2)
    @NotEmpty(message = "O gênero é obrigatório")
    private String gender;
}