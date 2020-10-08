package com.rdlab.universityregistrar.model.entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.*;
import java.util.Objects;

/**
 * Entity representing record from schedule_of_classes database table
 */
@Getter
@Setter
@Builder
@NoArgsConstructor(force = true, access = AccessLevel.PUBLIC)
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@Entity
@Table(name = "schedule_of_classes",
        uniqueConstraints =
        @UniqueConstraint(columnNames = {"student_id", "course_id", "semester_id", "lecturer_id", "time", "location"}))
public class ScheduleEntry {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "entry_id")
    private Integer entryId;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id")
    private Student student;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lecturer_id")
    private Lecturer lecturer;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_id")
    private Course course;
    @Column(name = "time")
    private Long time;
    @Column(name = "location")
    private String location;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "semester_id",
            referencedColumnName = "semester_id")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Semester semester;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ScheduleEntry)) return false;
        ScheduleEntry that = (ScheduleEntry) o;
        return Objects.equals(entryId, that.entryId) &&
                Objects.equals(student, that.student) &&
                Objects.equals(lecturer, that.lecturer) &&
                Objects.equals(course, that.course) &&
                Objects.equals(time, that.time) &&
                Objects.equals(location, that.location) &&
                Objects.equals(semester, that.semester);
    }

    @Override
    public int hashCode() {
        return Objects.hash(entryId, student, lecturer, course, time, location, semester);
    }
}
