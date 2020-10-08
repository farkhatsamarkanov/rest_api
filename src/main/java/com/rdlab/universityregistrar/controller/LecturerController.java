package com.rdlab.universityregistrar.controller;

import com.rdlab.universityregistrar.controller.response.Response;
import com.rdlab.universityregistrar.service.ServiceFunctionality;
import com.rdlab.universityregistrar.service.dto.LecturerDTO;
import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * REST Api controller implementation for operation with {@link LecturerDTO}.
 * Performs input data basic (JSR-303) validation, puts existing validation constraints to {@link BindingResult} instance
 * and calls corresponding service methods necessary to perform requested operations.
 */
@RestController
@RequestMapping("/api")
@Api(tags = "Lecturers API")
public class LecturerController {
    @Autowired
    ServiceFunctionality<LecturerDTO> service;

    @GetMapping("/lecturers")
    @ApiOperation(value = "Get list of all lecturers", produces = "application/json")
    @ApiResponses({
            @ApiResponse(
                    code = 200,
                    message = "List of lecturers fetched successfully",
                    response = Response.class,
                    examples = @Example({
                            @ExampleProperty(value = "list of all available items of requested type", mediaType = "message"),
                            @ExampleProperty(value = "1600682630711", mediaType = "timeStamp"),
                            @ExampleProperty(value = "[#/definitions/Lecturer]", mediaType = "responseBody")
                    })
            ),
            @ApiResponse(
                    code = 500,
                    message = "Internal server or db error occurred",
                    response = Response.class,
                    examples = @Example({
                            @ExampleProperty(value = "internal server error occurred", mediaType = "message"),
                            @ExampleProperty(value = "1600682630711", mediaType = "timeStamp"),
                            @ExampleProperty(value = "null", mediaType = "responseBody")
                    })
            )
    })
    public ResponseEntity<Response> getAllEntities() {
        return service.getAllEntities();
    }

    @GetMapping("/lecturers/{lecturerId}")
    @ApiOperation(value = "Get lecturer by ID", notes = "Provide an ID to look up specific lecturer", produces = "application/json")
    @ApiResponses({
            @ApiResponse(
                    code = 200,
                    message = "Lecturer fetched successfully",
                    response = Response.class,
                    examples = @Example({
                            @ExampleProperty(value = "item of requested type with id: 1", mediaType = "message"),
                            @ExampleProperty(value = "1600682630711", mediaType = "timeStamp"),
                            @ExampleProperty(value = "[#/definitions/Lecturer]", mediaType = "responseBody")
                    })
            ),
            @ApiResponse(
                    code = 404,
                    message = "Lecturer with such id is not found",
                    response = Response.class,
                    examples = @Example({
                            @ExampleProperty(value = "item of requested type with such id not found", mediaType = "message"),
                            @ExampleProperty(value = "1600682630711", mediaType = "timeStamp"),
                            @ExampleProperty(value = "", mediaType = "responseBody")
                    })
            ),
            @ApiResponse(
                    code = 500,
                    message = "Internal server or db error occurred",
                    response = Response.class,
                    examples = @Example({
                            @ExampleProperty(value = "internal server error occurred", mediaType = "message"),
                            @ExampleProperty(value = "1600682630711", mediaType = "timeStamp"),
                            @ExampleProperty(value = "null", mediaType = "responseBody")
                    })
            )
    })
    public ResponseEntity<Response> getLecturer(
            @ApiParam(value = "ID value for the lecturer you need to retrieve", required = true, type = "int32")
            @PathVariable Integer lecturerId) {
        return service.getEntity(String.valueOf(lecturerId));
    }


    @PostMapping("/lecturers")
    @ApiOperation(value = "Add lecturer", notes = "Provide a lecturer in json format", produces = "application/json", consumes = "application/json")
    @ApiResponses({
            @ApiResponse(
                    code = 201,
                    message = "Lecturer added successfully",
                    response = Response.class,
                    examples = @Example({
                            @ExampleProperty(value = "item successfully added", mediaType = "message"),
                            @ExampleProperty(value = "1600682630711", mediaType = "timeStamp"),
                            @ExampleProperty(value = "#/definitions/Lecturer", mediaType = "responseBody")
                    })
            ),
            @ApiResponse(
                    code = 400,
                    message = "Provided lecturer parameters violate one or more validation constraints",
                    response = Response.class,
                    examples = @Example({
                            @ExampleProperty(value = "invalid input", mediaType = "message"),
                            @ExampleProperty(value = "1600682630711", mediaType = "timeStamp"),
                            @ExampleProperty(value = "[lecturer name cannot be empty, lecturer's date of birth cannot be in the future]", mediaType = "responseBody")
                    })
            ),
            @ApiResponse(
                    code = 500,
                    message = "Internal server or db error occurred",
                    response = Response.class,
                    examples = @Example({
                            @ExampleProperty(value = "internal server error occurred", mediaType = "message"),
                            @ExampleProperty(value = "1600682630711", mediaType = "timeStamp"),
                            @ExampleProperty(value = "null", mediaType = "responseBody")
                    })
            )
    })
    public ResponseEntity<Response> addLecturer(
            @ApiParam(value = "Lecturer parameters in json format",
                    required = true,
                    type = "#/definitions/Lecturer",
                    examples = @Example({
                            @ExampleProperty(value = "John Doe", mediaType = "lecturerName"),
                            @ExampleProperty(value = "1984-12-12", mediaType = "dateOfBirth"),
                            @ExampleProperty(value = "1", mediaType = "numericAcademicRank")
                    }))
            @RequestBody @Valid LecturerDTO lecturerToAdd, BindingResult bindingResult) {
        return service.addEntity(lecturerToAdd, bindingResult);
    }

    @PutMapping("/lecturers")
    @ApiOperation(value = "Update a lecturer", notes = "Provide a lecturer in json format", produces = "application/json", consumes = "application/json")
    @ApiResponses({
            @ApiResponse(
                    code = 200,
                    message = "Lecturer updated successfully",
                    response = Response.class,
                    examples = @Example({
                            @ExampleProperty(value = "item updated successfully", mediaType = "message"),
                            @ExampleProperty(value = "1600682630711", mediaType = "timeStamp"),
                            @ExampleProperty(value = "#/definitions/Lecturer", mediaType = "responseBody")
                    })
            ),
            @ApiResponse(
                    code = 404,
                    message = "Lecturer with such id is not found",
                    response = Response.class,
                    examples = @Example({
                            @ExampleProperty(value = "item of requested type with such id not found", mediaType = "message"),
                            @ExampleProperty(value = "1600682630711", mediaType = "timeStamp"),
                            @ExampleProperty(value = "", mediaType = "responseBody")
                    })
            ),
            @ApiResponse(
                    code = 400,
                    message = "Provided lecturer parameters violate one or more validation constraints",
                    response = Response.class,
                    examples = @Example({
                            @ExampleProperty(value = "invalid input", mediaType = "message"),
                            @ExampleProperty(value = "1600682630711", mediaType = "timeStamp"),
                            @ExampleProperty(value = "[lecturer name cannot be empty, lecturer's date of birth cannot be in the future]", mediaType = "responseBody")
                    })
            ),
            @ApiResponse(
                    code = 500,
                    message = "Internal server or db error occurred",
                    response = Response.class,
                    examples = @Example({
                            @ExampleProperty(value = "internal server error occurred", mediaType = "message"),
                            @ExampleProperty(value = "1600682630711", mediaType = "timeStamp"),
                            @ExampleProperty(value = "null", mediaType = "responseBody")
                    })
            )
    })
    public ResponseEntity<Response> updateLecturer(
            @ApiParam(value = "Lecturer parameters in json format",
                    required = true,
                    type = "#/definitions/Lecturer",
                    examples = @Example({
                            @ExampleProperty(value = "1", mediaType = "lecturerId"),
                            @ExampleProperty(value = "John Doe", mediaType = "lecturerName"),
                            @ExampleProperty(value = "1984-12-12", mediaType = "dateOfBirth"),
                            @ExampleProperty(value = "1", mediaType = "numericAcademicRank")
                    }))
            @RequestBody @Valid LecturerDTO lecturerToUpdate, BindingResult bindingResult) {
        return service.updateEntity(lecturerToUpdate, bindingResult);
    }

    @DeleteMapping("/lecturers/{lecturerId}")
    @ApiOperation(value = "Delete a lecturer", notes = "Provide an id to delete specific lecturer", produces = "application/json")
    @ApiResponses({
            @ApiResponse(
                    code = 200,
                    message = "Lecturer deleted successfully",
                    response = Response.class,
                    examples = @Example({
                            @ExampleProperty(value = "item deleted successfully", mediaType = "message"),
                            @ExampleProperty(value = "1600682630711", mediaType = "timeStamp"),
                            @ExampleProperty(value = "", mediaType = "responseBody")
                    })
            ),
            @ApiResponse(
                    code = 404,
                    message = "Lecturer with such id is not found",
                    response = Response.class,
                    examples = @Example({
                            @ExampleProperty(value = "item of requested type with such id not found", mediaType = "message"),
                            @ExampleProperty(value = "1600682630711", mediaType = "timeStamp"),
                            @ExampleProperty(value = "", mediaType = "responseBody")
                    })
            ),
            @ApiResponse(
                    code = 500,
                    message = "Internal server or db error occurred",
                    response = Response.class,
                    examples = @Example({
                            @ExampleProperty(value = "internal server error occurred", mediaType = "message"),
                            @ExampleProperty(value = "1600682630711", mediaType = "timeStamp"),
                            @ExampleProperty(value = "null", mediaType = "responseBody")
                    })
            )
    })
    public ResponseEntity<Response> deleteLecturer(
            @ApiParam(value = "ID value for the lecturer you need to delete", required = true, type = "int32")
            @PathVariable Integer lecturerId) {
        return service.deleteEntity(String.valueOf(lecturerId));
    }

    @GetMapping("/lecturers/search")
    @ApiOperation(value = "Get all lecturers that meet the provided search criterion", notes = "Provide a search criterion to look up for specific lecturers", produces = "application/json")
    @ApiResponses({
            @ApiResponse(
                    code = 200,
                    message = "List of found lecturers fetched successfully",
                    response = Response.class,
                    examples = @Example({
                            @ExampleProperty(value = "list of all items of requested type containing search criterion", mediaType = "message"),
                            @ExampleProperty(value = "1600682630711", mediaType = "timeStamp"),
                            @ExampleProperty(value = "[#/definitions/Lecturer]", mediaType = "responseBody")
                    })
            ),
            @ApiResponse(
                    code = 500,
                    message = "Internal server or db error occurred",
                    response = Response.class,
                    examples = @Example({
                            @ExampleProperty(value = "internal server error occurred", mediaType = "message"),
                            @ExampleProperty(value = "1600682630711", mediaType = "timeStamp"),
                            @ExampleProperty(value = "null", mediaType = "responseBody")
                    })
            )
    })
    public ResponseEntity<Response> searchForLecturers(
            @ApiParam(value = "Search criterion (lecturer's name or lecturer's numeric academic rank)", required = true, type = "string/int32")
            @RequestParam String param) {
        return service.searchForEntities(param);
    }
}
