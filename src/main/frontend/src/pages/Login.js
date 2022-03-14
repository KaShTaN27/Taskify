import React, {useState} from "react";
import axios from "axios";
import {BASE_URL, getToken, saveEmail, savePersonalData, saveToken} from "../utils/Common";
import jwtDecode from "jwt-decode";
import {useNavigate} from "react-router-dom";

export const Login = () => {

    const [email, setEmail] = useState('');
    const [password, setPassword] = useState('');
    const [error, setError] = useState('');
    const navigate = useNavigate();

    const getUserInfo = () => {
        axios.get(`${BASE_URL}/api/user/info`, {
            headers: {
                'Authorization': getToken()
            }
        }).then(response => {
            savePersonalData(response.data.name, response.data.lastName, response.data.organizationName);
            navigate("/tasks");
            window.location.reload();
        })
    }

    const handleLogin = async () => {
        setError('');
        await axios.post(BASE_URL + "/api/auth/login", {
            email: email,
            password: password
        }).then(response => {
            console.log('Login response >>> ', response)
            saveToken(response.data.token);
            saveEmail(email);
            console.log("JWT | ", jwtDecode(getToken()))

            getUserInfo()
        }).catch(error => {
            console.log('error >>> ', error)
        })

    }

    return (
        <div className="row m-auto">
            <div className="col-lg-6 mt-5" align="center">
                <img src="https://account.bulletprofit.com/upload/auth/login.png" alt="login illustration"
                     width="600px"/>
            </div>
            <div className="col-lg-6">
                <div className="login-page">
                    <div align="center" className="pb-3">
                        <h4>Welcome to the login page!</h4>
                        <hr/>
                    </div>
                    <div className="mb-3">
                        <input
                            type="text"
                            className="form-control"
                            placeholder="Email"
                            value={email}
                            onChange={e => setEmail(e.target.value)}
                        />
                    </div>
                    <div className="mb-3">
                        <input
                            type="password"
                            className="form-control"
                            placeholder="Password"
                            value={password}
                            onChange={e => setPassword(e.target.value)}
                        />
                    </div>
                    {error && <div className="error">{error}</div>}
                    <div align="center">
                        <button className="btn btn-primary" onClick={handleLogin}>Login</button>
                    </div>
                    <hr/>
                    <a href="/">Registrate new organization</a>
                </div>
            </div>
        </div>
    )
}