package com.manager.schoolmateapi.mappers;

import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import com.manager.schoolmateapi.users.dto.CreateUserDto;
import com.manager.schoolmateapi.users.dto.EditPasswordDto;
import com.manager.schoolmateapi.users.dto.EditUserDto;
import com.manager.schoolmateapi.users.models.User;

@Mapper(componentModel = "spring")
public interface UserDtoMapper {

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "active", ignore = true)
    @Mapping(target = "complaints", ignore = true)
    @Mapping(target = "assignedComplaints", ignore = true)
    @Mapping(target = "documents", ignore = true)
    @Mapping(target = "tags", ignore = true)
    User createUserDtoToUser(CreateUserDto createUserDto);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "password", ignore = true)
    @Mapping(target = "complaints", ignore = true)
    @Mapping(target = "assignedComplaints", ignore = true)
    @Mapping(target = "documents", ignore = true)
    @Mapping(target = "tags", ignore = true)
    void updateUserFromDto(EditUserDto editUserDto, @MappingTarget User user);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "role", ignore = true)
    @Mapping(target = "active", ignore = true)
    @Mapping(target = "email", ignore = true)
    @Mapping(target = "firstName", ignore = true)
    @Mapping(target = "lastName", ignore = true)
    @Mapping(target = "complaints", ignore = true)
    @Mapping(target = "assignedComplaints", ignore = true)
    @Mapping(target = "documents", ignore = true)
    @Mapping(target = "tags", ignore = true)
    void updatePasswordFromDto(EditPasswordDto editPasswordDto, @MappingTarget User user);
}
