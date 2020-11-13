package userServiceModels;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public class UserDto implements Serializable {

    private long id;
    private String username;
    private String password;
    private String encryptedPassword;

}

