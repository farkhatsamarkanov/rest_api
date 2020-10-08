package com.rdlab.universityregistrar.controller;

import com.rdlab.universityregistrar.controller.response.Response;
import com.rdlab.universityregistrar.service.ServiceFunctionality;
import com.rdlab.universityregistrar.service.dto.ScheduleEntryDTO;
import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * REST Api controller implementation for operation with {@link ScheduleEntryDTO}.
 * Performs input data basic (JSR-303) validation, puts existing validation constraints to {@link BindingResult} instance
 * and calls corresponding service methods necessary to perform requested operations.
 */
@RestController
@RequestMapping("/api")
@Api(tags = "Schedule of classes API")
public class ScheduleEntryController {
    @Autowired
    ServiceFunctionality<ScheduleEntryDTO> service;

    @GetMapping("/schedules")
    @ApiOperation(value = "Get list of all schedule entries", produces = "application/json")
    @ApiResponses({
            @ApiResponse(
                    code = 200,
                    message = "List of schedule entries fetched successfully",
                    response = Response.class,
                    examples = @Example({
                            @ExampleProperty(value = "list of all available items of requested type", mediaType = "message"),
                            @ExampleProperty(value = "1600682630711", mediaType = "timeStamp"),
                            @ExampleProperty(value = "[#/definitions/Schedule entry]", mediaType = "responseBody")
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

    @GetMapping("/schedules/{scheduleId}")
    @ApiOperation(value = "Get schedule entry by id", notes = "Provide an id to look up specific schedule entry", produces = "application/json")
    @ApiResponses({
            @ApiResponse(
                    code = 200,
                    message = "Schedule entry fetched successfully",
                    response = Response.class,
                    examples = @Example({
                            @ExampleProperty(value = "item of requested type with id: 1", mediaType = "message"),
                            @ExampleProperty(value = "1600682630711", mediaType = "timeStamp"),
                            @ExampleProperty(value = "[#/definitions/Schedule entry]", mediaType = "responseBody")
                    })
            ),
            @ApiResponse(
                    code = 404,
                    message = "Schedule entry with such id is not found",
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
            @ApiParam(value = "ID value for the schedule entry you need to retrieve", required = true, type = "int32")
            @PathVariable Integer scheduleId) {
        return service.getEntity(String.valueOf(scheduleId));
    }

    @PostMapping("/schedules")
    @ApiOperation(value = "Add schedule entry", notes = "Provide a schedule entry in json format", produces = "application/json", consumes = "application/json")
    @ApiResponses({
            @ApiResponse(
                    code = 201,
                    message = "Schedule entry added successfully",
                    response = Response.class,
                    examples = @Example({
                            @ExampleProperty(value = "item successfully added", mediaType = "message"),
                            @ExampleProperty(value = "1600682630711", mediaType = "timeStamp"),
                            @ExampleProperty(value = "#/definitions/Schedule entry", mediaType = "responseBody")
                    })
            ),
            @ApiResponse(
                    code = 400,
                    message = "Schedule entry with such parameters already exists",
                    response = Response.class,
                    examples = @Example({
                            @ExampleProperty(value = "item is not unique (already exists)", mediaType = "message"),
                            @ExampleProperty(value = "1600682630711", mediaType = "timeStamp"),
                            @ExampleProperty(value = "", mediaType = "responseBody")
                    })
            ),
            @ApiResponse(
                    code = 400,
                    message = "Provided schedule entry parameters violate one or more validation constraints",
                    response = Response.class,
                    examples = @Example({
                            @ExampleProperty(value = "invalid input", mediaType = "message"),
                            @ExampleProperty(value = "1600682630711", mediaType = "timeStamp"),
                            @ExampleProperty(value = "[studentId must not be empty, location cannot be empty]", mediaType = "responseBody")
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
            @ApiParam(value = "Course parameters in json format",
                    required = true,
                    type = "#/definitions/Schedule entry",
                    examples = @Example({
                            @ExampleProperty(value = "1", mediaType = "studentId"),
                            @ExampleProperty(value = "3", mediaType = "courseId"),
                            @ExampleProperty(value = "1", mediaType = "lecturerId"),
                            @ExampleProperty(value = "FAL2020", mediaType = "semesterId"),
                            @ExampleProperty(value = "Room A1", mediaType = "location"),
                            @ExampleProperty(value = "2020-05-05 09:10:00", mediaType = "time")
                    }))
            @RequestBody @Valid ScheduleEntryDTO scheduleEntryToAdd, BindingResult bindingResult) {
        return service.addEntity(scheduleEntryToAdd, bindingResult);
    }

    @PutMapping("/schedules")
    @ApiOperation(value = "Update a schedule entry", notes = "Provide a schedule entry in json format", produces = "application/json", consumes = "application/json")
    @ApiResponses({
            @ApiResponse(
                    code = 200,
                    message = "Schedule entry updated successfully",
                    response = Response.class,
                    examples = @Example({
                            @ExampleProperty(value = "item updated successfully", mediaType = "message"),
                            @ExampleProperty(value = "1600682630711", mediaType = "timeStamp"),
                            @ExampleProperty(value = "#/definitions/Schedule entry", mediaType = "responseBody")
                    }
                    )
            ),
            @ApiResponse(
                    code = 404,
                    message = "Schedule entry with such id is not found",
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
                    message = "Provided schedule entry parameters violate one or more validation constraints",
                    response = Response.class,
                    examples = @Example({
                            @ExampleProperty(value = "invalid input", mediaType = "message"),
                            @ExampleProperty(value = "1600682630711", mediaType = "timeStamp"),
                            @ExampleProperty(value = "[studentId must not be empty, location cannot be empty]", mediaType = "responseBody")
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
            @ApiParam(value = "Schedule entry parameters in json format",
                    required = true,
                    type = "#/definitions/Schedule entry",
                    examples = @Example({
                            @ExampleProperty(value = "9", mediaType = "entryId"),
                            @ExampleProperty(value = "1", mediaType = "studentId"),
                            @ExampleProperty(value = "3", mediaType = "courseId"),
                            @ExampleProperty(value = "1", mediaType = "lecturerId"),
                            @ExampleProperty(value = "FAL2020", mediaType = "semesterId"),
                            @ExampleProperty(value = "Room A1", mediaType = "location"),
                            @ExampleProperty(value = "2020-05-05 09:10:00", mediaType = "time")
                    }))
            @RequestBody @Valid ScheduleEntryDTO scheduleEntryToUpdate, BindingResult bindingResult) {
        return service.updateEntity(scheduleEntryToUpdate, bindingResult);
    }

    @DeleteMapping("/schedules/{scheduleId}")
    @ApiOperation(value = "Delete a schedule entry", notes = "Provide an id to delete specific Schedule entry", produces = "application/json")
    @ApiResponses({
            @ApiResponse(
                    code = 200,
                    message = "Schedule entry deleted successfully",
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
                    message = "Schedule entry with such id is not found",
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
    public ResponseEntity<Response> deleteLecturer(
            @ApiParam(value = "ID value for the schedule entry you need to delete", required = true, type = "int32")
            @PathVariable Integer scheduleId) {
        return service.deleteEntity(String.valueOf(scheduleId));
    }

    @GetMapping("/schedules/search")
    @ApiOperation(value = "Get all schedule entries that meet the provided search criterion", notes = "Provide a search criterion to look up for specific schedule entry", produces = "application/json")
    @ApiResponses({
            @ApiResponse(
                    code = 200,
                    message = "List of found schedule entries fetched successfully",
                    response = Response.class,
                    examples = @Example({
                            @ExampleProperty(value = "list of all items of requested type containing search criterion", mediaType = "message"),
                            @ExampleProperty(value = "1600682630711", mediaType = "timeStamp"),
                            @ExampleProperty(value = "[#/definitions/Schedule entry]", mediaType = "responseBody")
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
    public ResponseEntity<Response> searchForLecturers(
            @ApiParam(value = "Search criterion (course id, semester id or location)", required = true, type = "string/int32")
            @RequestParam String param) {
        return service.searchForEntities(param);
    }
}
