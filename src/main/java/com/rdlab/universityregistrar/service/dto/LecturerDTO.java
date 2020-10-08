package com.rdlab.universityregistrar.service.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.rdlab.universityregistrar.model.entity.Lecturer;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import javax.validation.constraints.*;
import java.util.Date;

/**
 * Data transfer object representing {@link Lecturer} entity. Used in
 *
 * @Controller and
 * @Service layers.
 * Contains basic bean validation rules.
 */
@Data
@Builder
@NoArgsConstructor(force = true, access = AccessLevel.PRIVATE)
@AllArgsConstructor
@ApiModel(value = "Lecturer", description = "Lecturer parameters")
public class LecturerDTO {
    @ApiModelProperty(value = "Lecturer id")
    private Integer lecturerId;

    @NotEmpty(message = "lecturer name cannot be empty")
    @Size(max = 45, message = "lecturer name cannot be longer than 45 characters")
    @Pattern(regexp = "^[a-zA-Z\\\\\\s]*$", message = "course title can " +
            "contain only letters in range (a-z, A-Z) and whitespaces")
    @ApiModelProperty(value = "Lecturer name")
    private String lecturerName;

    @Past(message = "lecturer's date of birth cannot be in the future")
    @JsonFormat(pattern = "yyyy-MM-dd")
    @ApiModelProperty(value = "Lecturer's date of birth")
    private Date dateOfBirth;

    @NotNull(message = "lecturer academic rank cannot be null")
    @ApiModelProperty(value = "Lecturer's numeric academic rank")
    private Integer numericAcademicRank;
}
