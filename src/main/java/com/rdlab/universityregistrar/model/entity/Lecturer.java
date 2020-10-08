package com.rdlab.universityregistrar.model.entity;

import lombok.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.*;
import java.util.Objects;
import java.util.Set;

/**
 * Entity representing record from lecturers database table
 */
@Getter
@Setter
@Builder
@NoArgsConstructor(force = true, access = AccessLevel.PUBLIC)
@AllArgsConstructor
@Entity
@Table(name = "lecturers")
public class Lecturer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "lecturer_id")
    private Integer lecturerId;
    @Column(name = "name")
    private String lecturerName;
    @Column(name = "date_of_birth")
    private Long dateOfBirth;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "numeric_academic_rank",
            referencedColumnName = "numeric_rank"
    )
    @OnDelete(action = OnDeleteAction.CASCADE)
    private AcademicRank numericAcademicRank;
    @OneToMany(mappedBy = "lecturer", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Set<ScheduleEntry> scheduleEntries;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Lecturer)) return false;
        Lecturer lecturer = (Lecturer) o;
        return Objects.equals(lecturerId, lecturer.lecturerId) &&
                Objects.equals(lecturerName, lecturer.lecturerName) &&
                Objects.equals(dateOfBirth, lecturer.dateOfBirth) &&
                Objects.equals(numericAcademicRank, lecturer.numericAcademicRank);
    }

    @Override
    public int hashCode() {
        return Objects.hash(lecturerId, lecturerName, dateOfBirth, numericAcademicRank);
    }
}
