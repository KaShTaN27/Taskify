import React, {useState} from "react";
import axios from "axios";
import {BASE_URL, getToken, saveEmail, savePersonalData, saveToken} from "../utils/Common";
import jwtDecode from "jwt-decode";
import {useNavigate} from "react-router-dom";

export const Login = (props) => {

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
            // if (error.response.status === 401 || error.response.status === 400) {
            //     setError("Password or email is wrong.")
            //     // setError(error.response.headers.get("Message"));
            // } else {
            //     setError("Something went wrong. Please, try again later.");
            // }
            console.log('error >>> ', error)
        })

    }

    return (
        <div className="login-page">
            <div align="center" className="pb-3">
                <h4>Welcome to the login page!</h4>
                <hr />
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
    )
}