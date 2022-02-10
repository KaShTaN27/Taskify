import React, {useState} from "react";
import axios from "axios";
import {BASE_URL, getAccessToken, saveEmail, saveTokens} from "../utils/Common";
import jwtDecode from "jwt-decode";
import {useNavigate} from "react-router-dom";
export const Login = (props) => {

    const [email, setEmail] = useState('');
    const [password, setPassword] = useState('');
    const [error, setError] = useState('');
    const navigate = useNavigate();

    const handleLogin = () => {
        setError('');
        axios.post(BASE_URL + "/login", null, {
            params: {
                username: email,
                password: password
            },
            headers:  {
                'Content-Type': 'application/x-www-form-urlencoded'
            }
        }).then(response => {
            console.log('Login response >>> ', response)
            saveTokens(response.data.access_token, response.data.refresh_token);
            saveEmail(email);
            console.log(jwtDecode(getAccessToken()))
            navigate("/tasks");
            window.location.reload();
        }).catch(error  => {
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
        <div className="container p-lg-4">
            <h3>Welcome to the Logg in page!</h3>
            <div>
                <label>Email:</label><br/>
                <input
                    type="email"
                    value={email}
                    onChange={e => setEmail(e.target.value)}
                />
            </div>
            <div>
                <label>Password:</label><br/>
                <input
                    type="password"
                    value={password}
                    onChange={e => setPassword(e.target.value)}
                />
            </div>
            <div><br />
                {error && <div className="error">{error}</div>}
                <input
                    type="button"
                    value="Login"
                    onClick={handleLogin}
                />
                <button className="btn btn-primary" onClick={handleLogin}>Login!</button>
            </div>
        </div>
    )
}