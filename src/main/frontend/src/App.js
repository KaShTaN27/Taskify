import React, {useEffect, useState} from "react";
import {Link, Route, Routes, useLocation, useNavigate} from "react-router-dom";
import {Registration} from "./pages/Registration";
import {Login} from "./pages/Login";
import {Tasks} from "./pages/Tasks";
import {Users} from "./pages/Users";
import {
    getToken,
    getEmail,
    getLastName,
    getName,
    removeEmail,
    removePersonalData, removeToken
} from "./utils/Common";
import jwtDecode from "jwt-decode";
import {Profile} from "./pages/Profile";

function App() {
    const [showInterface, setShowInterface] = useState(false);
    const [showUsersPage, setShowUsersPage] = useState(false);
    const [currentUser, setCurrentUser] = useState(undefined);
    const location = useLocation();
    const navigate = useNavigate();

    useEffect(() => {
        const user = getToken();
        if (user) {
            setCurrentUser(user);
            setShowInterface(true);
            setShowUsersPage(jwtDecode(getToken()).roles.includes('ROLE_ADMIN'))
        }
    }, [])



    useEffect(() => {
        console.log('handle route change here ', location)
        if (getToken()) {
            const decodedToken = jwtDecode(getToken());
            if (decodedToken.exp * 1000 < Date.now()) {
                console.log('Logging out...')
                handleLogout();
                navigate("/login");
            }
        }
    }, [location, navigate])


    const handleLogout = () => {
        removeToken();
        removeEmail();
        removePersonalData();
        setShowInterface(false);
        setShowUsersPage(false);
        setCurrentUser(undefined);
        // props.history.push('/login');
        console.log(getToken(), ' and ', getEmail())
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
                    {showUsersPage && (
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
