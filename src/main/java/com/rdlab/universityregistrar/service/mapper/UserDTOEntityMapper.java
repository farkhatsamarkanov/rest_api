package com.rdlab.universityregistrar.service.mapper;

import com.rdlab.universityregistrar.model.entity.Student;
import com.rdlab.universityregistrar.model.entity.User;
import com.rdlab.universityregistrar.service.dto.UserDTO;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserDTOEntityMapper extends DTOEntityMapper<User, UserDTO> {
    @Override
    default User dtoToEntity(UserDTO dto) {
        return User.builder()
                .userId(dto.getUserId())
                .login(dto.getLogin())
                .password(dto.getPassword())
                .isActive(dto.getIsActive())
                .student(Student.builder()
                        .studentId(dto.getStudentId())
                        .build())
                .build();
    }

    @Override
    default UserDTO entityToDto(User entity) {
        return UserDTO.builder()
                .userId(entity.getUserId())
                .login(entity.getLogin())
                .password(entity.getPassword())
                .isActive(entity.getIsActive())
                .studentId(entity.getStudent().getStudentId())
                .build();
    }
}
