package com.rdlab.universityregistrar.controller;

import com.rdlab.universityregistrar.controller.response.Response;
import com.rdlab.universityregistrar.service.ServiceFunctionality;
import com.rdlab.universityregistrar.service.dto.StudentDTO;
import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * REST Api controller implementation for operation with {@link StudentDTO}.
 * Performs input data basic (JSR-303) validation, puts existing validation constraints to {@link BindingResult} instance
 * and calls corresponding service methods necessary to perform requested operations.
 */
@RestController
@RequestMapping("/api")
@Api(tags = "Student API")
public class StudentController {
    @Autowired
    ServiceFunctionality<StudentDTO> service;

    @GetMapping("/students")
    @ApiOperation(value = "Get list of all students", produces = "application/json")
    @ApiResponses({
            @ApiResponse(
                    code = 200,
                    message = "List of students fetched successfully",
                    response = Response.class,
                    examples = @Example({
                            @ExampleProperty(value = "list of all available items of requested type", mediaType = "message"),
                            @ExampleProperty(value = "1600682630711", mediaType = "timeStamp"),
                            @ExampleProperty(value = "[#/definitions/Student]", mediaType = "responseBody")
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

    @GetMapping("/students/{studentId}")
    @ApiOperation(value = "Get student by id", notes = "Provide an id to look up specific student", produces = "application/json")
    @ApiResponses({
            @ApiResponse(
                    code = 200,
                    message = "Student fetched successfully",
                    response = Response.class,
                    examples = @Example({
                            @ExampleProperty(value = "item of requested type with id: 1", mediaType = "message"),
                            @ExampleProperty(value = "1600682630711", mediaType = "timeStamp"),
                            @ExampleProperty(value = "[#/definitions/Student]", mediaType = "responseBody")
                    }
                    )
            ),
            @ApiResponse(
                    code = 404,
                    message = "Student with such id is not found",
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
            @ApiParam(value = "ID value for the student you need to retrieve", required = true, type = "int32")
            @PathVariable Integer studentId) {
        return service.getEntity(String.valueOf(studentId));
    }

    @PostMapping("/students")
    @ApiOperation(value = "Add student", notes = "Provide a student in json format", produces = "application/json", consumes = "application/json")
    @ApiResponses({
            @ApiResponse(
                    code = 201,
                    message = "Student added successfully",
                    response = Response.class,
                    examples = @Example({
                            @ExampleProperty(value = "item successfully added", mediaType = "message"),
                            @ExampleProperty(value = "1600682630711", mediaType = "timeStamp"),
                            @ExampleProperty(value = "#/definitions/Student", mediaType = "responseBody")
                    }
                    )
            ),
            @ApiResponse(
                    code = 400,
                    message = "Student with such semester id already exists",
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
                    message = "Provided student parameters violate one or more validation constraints",
                    response = Response.class,
                    examples = @Example({
                            @ExampleProperty(value = "invalid input", mediaType = "message"),
                            @ExampleProperty(value = "1600682630711", mediaType = "timeStamp"),
                            @ExampleProperty(value = "[student name name cannot be longer than 45 characters, student date of birth cannot be in the future]", mediaType = "responseBody")
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
            @ApiParam(value = "Student parameters in json format",
                    required = true,
                    type = "#/definitions/Student",
                    examples = @Example({
                            @ExampleProperty(value = "John Doe", mediaType = "studentName"),
                            @ExampleProperty(value = "1995-05-05", mediaType = "dateOfBirth")
                    }))
            @RequestBody @Valid StudentDTO studentToAdd, BindingResult bindingResult) {
        return service.addEntity(studentToAdd, bindingResult);
    }

    @PutMapping("/students")
    @ApiOperation(value = "Update a student information", notes = "Provide a student information in json format", produces = "application/json", consumes = "application/json")
    @ApiResponses({
            @ApiResponse(
                    code = 200,
                    message = "Student updated successfully",
                    response = Response.class,
                    examples = @Example({
                            @ExampleProperty(value = "item updated successfully", mediaType = "message"),
                            @ExampleProperty(value = "1600682630711", mediaType = "timeStamp"),
                            @ExampleProperty(value = "#/definitions/Student", mediaType = "responseBody")
                    }
                    )
            ),
            @ApiResponse(
                    code = 404,
                    message = "Student with such id is not found",
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
                    message = "Provided student parameters violate one or more validation constraints",
                    response = Response.class,
                    examples = @Example({
                            @ExampleProperty(value = "invalid input", mediaType = "message"),
                            @ExampleProperty(value = "1600682630711", mediaType = "timeStamp"),
                            @ExampleProperty(value = "[student name name cannot be longer than 45 characters, student date of birth cannot be in the future]", mediaType = "responseBody")
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
            @ApiParam(value = "Student parameters in json format",
                    required = true,
                    type = "#/definitions/Student",
                    examples = @Example({
                            @ExampleProperty(value = "1", mediaType = "studentId"),
                            @ExampleProperty(value = "John Doe", mediaType = "studentName"),
                            @ExampleProperty(value = "1995-05-05", mediaType = "dateOfBirth")
                    }))
            @RequestBody @Valid StudentDTO studentToUpdate, BindingResult bindingResult) {
        return service.updateEntity(studentToUpdate, bindingResult);
    }

    @DeleteMapping("/students/{studentId}")
    @ApiOperation(value = "Delete a student", notes = "Provide an id to delete specific student", produces = "application/json")
    @ApiResponses({
            @ApiResponse(
                    code = 200,
                    message = "Student deleted successfully",
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
                    message = "Student with such id is not found",
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
    public ResponseEntity<Response> deleteStudent(
            @ApiParam(value = "ID value for the student you need to delete", required = true, type = "int32")
            @PathVariable Integer studentId) {
        return service.deleteEntity(String.valueOf(studentId));
    }

    @GetMapping("/students/search")
    @ApiOperation(value = "Get all students that meet the provided search criterion", notes = "Provide a search criterion to look up for specific students", produces = "application/json")
    @ApiResponses({
            @ApiResponse(
                    code = 200,
                    message = "List of found students fetched successfully",
                    response = Response.class,
                    examples = @Example({
                            @ExampleProperty(value = "list of all items of requested type containing search criterion", mediaType = "message"),
                            @ExampleProperty(value = "1600682630711", mediaType = "timeStamp"),
                            @ExampleProperty(value = "[#/definitions/Student]", mediaType = "responseBody")
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
    public ResponseEntity<Response> searchForStudents(
            @ApiParam(value = "Search criterion (student name)", required = true, type = "string")
            @RequestParam String param) {
        return service.searchForEntities(param);
    }
}
