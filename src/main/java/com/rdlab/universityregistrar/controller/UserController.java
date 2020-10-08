package com.rdlab.universityregistrar.controller;

import com.rdlab.universityregistrar.controller.response.Response;
import com.rdlab.universityregistrar.service.ServiceFunctionality;
import com.rdlab.universityregistrar.service.dto.UserDTO;
import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * REST Api controller implementation for operation with {@link UserDTO}.
 * Performs input data basic (JSR-303) validation, puts existing validation constraints to {@link BindingResult} instance
 * and calls corresponding service methods necessary to perform requested operations.
 */
@RestController
@RequestMapping("/api")
@Api(tags = "Students API")
public class UserController {
    @Autowired
    ServiceFunctionality<UserDTO> service;

    @GetMapping("/users")
    @ApiOperation(value = "Get list of all users", produces = "application/json")
    @ApiResponses({
            @ApiResponse(
                    code = 200,
                    message = "List of users fetched successfully",
                    response = Response.class,
                    examples = @Example({
                            @ExampleProperty(value = "list of all available items of requested type", mediaType = "message"),
                            @ExampleProperty(value = "1600682630711", mediaType = "timeStamp"),
                            @ExampleProperty(value = "[#/definitions/User]", mediaType = "responseBody")
                    }
                    )
            ),
            @ApiResponse(
                    code = 500,
                    message = "Internal server or db error occurred",
                    response = Response.class,
                    examples = @Example({
                            @ExampleProperty(value = "internal server error occurred", mediaType = "message"),
                            @ExampleProperty(value = "1600682630711", mediaType = "timeStamp"),
                            @ExampleProperty(value = "null", mediaType = "responseBody")
                    }
                    )
            )
    })
    public ResponseEntity<Response> getAllEntities() {
        return service.getAllEntities();
    }

    @GetMapping("/users/{userId}")
    @ApiOperation(value = "Get user by login", notes = "Provide a login to look up specific user", produces = "application/json")
    @ApiResponses({
            @ApiResponse(
                    code = 200,
                    message = "User fetched successfully",
                    response = Response.class,
                    examples = @Example({
                            @ExampleProperty(value = "item of requested type with id: 1", mediaType = "message"),
                            @ExampleProperty(value = "1600682630711", mediaType = "timeStamp"),
                            @ExampleProperty(value = "[#/definitions/User]", mediaType = "responseBody")
                    }
                    )
            ),
            @ApiResponse(
                    code = 404,
                    message = "User with such id is not found",
                    response = Response.class,
                    examples = @Example({
                            @ExampleProperty(value = "item of requested type with such id not found", mediaType = "message"),
                            @ExampleProperty(value = "1600682630711", mediaType = "timeStamp"),
                            @ExampleProperty(value = "", mediaType = "responseBody")
                    }
                    )
            ),
            @ApiResponse(
                    code = 500,
                    message = "Internal server or db error occurred",
                    response = Response.class,
                    examples = @Example({
                            @ExampleProperty(value = "internal server error occurred", mediaType = "message"),
                            @ExampleProperty(value = "1600682630711", mediaType = "timeStamp"),
                            @ExampleProperty(value = "null", mediaType = "responseBody")
                    }
                    )
            )
    })
    public ResponseEntity<Response> getEntity(
            @ApiParam(value = "Login value for the user you need to retrieve", required = true, type = "string")
            @PathVariable String userId) {
        return service.getEntity(userId);
    }

    @PostMapping("/users")
    @ApiOperation(value = "Add user", notes = "Provide a user in json format", produces = "application/json", consumes = "application/json")
    @ApiResponses({
            @ApiResponse(
                    code = 201,
                    message = "User added successfully",
                    response = Response.class,
                    examples = @Example({
                            @ExampleProperty(value = "item successfully added", mediaType = "message"),
                            @ExampleProperty(value = "1600682630711", mediaType = "timeStamp"),
                            @ExampleProperty(value = "#/definitions/User", mediaType = "responseBody")
                    }
                    )
            ),
            @ApiResponse(
                    code = 400,
                    message = "User with such semester id already exists",
                    response = Response.class,
                    examples = @Example({
                            @ExampleProperty(value = "item is not unique (already exists)", mediaType = "message"),
                            @ExampleProperty(value = "1600682630711", mediaType = "timeStamp"),
                            @ExampleProperty(value = "", mediaType = "responseBody")
                    }
                    )
            ),
            @ApiResponse(
                    code = 400,
                    message = "Provided user parameters violate one or more validation constraints",
                    response = Response.class,
                    examples = @Example({
                            @ExampleProperty(value = "invalid input", mediaType = "message"),
                            @ExampleProperty(value = "1600682630711", mediaType = "timeStamp"),
                            @ExampleProperty(value = "[login cannot be longer than 45 characters, password cannot be longer than 45 characters]", mediaType = "responseBody")
                    }
                    )
            ),
            @ApiResponse(
                    code = 500,
                    message = "Internal server or db error occurred",
                    response = Response.class,
                    examples = @Example({
                            @ExampleProperty(value = "internal server error occurred", mediaType = "message"),
                            @ExampleProperty(value = "1600682630711", mediaType = "timeStamp"),
                            @ExampleProperty(value = "null", mediaType = "responseBody")
                    }
                    )
            )
    })
    public ResponseEntity<Response> addUser(
            @ApiParam(value = "User parameters in json format",
                    required = true,
                    type = "#/definitions/User",
                    examples = @Example({
                            @ExampleProperty(value = "login", mediaType = "login"),
                            @ExampleProperty(value = "pas$$word", mediaType = "password"),
                            @ExampleProperty(value = "true", mediaType = "isActive"),
                            @ExampleProperty(value = "2", mediaType = "studentId"),
                    }))
            @RequestBody @Valid UserDTO userToAdd, BindingResult bindingResult) {
        return service.addEntity(userToAdd, bindingResult);
    }

    @PutMapping("/users")
    @ApiOperation(value = "Update an user", notes = "Provide an user in json format", produces = "application/json", consumes = "application/json")
    @ApiResponses({
            @ApiResponse(
                    code = 200,
                    message = "User updated successfully",
                    response = Response.class,
                    examples = @Example({
                            @ExampleProperty(value = "item updated successfully", mediaType = "message"),
                            @ExampleProperty(value = "1600682630711", mediaType = "timeStamp"),
                            @ExampleProperty(value = "#/definitions/User", mediaType = "responseBody")
                    }
                    )
            ),
            @ApiResponse(
                    code = 404,
                    message = "User with such id is not found",
                    response = Response.class,
                    examples = @Example({
                            @ExampleProperty(value = "item of requested type with such id not found", mediaType = "message"),
                            @ExampleProperty(value = "1600682630711", mediaType = "timeStamp"),
                            @ExampleProperty(value = "", mediaType = "responseBody")
                    }
                    )
            ),
            @ApiResponse(
                    code = 400,
                    message = "Provided user parameters violate one or more validation constraints",
                    response = Response.class,
                    examples = @Example({
                            @ExampleProperty(value = "invalid input", mediaType = "message"),
                            @ExampleProperty(value = "1600682630711", mediaType = "timeStamp"),
                            @ExampleProperty(value = "[login cannot be longer than 45 characters, password cannot be longer than 45 characters]", mediaType = "responseBody")
                    }
                    )
            ),
            @ApiResponse(
                    code = 500,
                    message = "Internal server or db error occurred",
                    response = Response.class,
                    examples = @Example({
                            @ExampleProperty(value = "internal server error occurred", mediaType = "message"),
                            @ExampleProperty(value = "1600682630711", mediaType = "timeStamp"),
                            @ExampleProperty(value = "null", mediaType = "responseBody")
                    }
                    )
            )
    })
    public ResponseEntity<Response> updateUser(
            @ApiParam(value = "User parameters in json format",
                    required = true,
                    type = "#/definitions/User",
                    examples = @Example({
                            @ExampleProperty(value = "login", mediaType = "login"),
                            @ExampleProperty(value = "pas$$word", mediaType = "password"),
                            @ExampleProperty(value = "true", mediaType = "isActive"),
                            @ExampleProperty(value = "2", mediaType = "studentId"),
                    }))
            @RequestBody @Valid UserDTO userToUpdate, BindingResult bindingResult) {
        return service.updateEntity(userToUpdate, bindingResult);
    }

    @DeleteMapping("/users/{userId}")
    @ApiOperation(value = "Delete a user", notes = "Provide an login to delete specific user", produces = "application/json")
    @ApiResponses({
            @ApiResponse(
                    code = 200,
                    message = "User deleted successfully",
                    response = Response.class,
                    examples = @Example({
                            @ExampleProperty(value = "item deleted successfully", mediaType = "message"),
                            @ExampleProperty(value = "1600682630711", mediaType = "timeStamp"),
                            @ExampleProperty(value = "", mediaType = "responseBody")
                    }
                    )
            ),
            @ApiResponse(
                    code = 404,
                    message = "User with such id is not found",
                    response = Response.class,
                    examples = @Example({
                            @ExampleProperty(value = "item of requested type with such id not found", mediaType = "message"),
                            @ExampleProperty(value = "1600682630711", mediaType = "timeStamp"),
                            @ExampleProperty(value = "", mediaType = "responseBody")
                    }
                    )
            ),
            @ApiResponse(
                    code = 500,
                    message = "Internal server or db error occurred",
                    response = Response.class,
                    examples = @Example({
                            @ExampleProperty(value = "internal server error occurred", mediaType = "message"),
                            @ExampleProperty(value = "1600682630711", mediaType = "timeStamp"),
                            @ExampleProperty(value = "null", mediaType = "responseBody")
                    }
                    )
            )
    })
    public ResponseEntity<Response> deleteEntity(
            @ApiParam(value = "Login value for the user you need to delete", required = true, type = "string")
            @PathVariable String userId) {
        return service.deleteEntity(userId);
    }

    @GetMapping("/users/search")
    @ApiOperation(value = "Get all users that meet the provided search criterion", notes = "Provide a search criterion to look up for specific users", produces = "application/json")
    @ApiResponses({
            @ApiResponse(
                    code = 200,
                    message = "List of found users fetched successfully",
                    response = Response.class,
                    examples = @Example({
                            @ExampleProperty(value = "list of all items of requested type containing search criterion", mediaType = "message"),
                            @ExampleProperty(value = "1600682630711", mediaType = "timeStamp"),
                            @ExampleProperty(value = "[#/definitions/User]", mediaType = "responseBody")
                    }
                    )
            ),
            @ApiResponse(
                    code = 500,
                    message = "Internal server or db error occurred",
                    response = Response.class,
                    examples = @Example({
                            @ExampleProperty(value = "internal server error occurred", mediaType = "message"),
                            @ExampleProperty(value = "1600682630711", mediaType = "timeStamp"),
                            @ExampleProperty(value = "null", mediaType = "responseBody")
                    }
                    )
            )
    })
    public ResponseEntity<Response> searchForUsers(
            @ApiParam(value = "Search criterion (semester name or semester year)", required = true, type = "string/int32")
            @RequestParam String param) {
        return service.searchForEntities(param);
    }
}
