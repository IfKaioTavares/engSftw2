package com.centroSer.app.persistent.converters;

import com.centroSer.app.persistent.entities.enums.UserRole;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class UserRoleConverter implements AttributeConverter<UserRole, Integer> {

    @Override
    public Integer convertToDatabaseColumn(UserRole userRole) {
        return userRole.getId();
    }

    @Override
    public UserRole convertToEntityAttribute(Integer id) {
        for (UserRole role : UserRole.values()) {
            if (role.getId() == id) {
                return role;
            }
        }
        throw new IllegalArgumentException("Unknown id: " + id);
    }
}
