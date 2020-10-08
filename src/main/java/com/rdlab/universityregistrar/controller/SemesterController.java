package com.rdlab.universityregistrar.controller;

import com.rdlab.universityregistrar.controller.response.Response;
import com.rdlab.universityregistrar.service.ServiceFunctionality;
import com.rdlab.universityregistrar.service.dto.SemesterDTO;
import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;


/**
 * REST Api controller implementation for operation with {@link SemesterDTO}.
 * Performs input data basic (JSR-303) validation, puts existing validation constraints to {@link BindingResult} instance
 * and calls corresponding service methods necessary to perform requested operations.
 */
@RestController
@RequestMapping("/api")
@Api(tags = "Semesters API")
public class SemesterController {
    @Autowired
    ServiceFunctionality<SemesterDTO> service;

    @GetMapping("/semesters")
    @ApiOperation(value = "Get list of all semesters", produces = "application/json")
    @ApiResponses({
            @ApiResponse(
                    code = 200,
                    message = "List of semesters fetched successfully",
                    response = Response.class,
                    examples = @Example({
                            @ExampleProperty(value = "list of all available items of requested type", mediaType = "message"),
                            @ExampleProperty(value = "1600682630711", mediaType = "timeStamp"),
                            @ExampleProperty(value = "[#/definitions/Semester]", mediaType = "responseBody")
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

    @GetMapping("/semesters/{semesterId}")
    @ApiOperation(value = "Get semester by id", notes = "Provide an id to look up specific semester", produces = "application/json")
    @ApiResponses({
            @ApiResponse(
                    code = 200,
                    message = "Semester fetched successfully",
                    response = Response.class,
                    examples = @Example({
                            @ExampleProperty(value = "item of requested type with id: 1", mediaType = "message"),
                            @ExampleProperty(value = "1600682630711", mediaType = "timeStamp"),
                            @ExampleProperty(value = "[#/definitions/Semester]", mediaType = "responseBody")
                    }
                    )
            ),
            @ApiResponse(
                    code = 404,
                    message = "Semester with such id is not found",
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
            @ApiParam(value = "ID value for the semester you need to retrieve", required = true, type = "string")
            @PathVariable String semesterId) {
        return service.getEntity(semesterId);
    }

    @PostMapping("/semesters")
    @ApiOperation(value = "Add semester", notes = "Provide a semester in json format", produces = "application/json", consumes = "application/json")
    @ApiResponses({
            @ApiResponse(
                    code = 201,
                    message = "Semester added successfully",
                    response = Response.class,
                    examples = @Example({
                            @ExampleProperty(value = "item successfully added", mediaType = "message"),
                            @ExampleProperty(value = "1600682630711", mediaType = "timeStamp"),
                            @ExampleProperty(value = "#/definitions/Semester", mediaType = "responseBody")
                    }
                    )
            ),
            @ApiResponse(
                    code = 400,
                    message = "Semester with such semester id already exists",
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
                    message = "Provided semester parameters violate one or more validation constraints",
                    response = Response.class,
                    examples = @Example({
                            @ExampleProperty(value = "invalid input", mediaType = "message"),
                            @ExampleProperty(value = "1600682630711", mediaType = "timeStamp"),
                            @ExampleProperty(value = "[semesterId cannot be longer than 7 characters, semester begin time cannot be in the past]", mediaType = "responseBody")
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
    public ResponseEntity<Response> addEntity(
            @ApiParam(value = "Semester parameters in json format",
                    required = true,
                    type = "#/definitions/Semester",
                    examples = @Example({
                            @ExampleProperty(value = "FAL2020", mediaType = "semesterId"),
                            @ExampleProperty(value = "Fall semester of 2020", mediaType = "semesterName"),
                            @ExampleProperty(value = "2020", mediaType = "semesterYear"),
                            @ExampleProperty(value = "2020-01-01", mediaType = "semesterStartTime"),
                            @ExampleProperty(value = "2020-05-05", mediaType = "semesterEndTime")
                    }))
            @RequestBody @Valid SemesterDTO semesterToAdd, BindingResult bindingResult) {
        return service.addEntity(semesterToAdd, bindingResult);
    }

    @PutMapping("/semesters")
    @ApiOperation(value = "Update a semester", notes = "Provide a semester in json format", produces = "application/json", consumes = "application/json")
    @ApiResponses({
            @ApiResponse(
                    code = 200,
                    message = "Semester updated successfully",
                    response = Response.class,
                    examples = @Example({
                            @ExampleProperty(value = "item updated successfully", mediaType = "message"),
                            @ExampleProperty(value = "1600682630711", mediaType = "timeStamp"),
                            @ExampleProperty(value = "#/definitions/Semester", mediaType = "responseBody")
                    }
                    )
            ),
            @ApiResponse(
                    code = 404,
                    message = "Semester with such id is not found",
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
                    message = "Provided course parameters violate one or more validation constraints",
                    response = Response.class,
                    examples = @Example({
                            @ExampleProperty(value = "invalid input", mediaType = "message"),
                            @ExampleProperty(value = "1600682630711", mediaType = "timeStamp"),
                            @ExampleProperty(value = "[semesterId cannot be longer than 7 characters, semester begin time cannot be in the past]", mediaType = "responseBody")
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
    public ResponseEntity<Response> updateEntity(
            @ApiParam(value = "Semester parameters in json format",
                    required = true,
                    type = "#/definitions/Semester",
                    examples = @Example({
                            @ExampleProperty(value = "FAL2020", mediaType = "semesterId"),
                            @ExampleProperty(value = "Fall semester of 2020", mediaType = "semesterName"),
                            @ExampleProperty(value = "2020", mediaType = "semesterYear"),
                            @ExampleProperty(value = "2020-01-01", mediaType = "semesterStartTime"),
                            @ExampleProperty(value = "2020-05-05", mediaType = "semesterEndTime")
                    }))
            @RequestBody @Valid SemesterDTO semesterToUpdate, BindingResult bindingResult) {
        return service.updateEntity(semesterToUpdate, bindingResult);
    }

    @DeleteMapping("/semesters/{semesterId}")
    @ApiOperation(value = "Delete a semester", notes = "Provide an id to delete specific semester", produces = "application/json")
    @ApiResponses({
            @ApiResponse(
                    code = 200,
                    message = "Semester deleted successfully",
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
                    message = "Semester with such id is not found",
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
    public ResponseEntity<Response> deleteSemester(
            @ApiParam(value = "ID value for the semester you need to delete", required = true, type = "string")
            @PathVariable String semesterId) {
        return service.deleteEntity(semesterId);
    }

    @GetMapping("/semesters/search")
    @ApiOperation(value = "Get all semesters that meet the provided search criterion", notes = "Provide a search criterion to look up for specific semesters", produces = "application/json")
    @ApiResponses({
            @ApiResponse(
                    code = 200,
                    message = "List of found semesters fetched successfully",
                    response = Response.class,
                    examples = @Example({
                            @ExampleProperty(value = "list of all items of requested type containing search criterion", mediaType = "message"),
                            @ExampleProperty(value = "1600682630711", mediaType = "timeStamp"),
                            @ExampleProperty(value = "[#/definitions/Semester]", mediaType = "responseBody")
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
    public ResponseEntity<Response> searchForSemesters(
            @ApiParam(value = "Search criterion (login or student id)", required = true, type = "string/int32")
            @RequestParam String param) {
        return service.searchForEntities(param);
    }
}
