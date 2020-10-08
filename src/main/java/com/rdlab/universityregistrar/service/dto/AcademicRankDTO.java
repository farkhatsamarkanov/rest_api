package com.rdlab.universityregistrar.service.dto;

import com.rdlab.universityregistrar.model.entity.AcademicRank;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

/**
 * Data transfer object representing {@link AcademicRank} entity. Used in
 *
 * @Controller and
 * @Service layers.
 * Contains basic bean validation rules.
 */
@Data
@Builder
@NoArgsConstructor(force = true, access = AccessLevel.PRIVATE)
@AllArgsConstructor
@ApiModel(value = "Academic rank", description = "Academic rank parameters")
public class AcademicRankDTO {
    private Integer rankId;
    @ApiModelProperty(value = "Numeric rank")
    @NotNull(message = "numeric rank cannot be null")
    private Integer numericRank;

    @NotEmpty(message = "rank name cannot be empty")
    @Size(max = 45, message = "rank name cannot be longer than 45 characters")
    @Pattern(regexp = "^[a-zA-Z0-9\\\\\\s]*$", message = "rank name must contain " +
            "only letters in range (a-z, A-Z), " +
            "whitespaces and numbers from 0 to 9")
    @ApiModelProperty(value = "Rank name")
    private String rankName;
}
