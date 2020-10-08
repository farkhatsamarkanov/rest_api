package com.rdlab.universityregistrar.service.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.rdlab.universityregistrar.model.entity.Student;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Past;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.util.Date;

/**
 * Data transfer object representing {@link Student} entity. Used in
 *
 * @Controller and
 * @Service layers.
 * Contains basic bean validation rules.
 */
@Data
@Builder
@NoArgsConstructor(force = true, access = AccessLevel.PRIVATE)
@AllArgsConstructor
@ApiModel(value = "Student", description = "Student parameters")
public class StudentDTO {
    @ApiModelProperty(value = "Student id")
    private Integer studentId;

    @NotEmpty(message = "student name name cannot be empty")
    @Size(max = 45, message = "student name name cannot be longer than 45 characters")
    @Pattern(regexp = "^[a-zA-Z\\\\\\s]*$", message = "course title can " +
            "contain only letters in range (a-z, A-Z) and whitespaces")
    @ApiModelProperty(value = "Student name")
    private String studentName;

    @Past(message = "student date of birth cannot be in the future")
    @JsonFormat(pattern = "yyyy-MM-dd")
    @ApiModelProperty(value = "Student date of birth")
    private Date dateOfBirth;
}
