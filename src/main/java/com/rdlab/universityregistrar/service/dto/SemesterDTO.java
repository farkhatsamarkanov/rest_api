package com.rdlab.universityregistrar.service.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.rdlab.universityregistrar.model.entity.Semester;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import javax.validation.constraints.*;
import java.util.Date;

/**
 * Data transfer object representing {@link Semester} entity. Used in
 *
 * @Controller and
 * @Service layers.
 * Contains basic bean validation rules.
 */
@Data
@Builder
@NoArgsConstructor(force = true, access = AccessLevel.PRIVATE)
@AllArgsConstructor
@ApiModel(value = "Semester", description = "Semester parameters")
public class SemesterDTO {
    private Integer entryId;
    @NotEmpty(message = "semesterId cannot be empty")
    @Size(max = 7, message = "semesterId cannot be longer than 7 characters")
    @Pattern(regexp = "^[a-zA-Z0-9]*$", message = "semesterId can " +
            "contain only letters in range (a-z, A-Z) and " +
            "numbers from 0 to 9")
    @ApiModelProperty(value = "Semester id")
    private String semesterId;
    @NotEmpty(message = "semester name cannot be empty")
    @Size(max = 30, message = "semesterName cannot be longer than 30 characters")
    @Pattern(regexp = "^[a-zA-Z0-9-.)(\\\\\\s]*$", message = "location can " +
            "contain only letters in range (a-z, A-Z), " +
            "numbers from 0 to 9, whitespaces and .-)( symbols")
    @ApiModelProperty(value = "Semester name")
    private String semesterName;
    @NotNull(message = "semester year cannot be null")
    @ApiModelProperty(value = "Semester year")
    private Integer semesterYear;
    @NotNull(message = "semester start time cannot be null")
    @Future(message = "semester begin time cannot be in the past")
    @JsonFormat(pattern = "yyyy-MM-dd")
    @ApiModelProperty(value = "Semester start time")
    private Date semesterStartTime;
    @NotNull(message = "semester end time cannot be null")
    @Future(message = "semester end time cannot be in the past")
    @JsonFormat(pattern = "yyyy-MM-dd")
    @ApiModelProperty(value = "Semester end time")
    private Date semesterEndTime;
}
