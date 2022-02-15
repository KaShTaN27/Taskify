import React, {useEffect, useState} from "react";
import {Routes, Route, Link, useLocation, useNavigate} from "react-router-dom";
import {Registration} from "./pages/Registration";
import {Login} from "./pages/Login";
import {Tasks} from "./pages/Tasks";
import {Users} from "./pages/Users";
import {
    BASE_URL,
    getAccessToken,
    getEmail, getLastName, getName,
    getRefreshToken,
    removeEmail, removePersonalData,
    removeTokens,
    saveTokens
} from "./utils/Common";
import jwtDecode from "jwt-decode";
import axios from "axios";
import {Profile} from "./pages/Profile";

function App() {
    const [showInterface, setShowInterface] = useState(false);
    const [currentUser, setCurrentUser] = useState(undefined);
    const location = useLocation();
    const navigate = useNavigate();

    useEffect(() => {
        const user = getAccessToken();
        if (user) {
            setCurrentUser(user);
            setShowInterface(true)
        }
    }, [])



    useEffect(() => {
        console.log('handle route change here ', location)
        if (getAccessToken()) {
            const decodedAccessToken = jwtDecode(getAccessToken());
            const decodedRefreshToken = jwtDecode(getRefreshToken());
            if (decodedAccessToken.exp * 1000 < Date.now()) {
                if (decodedRefreshToken.exp * 1000 > Date.now()) {
                    console.log('We need refresh')
                    // refresh
                    axios.get(BASE_URL + "/api/token/refresh", {
                        headers: {
                            'Authorization': 'Bearer ' + getRefreshToken()
                        }
                    }).then(response => {
                        console.log(response)
                        saveTokens(response.data.access_token, response.data.refresh_token)
                        window.location.reload()
                    });
                } else {
                    console.log('Logging out...')
                    handleLogout();
                    navigate("/login");
                }
            }
        }
    }, [location, navigate])


    const handleLogout = () => {
        removeTokens();
        removeEmail();
        removePersonalData();
        setShowInterface(false);
        setCurrentUser(undefined);
        // props.history.push('/login');
        console.log(getAccessToken(), ' and ', getRefreshToken(), ' and ', getEmail())
    }

    return (
        <div>


            <nav className="navbar navbar-dark navbar-expand bg-primary">
                <div className={'navbar-brand p-lg-2'}>
                    Taskify application
                </div>

                <div className="navbar-nav me-auto">
                    {showInterface && (
                        <li className="navbar-item">
                            <Link className="nav-link" to="/tasks">Tasks</Link>
                        </li>)}
                    {showInterface && (
                        <li className="navbar-item">
                            <Link className="nav-link" to="/users">Users</Link>
                        </li>)}
                </div>
                {currentUser ? (
                    <div className="navbar-nav ms-auto me-3">
                        <li className="navbar-item">
                            <Link className="nav-link" to="/profile">{getName()} {getLastName()}</Link>
                        </li>
                        <li className="navbar-item">
                            <a href="/login" className="nav-link" onClick={handleLogout}>
                                Log out
                            </a>
                        </li>
                    </div>
                ) : (
                    <div className="navbar-nav ms-auto me-3">
                        <li className="navbar-item">
                            <Link className="nav-link" to="/login">Login</Link>
                        </li>
                        <li className="navbar-item">
                            <Link className="nav-link" to="/">Sign up</Link>
                        </li>
                    </div>)}
            </nav>


            <div>

                    <div className="content pt-4">
                        <Routes>
                            <Route exact path="/" element={<Registration />}/>
                            <Route path="/login" element={<Login />}/>
                            <Route path="/tasks" element={<Tasks />}/>
                            <Route path="/users" element={<Users />}/>
                            <Route path="/profile" element={<Profile />}/>
                        </Routes>
                    </div>
                {/*<AuthVerify />*/}
            </div>
        </div>
    );
}

export default App;
