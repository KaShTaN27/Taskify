import React, {useState} from "react";
import {BASE_URL, saveToken} from "../utils/Common";
import axios from "axios";
import {useNavigate} from "react-router-dom";
import {TextField} from "@mui/material";

export const Registration = () => {
    const [organizationName, setOrganizationName] = useState('');
    const [phoneNumber, setPhoneNumber] = useState('');
    const [address, setAddress] = useState('');
    const [firstName, setFirstName] = useState('');
    const [lastName, setLastName] = useState('');
    const [email, setEmail] = useState('');
    const [password, setPassword] = useState('');
    const navigate = useNavigate();


    const handleRegistration = () => {
        axios.post(BASE_URL + "/api/auth/signup", {
            name: organizationName,
            phoneNumber: phoneNumber,
            address: address,
            firstName: firstName,
            lastName: lastName,
            email: email,
            password: password
        }).then(response => {
            console.log('Registration response >>> ', response)
            axios.post(BASE_URL + "/api/auth/login", null, {
                params: {
                    username: email,
                    password: password
                },
                headers: {
                    'Content-Type': 'application/x-www-form-urlencoded'
                }
            }).then(res => {
                console.log('Registration/login response >>> ', res)
                saveToken(res.data.token)
                navigate('/tasks');
            }).catch(err => {
                console.log('Registration/login error >>> ', err)
            })
        }).catch(error => {
            console.log('Registration error >>> ', error)
        })
    }

    return (
        <div className="row m-auto">
            <div className="col-lg-6 mt-5" align="center">
                <img src="https://account.bulletprofit.com/upload/auth/login.png" alt="sign up illustration"
                     width="600px"/>
            </div>
            <div className="col-lg-6">
                <div className="signup-page">
                    <div align="center" className="pb-1">
                        <h3>Welcome to the registration page!</h3>
                        <hr/>
                    </div>
                    <form>
                        <TextField
                            variant="standard"
                            label="Name of organization"
                            fullWidth={true}
                            value={organizationName}
                            onChange={e => setOrganizationName(e.target.value)}
                            className="mb-2"
                        />
                        <TextField
                            variant="standard"
                            label="Phone number"
                            fullWidth={true}
                            value={phoneNumber}
                            onChange={e => setPhoneNumber(e.target.value)}
                            className="mb-2"
                        />
                        <TextField
                            variant="standard"
                            label="Address"
                            fullWidth={true}
                            value={address}
                            onChange={e => setAddress(e.target.value)}
                            className="mb-2"
                        />
                        <div className="row">
                            <div className="col-lg-6">
                                <TextField
                                    variant="standard"
                                    label="First name"
                                    fullWidth={true}
                                    value={firstName}
                                    onChange={e => setFirstName(e.target.value)}
                                    className="mb-2"
                                />
                            </div>
                            <div className="col-lg-6">
                                <TextField
                                    variant="standard"
                                    label="Last name"
                                    fullWidth={true}
                                    value={lastName}
                                    onChange={e => setLastName(e.target.value)}
                                    className="mb-2"
                                />
                            </div>
                        </div>
                        <TextField
                            variant="standard"
                            type="email"
                            label="Email"
                            fullWidth={true}
                            value={email}
                            onChange={e => setEmail(e.target.value)}
                            className="mb-2"
                        />
                        <TextField
                            variant="standard"
                            type="password"
                            label="Password"
                            fullWidth={true}
                            value={password}
                            onChange={e => setPassword(e.target.value)}
                            className="mb-2"
                        />
                        <div align="center">
                            <button
                                type="submit"
                                className="btn btn-primary mt-2"
                                onClick={handleRegistration}
                            >
                                Sign up
                            </button>
                        </div>
                    </form>
                </div>
            </div>
        </div>

    )
}

