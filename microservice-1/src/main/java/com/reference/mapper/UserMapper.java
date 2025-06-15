package com.reference.mapper;

import com.reference.data.model.UserEntity;
import com.reference.domain.User;
import com.reference.model.UserAPI;
import org.mapstruct.Mapper;

@Mapper
public interface UserMapper {
	User apiToDomain(UserAPI userAPI);
	UserAPI domainToAPI(User user);
	UserEntity domainToEntity(User user);
	User entityToDomain(UserEntity userEntity);
}
