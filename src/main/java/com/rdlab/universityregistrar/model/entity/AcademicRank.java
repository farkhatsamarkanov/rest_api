package com.rdlab.universityregistrar.model.entity;

import lombok.*;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Objects;
import java.util.Set;

/**
 * Entity representing record from academic_ranks database table
 */
@Getter
@Setter
@Builder
@NoArgsConstructor(force = true, access = AccessLevel.PUBLIC)
@AllArgsConstructor
@Entity
@Table(name = "academic_ranks")
public class AcademicRank implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "entry_id")
    private Integer rankId;
    @Column(name = "numeric_rank", unique = true)
    private Integer numericRank;
    @Column(name = "name")
    private String rankName;
    @OneToMany(mappedBy = "numericAcademicRank", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<Lecturer> lecturersWithThisRank;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof AcademicRank)) return false;
        AcademicRank that = (AcademicRank) o;
        return Objects.equals(rankId, that.rankId) &&
                Objects.equals(numericRank, that.numericRank) &&
                Objects.equals(rankName, that.rankName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(rankId, numericRank, rankName);
    }
}
