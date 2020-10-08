package com.rdlab.universityregistrar.controller;

import com.rdlab.universityregistrar.controller.response.Response;
import com.rdlab.universityregistrar.service.ServiceFunctionality;
import com.rdlab.universityregistrar.service.dto.CourseDTO;
import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * REST Api controller implementation for operation with {@link CourseDTO}.
 * Performs input data basic (JSR-303) validation, puts existing validation constraints to {@link BindingResult} instance
 * and calls corresponding service methods necessary to perform requested operations.
 */
@RestController
@RequestMapping("/api")
@Api(tags = "Courses API")
public class CourseController {

    @Autowired
    ServiceFunctionality<CourseDTO> service;

    @GetMapping("/courses")
    @ApiOperation(value = "Get list of all courses", produces = "application/json")
    @ApiResponses({
            @ApiResponse(
                    code = 200,
                    message = "List of courses fetched successfully",
                    response = Response.class,
                    examples = @Example({
                            @ExampleProperty(value = "list of all available items of requested type", mediaType = "message"),
                            @ExampleProperty(value = "1600682630711", mediaType = "timeStamp"),
                            @ExampleProperty(value = "[#/definitions/Course]", mediaType = "responseBody")
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

    @GetMapping("/courses/{courseId}")
    @ApiOperation(value = "Get course by id", notes = "Provide an id to look up specific course", produces = "application/json")
    @ApiResponses({
            @ApiResponse(
                    code = 200,
                    message = "Course fetched successfully",
                    response = Response.class,
                    examples = @Example({
                            @ExampleProperty(value = "item of requested type with id: 1", mediaType = "message"),
                            @ExampleProperty(value = "1600682630711", mediaType = "timeStamp"),
                            @ExampleProperty(value = "[#/definitions/Course]", mediaType = "responseBody")
                    })
            ),
            @ApiResponse(
                    code = 404,
                    message = "Course with such id is not found",
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
            @ApiParam(value = "ID value for the course you need to retrieve", required = true, type = "int32")
            @PathVariable Integer courseId) {
        return service.getEntity(String.valueOf(courseId));
    }

    @PostMapping("/courses")
    @ApiOperation(value = "Add course", notes = "Provide a course in json format", produces = "application/json", consumes = "application/json")
    @ApiResponses({
            @ApiResponse(
                    code = 201,
                    message = "Course added successfully",
                    response = Response.class,
                    examples = @Example({
                            @ExampleProperty(value = "item successfully added", mediaType = "message"),
                            @ExampleProperty(value = "1600682630711", mediaType = "timeStamp"),
                            @ExampleProperty(value = "#/definitions/Course", mediaType = "responseBody")
                    })
            ),
            @ApiResponse(
                    code = 400,
                    message = "Course with such course title already exists",
                    response = Response.class,
                    examples = @Example({
                            @ExampleProperty(value = "item is not unique (already exists)", mediaType = "message"),
                            @ExampleProperty(value = "1600682630711", mediaType = "timeStamp"),
                            @ExampleProperty(value = "", mediaType = "responseBody")
                    })
            ),
            @ApiResponse(
                    code = 400,
                    message = "Provided course parameters violate one or more validation constraints",
                    response = Response.class,
                    examples = @Example({
                            @ExampleProperty(value = "invalid input", mediaType = "message"),
                            @ExampleProperty(value = "1600682630711", mediaType = "timeStamp"),
                            @ExampleProperty(value = "[course title cannot be empty, course title can contain only letters in range (a-z, A-Z), numbers from 0 to 9 and whitespaces]", mediaType = "responseBody")
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
            @ApiParam(value = "Course parameters in json format",
                    required = true,
                    type = "#/definitions/Course",
                    examples = @Example({
                            @ExampleProperty(value = "Computer Science 101", mediaType = "courseTitle"),
                            @ExampleProperty(value = "Prerequisites: Math", mediaType = "courseDescription")
                    }))
            @RequestBody @Valid CourseDTO courseToAdd, BindingResult bindingResult) {
        return service.addEntity(courseToAdd, bindingResult);
    }

    @PutMapping("/courses")
    @ApiOperation(value = "Update a course", notes = "Provide a course in json format", produces = "application/json", consumes = "application/json")
    @ApiResponses({
            @ApiResponse(
                    code = 200,
                    message = "Course updated successfully",
                    response = Response.class,
                    examples = @Example({
                            @ExampleProperty(value = "item updated successfully", mediaType = "message"),
                            @ExampleProperty(value = "1600682630711", mediaType = "timeStamp"),
                            @ExampleProperty(value = "#/definitions/Course", mediaType = "responseBody")
                    })
            ),
            @ApiResponse(
                    code = 404,
                    message = "Course with such id is not found",
                    response = Response.class,
                    examples = @Example({
                            @ExampleProperty(value = "item of requested type with such id not found", mediaType = "message"),
                            @ExampleProperty(value = "1600682630711", mediaType = "timeStamp"),
                            @ExampleProperty(value = "", mediaType = "responseBody")
                    })
            ),
            @ApiResponse(
                    code = 400,
                    message = "Provided course parameters violate one or more validation constraints",
                    response = Response.class,
                    examples = @Example({
                            @ExampleProperty(value = "invalid input", mediaType = "message"),
                            @ExampleProperty(value = "1600682630711", mediaType = "timeStamp"),
                            @ExampleProperty(value = "[course title cannot be empty, course title can contain only letters in range (a-z, A-Z), numbers from 0 to 9 and whitespaces]", mediaType = "responseBody")
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
            @ApiParam(value = "Course parameters in json format",
                    required = true,
                    type = "#/definitions/Course",
                    examples = @Example({
                            @ExampleProperty(value = "1", mediaType = "courseId"),
                            @ExampleProperty(value = "Computer Science 101", mediaType = "courseTitle"),
                            @ExampleProperty(value = "Prerequisites: Math", mediaType = "courseDescription")
                    }))
            @RequestBody @Valid CourseDTO courseToUpdate, BindingResult bindingResult) {
        return service.updateEntity(courseToUpdate, bindingResult);
    }

    @DeleteMapping("/courses/{courseId}")
    @ApiOperation(value = "Delete a course", notes = "Provide an id to delete specific course", produces = "application/json")
    @ApiResponses({
            @ApiResponse(
                    code = 200,
                    message = "Course deleted successfully",
                    response = Response.class,
                    examples = @Example({
                            @ExampleProperty(value = "item deleted successfully", mediaType = "message"),
                            @ExampleProperty(value = "1600682630711", mediaType = "timeStamp"),
                            @ExampleProperty(value = "", mediaType = "responseBody")
                    })
            ),
            @ApiResponse(
                    code = 404,
                    message = "Course with such id is not found",
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
    public ResponseEntity<Response> deleteCourse(
            @ApiParam(value = "ID value for the course you need to delete", required = true, type = "int32")
            @PathVariable Integer courseId) {
        return service.deleteEntity(String.valueOf(courseId));
    }

    @GetMapping("/courses/search")
    @ApiOperation(value = "Get all courses that meet the provided search criterion", notes = "Provide a search criterion to look up for specific courses", produces = "application/json")
    @ApiResponses({
            @ApiResponse(
                    code = 200,
                    message = "List of found courses fetched successfully",
                    response = Response.class,
                    examples = @Example({
                            @ExampleProperty(value = "list of all items of requested type containing search criterion", mediaType = "message"),
                            @ExampleProperty(value = "1600682630711", mediaType = "timeStamp"),
                            @ExampleProperty(value = "[#/definitions/Course]", mediaType = "responseBody")
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
    public ResponseEntity<Response> searchForCourses(
            @ApiParam(value = "Search criterion (course title or course description)", required = true, type = "string")
            @RequestParam String param) {
        return service.searchForEntities(param);
    }
}
