import React, {useState} from "react";
import {BASE_URL, saveToken} from "../utils/Common";
import axios from "axios";

export const Registration = () => {
    const [organizationName, setOrganizationName] = useState('');
    const [phoneNumber, setPhoneNumber] = useState('');
    const [address, setAddress] = useState('');
    const [firstName, setFirstName] = useState('');
    const [lastName, setLastName] = useState('');
    const [email, setEmail] = useState('');
    const [password, setPassword] = useState('');

    const handleRegistration = (props) => {
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
            axios.post(BASE_URL + "/login", null, {
                params: {
                    username: email,
                    password: password
                },
                headers:  {
                    'Content-Type': 'application/x-www-form-urlencoded'
                }
            }).then(res => {
                console.log('Registration/login response >>> ', res)
                saveToken(res.data.access_token, res.data.refresh_token, email)
                props.history.push('/tasks')
            }).catch(err => {
                console.log('Registration/login error >>> ', err)
            })
        }).catch(error => {
            console.log('Registration error >>> ', error)
        })
    }

    return (
        <div className="container m-auto">
            <h3>Welcome to the registration page!</h3>
            <form>
                <div className="mb-3">
                    <label className="form-label">Name of organization</label>
                    <input
                        type="text"
                        className="form-control"
                        placeholder="Name"
                        value={organizationName}
                        onChange={e => setOrganizationName(e.target.value)}
                        required
                    />
                </div>
                <div className="mb-3">
                    <label className="form-label">Phone number</label>
                    <input
                        type="text"
                        className="form-control"
                        placeholder="Number"
                        value={phoneNumber}
                        onChange={e => setPhoneNumber(e.target.value)}
                        required
                    />
                </div>
                <div className="mb-3">
                    <label className="form-label">Address</label>
                    <input
                        type="text"
                        className="form-control"
                        placeholder="Address"
                        value={address}
                        onChange={e => setAddress(e.target.value)}
                        required
                    />
                </div>
                <div className="row">
                    <div className="col-lg-6">
                        <div className="mb-3">
                            <label className="form-label">First name</label>
                            <input
                                type="text"
                                className="form-control"
                                placeholder="First name"
                                value={firstName}
                                onChange={e => setFirstName(e.target.value)}
                                required
                            />
                        </div>
                    </div>
                    <div className="col-lg-6">
                        <div className="mb-3">
                            <label className="form-label">Last name</label>
                            <input
                                type="text"
                                className="form-control"
                                placeholder="Last name"
                                value={lastName}
                                onChange={e => setLastName(e.target.value)}
                                required
                            />
                        </div>
                    </div>
                </div>
                <div className="mb-3">
                    <label className="form-label">Email address</label>
                    <input
                        type="email"
                        className="form-control"
                        placeholder="example@example.com"
                        value={email}
                        onChange={e => setEmail(e.target.value)}
                        required
                    />
                </div>
                <div className="mb-3">
                    <label className="form-label">Password</label>
                    <input
                        type="password"
                        className="form-control"
                        placeholder="Password"
                        value={password}
                        onChange={e => setPassword(e.target.value)}
                        required
                    />
                </div>
                <button
                    type="submit"
                    className="btn btn-primary"
                    onClick={handleRegistration}
                >
                    Sign up
                </button>
            </form>
        </div>
)
}

