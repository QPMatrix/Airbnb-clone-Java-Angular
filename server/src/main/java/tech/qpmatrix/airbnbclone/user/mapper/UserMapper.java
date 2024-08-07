package tech.qpmatrix.airbnbclone.user.mapper;

import org.mapstruct.Mapper;
import tech.qpmatrix.airbnbclone.user.application.dto.ReadUserDTO;
import tech.qpmatrix.airbnbclone.user.domain.Authority;
import tech.qpmatrix.airbnbclone.user.domain.User;

@Mapper(componentModel = "spring")
public interface UserMapper {

    ReadUserDTO readUserDTOToUser(User user);
    default String mapAuthoritiesToString(Authority authority) {
        return authority.getName();
    }
}
