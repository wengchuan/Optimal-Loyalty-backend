package com.optimal.loyalty.Optimal.Loyalty.service;

import com.optimal.loyalty.Optimal.Loyalty.dto.UserDTO;
import com.optimal.loyalty.Optimal.Loyalty.dto.UserLoginDTO;

public interface AuthenticateService {
     UserDTO authenticate(UserLoginDTO userLoginDTO);

}
