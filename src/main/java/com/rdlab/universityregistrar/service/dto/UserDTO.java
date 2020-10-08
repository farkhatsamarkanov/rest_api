package com.rdlab.universityregistrar.service.dto;

import com.rdlab.universityregistrar.model.entity.User;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

/**
 * Data transfer object representing {@link User} entity. Used in
 *
 * @Controller and
 * @Service layers.
 * Contains basic bean validation rules.
 */
@Data
@Builder
@NoArgsConstructor(force = true, access = AccessLevel.PRIVATE)
@AllArgsConstructor
@ApiModel(value = "User", description = "User parameters")
public class UserDTO {
    private Integer userId;
    @NotEmpty(message = "login cannot be empty")
    @Size(max = 45, message = "login cannot be longer than 45 characters")
    @Pattern(regexp = "^[a-zA-Z0-9\\\\\\s_]*$", message = "login can " +
            "contain only letters in range (a-z, A-Z), whitespaces" +
            "numbers from 0 to 9 and _ symbols")
    @ApiModelProperty(value = "User's login")
    private String login;
    @NotEmpty(message = "password cannot be empty")
    @Size(max = 45, message = "password cannot be longer than 45 characters")
    @Pattern(regexp = "^[a-zA-Z0-9\\\\\\s._@#$%&*]*$", message = "password can " +
            "contain only letters in range (a-z, A-Z), whitespaces" +
            "numbers from 0 to 9 and .,_,@,#,$,*,^ symbols")
    @ApiModelProperty(value = "User's password")
    private String password;
    @NotNull(message = "isActive flag cannot be empty")
    @ApiModelProperty(value = "User's activity flag")
    private Boolean isActive;
    @NotNull(message = "student id cannot be empty")
    @ApiModelProperty(value = "Student id of user")
    private Integer studentId;
}
