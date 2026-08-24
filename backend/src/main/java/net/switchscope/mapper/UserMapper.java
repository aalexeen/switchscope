package net.switchscope.mapper;

import net.switchscope.model.User;
import net.switchscope.to.UserTo;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(config = MapStructConfig.class)
public interface UserMapper extends BaseMapper<User, UserTo> {

    @Mapping(target = "id", ignore = true) // id is server-generated; never taken from the request
    @Mapping(target = "email", expression = "java(to.getEmail().toLowerCase())")
    // The role is not mapped here any more: it is a row now, and a mapper cannot read one.
    // UserService.create grants it, which is also where "whatever creating a user comes to mean"
    // already lives. Either way it does not come from the request.
    @Mapping(target = "roles", ignore = true)
    @Override
    User toEntity(UserTo to);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "email", expression = "java(to.getEmail().toLowerCase())")
    @Override
    User updateFromTo(@MappingTarget User entity, UserTo to);
}

