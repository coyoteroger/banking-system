package com.devsuv.customer.application.dto;

import jakarta.validation.constraints.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CustomerRequestDTO {

    @NotBlank(message = "El nombre es requerido")
    private String name;

    @NotBlank(message = "El género es requerido")
    @Pattern(regexp = "^(MASCULINO|FEMENINO)$", message = "El género debe ser MASCULINO o FEMENINO")
    private String gender;

    @NotNull(message = "La edad es requerida")
    @Min(value = 1, message = "La edad debe ser mayor a 0")
    @Max(value = 120, message = "La edad debe ser un valor realista")
    private Integer age;

    @NotBlank(message = "La identificación es requerida")
    private String identification;

    @NotBlank(message = "La dirección es requerida")
    private String address;

    @NotBlank(message = "El teléfono es requerido")
    private String phone;

    @NotBlank(message = "La contraseña es requerida")
    @Size(min = 4, message = "La contraseña debe tener al menos 4 caracteres")
    private String password;

    private Boolean status;
}
