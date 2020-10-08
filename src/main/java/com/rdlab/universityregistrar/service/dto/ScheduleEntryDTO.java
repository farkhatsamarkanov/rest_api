package com.rdlab.universityregistrar.service.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.rdlab.universityregistrar.model.entity.ScheduleEntry;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import javax.validation.constraints.*;
import java.util.Date;

/**
 * Data transfer object representing {@link ScheduleEntry} entity. Used in
 *
 * @Controller and
 * @Service layers.
 * Contains basic bean validation rules.
 */
@Data
@Builder
@NoArgsConstructor(force = true, access = AccessLevel.PRIVATE)
@AllArgsConstructor
@ApiModel(value = "Schedule entry", description = "Schedule entry parameters")
public class ScheduleEntryDTO {
    @ApiModelProperty(value = "Class id")
    private Integer entryId;
    @NotNull(message = "studentId must not be empty")
    @ApiModelProperty(value = "Class student id")
    private Integer studentId;
    @NotNull(message = "lecturerId must not be empty")
    @ApiModelProperty(value = "Class lecturer id")
    private Integer lecturerId;
    @NotNull(message = "courseId must not be empty")
    @ApiModelProperty(value = "Course id")
    private Integer courseId;
    @NotNull(message = "time must not be empty")
    @Future(message = "course begin time cannot be in the past")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Almaty")
    @ApiModelProperty(value = "Class beginning time")
    private Date time;
    @NotEmpty(message = "location cannot be empty")
    @Size(max = 45, message = "location cannot be longer than 45 characters")
    @Pattern(regexp = "^[a-zA-Z0-9.-\\\\\\s]*$", message = "location can " +
            "contain only letters in range (a-z, A-Z), " +
            "numbers from 0 to 9, whitespaces and .- symbols")
    @ApiModelProperty(value = "Class location")
    private String location;
    @NotEmpty(message = "semesterId cannot be empty")
    @Size(max = 7, message = "semesterId cannot be longer than 7 characters")
    @Pattern(regexp = "^[a-zA-Z0-9]*$", message = "semesterId can " +
            "contain only letters in range (a-z, A-Z) and " +
            "numbers from 0 to 9")
    @ApiModelProperty(value = "Semester id")
    private String semesterId;
}
