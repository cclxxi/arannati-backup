package kz.arannati.arannati.dto.auth;

import lombok.*;

import jakarta.validation.constraints.*;

@Getter
@Setter
@Builder
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class CosmetologistRegistrationRequest extends UserRegistrationRequest {

    @NotBlank(message = "Название учебного заведения обязательно")
    @Size(max = 255, message = "Название учебного заведения не должно превышать 255 символов")
    private String institutionName;

    @Min(value = 1990, message = "Год окончания не может быть раньше 1990")
    @Max(value = 2024, message = "Год окончания не может быть позже текущего года")
    private Integer graduationYear;

    @Size(max = 255, message = "Специализация не должна превышать 255 символов")
    private String specialization;

    @Size(max = 100, message = "Номер лицензии не должен превышать 100 символов")
    private String licenseNumber;
}
