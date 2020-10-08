package com.rdlab.universityregistrar.controller;

import com.rdlab.universityregistrar.controller.response.Response;
import com.rdlab.universityregistrar.service.ServiceFunctionality;
import com.rdlab.universityregistrar.service.dto.AcademicRankDTO;
import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * REST Api controller implementation for operation with {@link AcademicRankDTO}.
 * Performs input data basic (JSR-303) validation, puts existing validation constraints to {@link BindingResult} instance
 * and calls corresponding service methods necessary to perform requested operations.
 */
@RestController
@RequestMapping("/api")
@Api(tags = "Academic ranks API")
public class AcademicRankController {
    @Autowired
    ServiceFunctionality<AcademicRankDTO> service;

    @GetMapping("/ranks")
    @ApiOperation(value = "Get list of all academic ranks", produces = "application/json")
    @ApiResponses({
            @ApiResponse(
                    code = 200,
                    message = "List of ranks fetched successfully",
                    response = Response.class,
                    examples = @Example({
                            @ExampleProperty(value = "list of all available items of requested type", mediaType = "message"),
                            @ExampleProperty(value = "1600682630711", mediaType = "timeStamp"),
                            @ExampleProperty(value = "[#/definitions/Academic rank]", mediaType = "responseBody")
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

    @GetMapping("/ranks/{rankId}")
    @ApiOperation(value = "Get academic rank by numeric rank", notes = "Provide a numeric rank to look up specific academic rank", produces = "application/json")
    @ApiResponses({
            @ApiResponse(
                    code = 200,
                    message = "Academic rank fetched successfully",
                    response = Response.class,
                    examples = @Example({
                            @ExampleProperty(value = "item of requested type with id: 1", mediaType = "message"),
                            @ExampleProperty(value = "1600682630711", mediaType = "timeStamp"),
                            @ExampleProperty(value = "[#/definitions/Academic rank]", mediaType = "responseBody")
                    })
            ),
            @ApiResponse(
                    code = 404,
                    message = "Academic rank with such id is not found",
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
    public ResponseEntity<Response> getEntity(
            @ApiParam(value = "Numeric rank value for the academic rank you need to retrieve", required = true, type = "int32")
            @PathVariable Integer rankId) {
        return service.getEntity(String.valueOf(rankId));
    }

    @PostMapping("/ranks")
    @ApiOperation(value = "Add academic rank", notes = "Provide an academic rank in json format", produces = "application/json", consumes = "application/json")
    @ApiResponses({
            @ApiResponse(
                    code = 201,
                    message = "Academic rank added successfully",
                    response = Response.class,
                    examples = @Example({
                            @ExampleProperty(value = "item successfully added", mediaType = "message"),
                            @ExampleProperty(value = "1600682630711", mediaType = "timeStamp"),
                            @ExampleProperty(value = "#/definitions/Academic rank", mediaType = "responseBody")
                    })
            ),
            @ApiResponse(
                    code = 400,
                    message = "Academic rank with such numeric rank already exists",
                    response = Response.class,
                    examples = @Example({
                            @ExampleProperty(value = "item is not unique (already exists)", mediaType = "message"),
                            @ExampleProperty(value = "1600682630711", mediaType = "timeStamp"),
                            @ExampleProperty(value = "", mediaType = "responseBody")
                    })
            ),
            @ApiResponse(
                    code = 400,
                    message = "Provided academic rank parameters violate one or more validation constraints",
                    response = Response.class,
                    examples = @Example({
                            @ExampleProperty(value = "invalid input", mediaType = "message"),
                            @ExampleProperty(value = "1600682630711", mediaType = "timeStamp"),
                            @ExampleProperty(value = "[numeric rank cannot be null, rank name cannot be longer than 45 characters]", mediaType = "responseBody")
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
    public ResponseEntity<Response> addEntity(
            @ApiParam(value = "Academic rank parameters in json format",
                    required = true,
                    type = "#/definitions/Academic rank",
                    examples = @Example({
                            @ExampleProperty(value = "Professor", mediaType = "rankName"),
                            @ExampleProperty(value = "1", mediaType = "numericRank")
                    }))
            @RequestBody @Valid AcademicRankDTO academicRankToAdd, BindingResult bindingResult) {
        return service.addEntity(academicRankToAdd, bindingResult);
    }

    @PutMapping("/ranks")
    @ApiOperation(value = "Update an academic rank", notes = "Provide an academic rank in json format", produces = "application/json", consumes = "application/json")
    @ApiResponses({
            @ApiResponse(
                    code = 200,
                    message = "Academic rank updated successfully",
                    response = Response.class,
                    examples = @Example({
                            @ExampleProperty(value = "item updated successfully", mediaType = "message"),
                            @ExampleProperty(value = "1600682630711", mediaType = "timeStamp"),
                            @ExampleProperty(value = "#/definitions/Academic rank", mediaType = "responseBody")
                    })
            ),
            @ApiResponse(
                    code = 404,
                    message = "Academic rank with such id is not found",
                    response = Response.class,
                    examples = @Example({
                            @ExampleProperty(value = "item of requested type with such id not found", mediaType = "message"),
                            @ExampleProperty(value = "1600682630711", mediaType = "timeStamp"),
                            @ExampleProperty(value = "", mediaType = "responseBody")
                    })
            ),
            @ApiResponse(
                    code = 400,
                    message = "Provided academic rank parameters violate one or more validation constraints",
                    response = Response.class,
                    examples = @Example({
                            @ExampleProperty(value = "invalid input", mediaType = "message"),
                            @ExampleProperty(value = "1600682630711", mediaType = "timeStamp"),
                            @ExampleProperty(value = "[numeric rank cannot be null, rank name cannot be longer than 45 characters]", mediaType = "responseBody")
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
    public ResponseEntity<Response> updateEntity(
            @ApiParam(value = "Academic rank parameters in json format",
                    required = true,
                    type = "#/definitions/Academic rank",
                    examples = @Example({
                            @ExampleProperty(value = "Professor", mediaType = "rankName"),
                            @ExampleProperty(value = "1", mediaType = "numericRank")
                    }))
            @RequestBody @Valid AcademicRankDTO academicRankToUpdate, BindingResult bindingResult) {
        return service.updateEntity(academicRankToUpdate, bindingResult);
    }

    @DeleteMapping("/ranks/{rankId}")
    @ApiOperation(value = "Delete an academic rank", notes = "Provide a numeric rank to delete specific academic rank", produces = "application/json")
    @ApiResponses({
            @ApiResponse(
                    code = 200,
                    message = "Academic rank deleted successfully",
                    response = Response.class,
                    examples = @Example({
                            @ExampleProperty(value = "item deleted successfully", mediaType = "message"),
                            @ExampleProperty(value = "1600682630711", mediaType = "timeStamp"),
                            @ExampleProperty(value = "", mediaType = "responseBody")
                    })
            ),
            @ApiResponse(
                    code = 404,
                    message = "Academic rank with such id is not found",
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
    public ResponseEntity<Response> deleteEntity(
            @ApiParam(value = "Numeric rank value for the academic rank you need to delete", required = true, type = "int32")
            @PathVariable Integer rankId) {
        return service.deleteEntity(String.valueOf(rankId));
    }

    @GetMapping("/ranks/search")
    @ApiOperation(value = "Get all academic ranks that meet the provided search criterion", notes = "Provide a search criterion to look up for specific academic ranks", produces = "application/json")
    @ApiResponses({
            @ApiResponse(
                    code = 200,
                    message = "List of found ranks fetched successfully",
                    response = Response.class,
                    examples = @Example({
                            @ExampleProperty(value = "list of all items of requested type containing search criterion", mediaType = "message"),
                            @ExampleProperty(value = "1600682630711", mediaType = "timeStamp"),
                            @ExampleProperty(value = "[#/definitions/Academic rank]", mediaType = "responseBody")
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
    public ResponseEntity<Response> searchForAcademicRanks(
            @ApiParam(value = "Search criterion (numeric rank or rank name)", required = true, type = "string/int32")
            @RequestParam String param) {
        return service.searchForEntities(param);
    }
}
