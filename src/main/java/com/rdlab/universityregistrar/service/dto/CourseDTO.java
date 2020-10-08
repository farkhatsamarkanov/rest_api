package com.rdlab.universityregistrar.service.dto;

import com.rdlab.universityregistrar.model.entity.Course;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

/**
 * Data transfer object representing {@link Course} entity. Used in
 *
 * @Controller and
 * @Service layers.
 * Contains basic bean validation rules.
 */
@Data
@Builder
@NoArgsConstructor(force = true, access = AccessLevel.PRIVATE)
@AllArgsConstructor
@ApiModel(value = "Course", description = "Course parameters")
public class CourseDTO {
    @ApiModelProperty(value = "Course id")
    private Integer courseId;

    @NotEmpty(message = "course title cannot be empty")
    @Size(max = 45, message = "course title cannot be longer than 45 characters")
    @Pattern(regexp = "^[a-zA-Z0-9\\\\\\s]*$", message = "course title can " +
            "contain only letters in range (a-z, A-Z), " +
            "numbers from 0 to 9 and whitespaces")
    @ApiModelProperty(value = "Course title")
    private String courseTitle;

    @Size(max = 45, message = "course description cannot be longer than 45 characters")
    @ApiModelProperty(value = "Course description")
    private String courseDescription;
}
