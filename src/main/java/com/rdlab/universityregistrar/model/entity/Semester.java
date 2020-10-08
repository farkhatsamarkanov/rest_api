package com.rdlab.universityregistrar.model.entity;

import lombok.*;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Objects;
import java.util.Set;

/**
 * Entity representing record from semester database table
 */
@Getter
@Setter
@Builder
@NoArgsConstructor(force = true, access = AccessLevel.PUBLIC)
@AllArgsConstructor
@Entity
@Table(name = "semesters")
public class Semester implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "entry_id")
    private Integer entryId;
    @Column(name = "semester_id", unique = true)
    private String semesterId;
    @Column(name = "name")
    private String semesterName;
    @Column(name = "year")
    private Integer semesterYear;
    @Column(name = "start_time")
    private Long semesterStartTime;
    @Column(name = "end_time")
    private Long semesterEndTime;
    @OneToMany(mappedBy = "semester", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<ScheduleEntry> scheduleEntries;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Semester)) return false;
        Semester semester = (Semester) o;
        return Objects.equals(entryId, semester.entryId) &&
                Objects.equals(semesterId, semester.semesterId) &&
                Objects.equals(semesterName, semester.semesterName) &&
                Objects.equals(semesterYear, semester.semesterYear) &&
                Objects.equals(semesterStartTime, semester.semesterStartTime) &&
                Objects.equals(semesterEndTime, semester.semesterEndTime);
    }

    @Override
    public int hashCode() {
        return Objects.hash(entryId, semesterId, semesterName, semesterYear, semesterStartTime, semesterEndTime);
    }
}
