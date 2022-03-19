package com.example.taskify.mapper;

import com.example.taskify.controller.form.RegistrateOrganizationForm;
import com.example.taskify.domain.Organization;

public class OrganizationMapper {
    public static Organization mapOrgFromRegistrateOrganizationForm(RegistrateOrganizationForm form) {
        return new Organization(form.getName(),
                                form.getPhoneNumber(),
                                form.getAddress());
    }
}
