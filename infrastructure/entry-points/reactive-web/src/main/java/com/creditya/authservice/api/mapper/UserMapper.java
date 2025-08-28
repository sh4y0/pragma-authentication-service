package com.creditya.authservice.api.mapper;

import com.creditya.authservice.api.dto.response.TokenDTO;
import com.creditya.authservice.api.dto.request.UserSignUpRequestDTO;
import com.creditya.authservice.model.user.User;
import com.creditya.authservice.model.user.UserToken;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface UserMapper {

    User dtoSignUpToDomain(UserSignUpRequestDTO dto);
    TokenDTO domainToDto(UserToken userToken);
}
