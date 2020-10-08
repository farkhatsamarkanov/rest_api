package com.rdlab.universityregistrar.model.entity;

import lombok.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.*;
import java.util.Objects;
import java.util.Set;

/**
 * Entity representing record from courses database table
 */
@Getter
@Setter
@Builder
@NoArgsConstructor(force = true, access = AccessLevel.PUBLIC)
@AllArgsConstructor
@Entity
@Table(name = "courses")
public class Course {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "course_id")
    private Integer courseId;
    @Column(name = "course_title", unique = true)
    private String courseTitle;
    @Column(name = "course_description")
    private String courseDescription;
    @OneToMany(mappedBy = "course", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Set<ScheduleEntry> scheduleEntries;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Course)) return false;
        Course course = (Course) o;
        return Objects.equals(courseId, course.courseId) &&
                Objects.equals(courseTitle, course.courseTitle) &&
                Objects.equals(courseDescription, course.courseDescription);
    }

    @Override
    public int hashCode() {
        return Objects.hash(courseId, courseTitle, courseDescription);
    }
}