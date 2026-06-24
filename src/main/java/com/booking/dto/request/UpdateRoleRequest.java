package com.booking.dto.request;

import com.booking.enums.Role;
import lombok.Data;

@Data
public class UpdateRoleRequest {

    private Role role;
}