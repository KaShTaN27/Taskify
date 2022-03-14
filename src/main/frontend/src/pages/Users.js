import React, {useEffect, useState} from "react";
import {BASE_URL, getOrganizationName, getToken} from "../utils/Common";
import axios from "axios";
import jwtDecode from "jwt-decode";
import {
    Accordion,
    AccordionActions,
    AccordionDetails,
    AccordionSummary,
    Button,
    FormControl,
    Input,
    InputAdornment,
    InputLabel,
    List,
    ListItem,
    ListItemIcon,
    ListItemText,
    Typography
} from "@mui/material";
import {AccountBox, Email, ExpandMoreOutlined, Hotel, Work} from "@mui/icons-material";

export const Users = () => {

    const [name, setName] = useState('');
    const [lastname, setLastname] = useState('');
    const [email, setEmail] = useState('');
    const [password, setPassword] = useState('');
    const [users, setUsers] = useState([]);
    const showUserCreationInterface = (jwtDecode(getToken()).roles.includes("ROLE_ADMIN"));
    const organization = getOrganizationName();
    const [tasks, setTasks] = useState([]);

    const [currentFirstName, setCurrentFirstName] = useState("");
    const [newFirstName, setNewFirstName] = useState("");
    const [selectedUser, setSelectedUser] = useState(-1);

    const selectUser = (panel) => (event, newUser) => {
        setSelectedUser(newUser ? panel : -1);
        // Проверка на раскрытие accordion'a
        if (selectedUser !== panel) {
            getTasksOfUser(panel);
            getUserById(panel);
            setNewFirstName(currentFirstName);
        }
    }



    const deleteUserById = (id) => {
        const newUsers = users.filter(user => user.id !== id);
        setUsers(newUsers);
        axios.delete(`${BASE_URL}/api/user/${id}`, {
            headers: {
                'Authorization': getToken()
            }
        }).then(response => {
            console.log(response.data)
        }).catch(err => {
            console.log(err)
        })
    }

    const getUserById = (id) => {
        axios.get(`${BASE_URL}/api/user/${id}`, {
            headers: {
                'Authorization': getToken()
            }
        }).then(response => {
            console.log('User by id >>', response)
            // TODO: setting currentEmail late
            setCurrentFirstName(response.data.name)
            console.log("After setting", currentFirstName)
        }).catch(error => {
            console.log('User by id error >>', error)
        })

    }

    const getTasksOfUser = (id) => {
        axios.get(`${BASE_URL}/api/user/${id}/tasks`, {
            headers: {
                'Authorization': getToken()
            }
        }).then(response => {
            console.log('User by id >>', response)
            setTasks(response.data)
        }).catch(error => {
            console.log('User by id error >>', error)
        })
    }

    const handleUpdateEmail = (id) => {
        axios.put(`${BASE_URL}/api/user/${id}/?email=${newFirstName}`, null
            , {
            headers: {
                'Authorization': getToken()
            }
        }).then(response => {
            console.log("response update user ", response.data)
        }).catch(err => {
            console.log(err)
        })
    }

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
            <div className="add-user">
                {users.map((user) => (
                    <Accordion expanded={selectedUser === user.id} onChange={selectUser(user.id)} key={user.id}>
                        <AccordionSummary expandIcon={<ExpandMoreOutlined/>}>
                            <AccountBox sx={{width: '5%', color: 'text.secondary'}}/>
                            <Typography sx={{width: '50%', flexShrink: 0}}>{user.firstName} {user.lastName}</Typography>
                            <Typography sx={{color: 'text.secondary'}}>Email: {user.email}</Typography>
                        </AccordionSummary>
                        <AccordionDetails>
                            <FormControl variant="standard">
                                <InputLabel htmlFor="input-with-icon-adornment">
                                    First name
                                </InputLabel>
                                <Input
                                    onChange={e => setNewFirstName(e.target.value)}
                                    // value=
                                    defaultValue={user.firstName}
                                    id="input-with-icon-adornment"
                                    startAdornment={
                                        <InputAdornment position="start">
                                            <Email />
                                        </InputAdornment>
                                    }
                                />
                            </FormControl>
                            <List>
                                {tasks.length ? (tasks.map(task => (
                                        <ListItem key={task.id}>
                                            <ListItemIcon>
                                                <Work/>
                                            </ListItemIcon>
                                            <ListItemText>
                                                <Typography>{task.title}</Typography>
                                                <small>Deadline: {task.deadline}</small>
                                            </ListItemText>
                                        </ListItem>
                                    )))
                                    : (
                                        <ListItem>
                                            <ListItemIcon>
                                                <Hotel/>
                                            </ListItemIcon>
                                            <ListItemText>
                                                The user has no tasks
                                            </ListItemText>
                                        </ListItem>
                                    )}
                            </List>
                        </AccordionDetails>
                        <AccordionActions>
                            <Button variant="outlined" color="success"
                            disabled={(newFirstName === user.firstName) || (newFirstName === "")}
                            /*onClick={() => handleUpdateEmail(user.id)}*/>Update</Button>
                            <Button variant="outlined" color="error"
                                    onClick={() => deleteUserById(user.id)}>Delete</Button>
                        </AccordionActions>
                    </Accordion>
                ))
                }
            </div>
        </div>
    )
}