import React from "react";
import {getToken, getEmail, getLastName, getName, getOrganizationName} from "../utils/Common";
import profile_icon from "./profile_icon.png";
import jwtDecode from "jwt-decode";

export const Profile = () => {
    return (
        <div className="profile">
            <div>
                <hr/>
                <div align="center">
                    <h3>{getName()} {getLastName()}</h3>
                </div>
                <hr/>
            </div>
            <div className="row pt-4">
                <div className="col-lg-6" align="center">
                    <div className="mb-3">
                        <img src={profile_icon} height="275px" alt="profile icon"/>
                    </div>
                </div>
                <div className="col-lg-6 mt-auto mb-auto" >
                    <div className="mb-3 user-info">
                        <ul>
                            <li className="pb-2">
                                <h5>First name: <strong>{getName()}</strong></h5>
                            </li>
                            <li className="pb-2">
                                <h5>Last name: <strong>{getLastName()}</strong></h5>
                            </li>
                            <li className="pb-2">
                                <h5>Email: <strong>{getEmail()}</strong></h5>
                            </li>
                            <li className="pb-2">
                                <h5>Organization: <strong>{getOrganizationName()}</strong></h5>
                            </li>
                            <li>
                                <h5>Roles: <strong>{jwtDecode(getToken()).roles}</strong></h5>
                            </li>
                        </ul>
                    </div>
                </div>
            </div>
        </div>
    )
}