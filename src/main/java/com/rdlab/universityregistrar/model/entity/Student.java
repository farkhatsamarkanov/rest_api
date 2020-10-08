package com.rdlab.universityregistrar.model.entity;

import lombok.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.*;
import java.util.Objects;
import java.util.Set;

/**
 * Entity representing record from students database table
 */
@Getter
@Setter
@Builder
@NoArgsConstructor(force = true, access = AccessLevel.PUBLIC)
@AllArgsConstructor
@Entity
@Table(name = "students")
public class Student {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "student_id")
    private Integer studentId;
    @Column(name = "name")
    private String studentName;
    @Column(name = "date_of_birth")
    private Long dateOfBirth;
    @OneToOne(mappedBy = "student", fetch = FetchType.LAZY)
    private User user;
    @OneToMany(mappedBy = "student", cascade = CascadeType.REMOVE, fetch = FetchType.LAZY)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Set<ScheduleEntry> scheduleEntries;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Student)) return false;
        Student student = (Student) o;
        return Objects.equals(studentId, student.studentId) &&
                Objects.equals(studentName, student.studentName) &&
                Objects.equals(dateOfBirth, student.dateOfBirth);
    }

    @Override
    public int hashCode() {
        return Objects.hash(studentId, studentName, dateOfBirth);
    }
}
