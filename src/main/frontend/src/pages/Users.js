import React, {useEffect, useState} from "react";
import {BASE_URL, getToken, getOrganizationName} from "../utils/Common";
import axios from "axios";
import jwtDecode from "jwt-decode";

export const Users = () => {

    const [name, setName] = useState('');
    const [lastname, setLastname] = useState('');
    const [email, setEmail] = useState('');
    const [password, setPassword] = useState('');
    const [users, setUsers] = useState([]);
    const showUserCreationInterface = (jwtDecode(getToken()).roles.includes("ROLE_ADMIN"));
    const organization = getOrganizationName();

    const handleCreate = () => {
        axios.post(BASE_URL + "/api/user/create", {
            firstName: name,
            lastName: lastname,
            email: email,
            password: password,
            organization: organization
        }, {
            headers: {
                'Authorization': getToken()
            }
        }).then(response => {
            console.log('Add task resp > ', response)
            // setName('');
            // setLastname('');
            // setEmail('');
            // setPassword('');
            window.location.reload();
        }).catch(err => {
            console.log('Add user err > ', err)
        })
    }

    useEffect(() => {
        async function fetchUsers() {
            await axios.get(`${BASE_URL}/api/user/all`, {
                headers: {
                    'Authorization': getToken()
                }
            }).then(response => {
                console.log('Users resp > ', response);
                setUsers(response.data);
            }).catch(err => {
                console.log('Users err > ', err);
            })
        }

        fetchUsers();
    }, [])

    return (
        <div className="profile">
            {showUserCreationInterface && (
                <div className="add-user">
                    <div align="center" className="pb-3">
                        <h4>Here you can create a new user</h4>
                        <hr/>
                    </div>
                    <div className="row">
                        <div className="col-lg-6">
                            <div className="mb-3">
                                <input
                                    type="text"
                                    className="form-control"
                                    placeholder="First Name"
                                    value={name}
                                    onChange={e => setName(e.target.value)}
                                />
                            </div>
                        </div>
                        <div className="col-lg-6">
                            <div className="mb-3">
                                <input
                                    type="text"
                                    className="form-control"
                                    placeholder="Last Name"
                                    value={lastname}
                                    onChange={e => setLastname(e.target.value)}
                                />
                            </div>
                        </div>
                    </div>
                    <div className="row">
                        <div className="col-lg-6">
                            <div className="mb-3">
                                <input
                                    type="text"
                                    className="form-control"
                                    placeholder="Email"
                                    value={email}
                                    onChange={e => setEmail(e.target.value)}
                                />
                            </div>
                        </div>
                        <div className="col-lg-6">
                            <div className="mb-3">
                                <input
                                    type="text"
                                    className="form-control"
                                    placeholder="Password"
                                    value={password}
                                    onChange={e => setPassword(e.target.value)}
                                />
                            </div>
                        </div>
                    </div>
                    <div align="center">
                        <button className="btn btn-primary" onClick={handleCreate}>Create</button>
                    </div>
                    <hr/>
                </div>
            )}
            <div>
                <table className="table table-striped">
                    <thead>
                    <tr>
                        <th scope="col">#</th>
                        <th scope="col">First name</th>
                        <th scope="col">Last name</th>
                        <th scope="col">Email</th>
                    </tr>
                    </thead>
                    <tbody>
                    {users.length ? (users.map((user, index) => (
                            <tr key={user.id}>
                                <th scope="row">{index + 1}</th>
                                <td>{user.firstName}</td>
                                <td>{user.lastName}</td>
                                <td>{user.email}</td>
                            </tr>
                        ))
                    ) : (
                        <tr>
                            <th scope="row"> </th>
                            <td> </td>
                            <td> </td>
                            <td> </td>
                        </tr>
                    )}

                    </tbody>
                </table>
            </div>
        </div>
    )
}