package com.example.taskify.mapper;

import com.example.taskify.controller.form.CreateNewUserForm;
import com.example.taskify.controller.form.LoginForm;
import com.example.taskify.controller.form.RegistrateOrganizationForm;
import com.example.taskify.controller.form.UserForm;
import com.example.taskify.domain.AppUser;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

public class UserMapper {
    public static AppUser mapUserFromCreateNewUserForm(CreateNewUserForm form) {
        return new AppUser(form.getFirstName(),
                           form.getLastName(),
                           form.getEmail(),
                           form.getPassword(),
                           form.getOrganization());
    }

    public static AppUser mapUserFromRegistrateOrganizationForm(RegistrateOrganizationForm form) {
        return new AppUser(form.getFirstName(),
                           form.getLastName(),
                           form.getEmail(),
                           form.getPassword(),
                           form.getName());
    }

    public static UserForm mapUserToUserForm(AppUser user) {
        return new UserForm(user.getId(),
                            user.getName(),
                            user.getLastName(),
                            user.getEmail());
    }

    public static UsernamePasswordAuthenticationToken mapPassAuthTokenFromLoginForm(LoginForm form) {
        return new UsernamePasswordAuthenticationToken(form.getEmail(),
                                                       form.getPassword());
    }
}
