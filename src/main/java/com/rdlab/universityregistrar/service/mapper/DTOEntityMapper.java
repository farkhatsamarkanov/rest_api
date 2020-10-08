package com.rdlab.universityregistrar.service.mapper;

import org.mapstruct.Named;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public interface DTOEntityMapper<E, D> {

    E dtoToEntity(D dto);

    D entityToDto(E entity);

    default List<D> entityListToDtoList(List<E> entityList) {
        List<D> dtoList = new ArrayList<>();
        if (!entityList.isEmpty()) {
            entityList.forEach(entity -> {
                dtoList.add(entityToDto(entity));
            });
        }
        return dtoList;
    }

    @Named("dateToLong")
    public default long dateToLong(Date date) {
        return date.getTime();
    }

    @Named("longToDate")
    public default Date longToDate(long dateOfBirthLong) {
        return new Date(dateOfBirthLong);
    }
}
