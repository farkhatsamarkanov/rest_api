package com.rdlab.universityregistrar.model.entity;

import lombok.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.*;
import java.util.Objects;

/**
 * Entity representing record from users database table
 */
@Getter
@Setter
@Builder
@NoArgsConstructor(force = true, access = AccessLevel.PUBLIC)
@AllArgsConstructor
@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "entry_id")
    private Integer userId;
    @Column(name = "login", unique = true)
    private String login;
    @Column(name = "password")
    private String password;
    @Column(name = "is_active")
    private Boolean isActive;
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Student student;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof User)) return false;
        User user = (User) o;
        return Objects.equals(userId, user.userId) &&
                Objects.equals(login, user.login) &&
                Objects.equals(password, user.password) &&
                Objects.equals(isActive, user.isActive) &&
                Objects.equals(student, user.student);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId, login, password, isActive, student);
    }
}
