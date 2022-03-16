import React, {useEffect, useState} from "react";
import axios from "axios";
import {BASE_URL, getEmail, getToken} from "../utils/Common";
import {MultiSelectComponent} from "@syncfusion/ej2-react-dropdowns"
import jwtDecode from "jwt-decode";
import {
    Accordion,
    AccordionActions,
    AccordionDetails,
    AccordionSummary,
    Button,
    Checkbox,
    List,
    ListItem,
    ListItemIcon,
    ListItemText,
    Typography
} from "@mui/material";
import {AccountBox, Description, Done, ExpandMoreOutlined, Work} from "@mui/icons-material";

export const Tasks = () => {

    const [tasks, setTasks] = useState([]);

    // Task payload
    const [title, setTitle] = useState('');
    const [description, setDescription] = useState('');
    const [deadline, setDeadline] = useState('');
    const [emails, setEmails] = useState([]);

    // For deadline select
    const date = new Date()
    const currentDate = `${date.toISOString().substring(0, 10)}`

    // For assigning task to users
    const [usersNames, setUsersNames] = useState([]);

    const isAdmin = jwtDecode(getToken()).roles.includes('ROLE_ADMIN');

    const [isDone, setIsDone] = useState(false);
    const [currentIsDone, setCurrentIsDone] = useState(undefined);
    const [users, setUsers] = useState([]);
    const [selectedTask, setSelectedTask] = useState(-1);
    const selectTask = (panel) => (event, newTask) => {

        setSelectedTask(newTask ? panel : -1);
        if (selectedTask !== panel) {
            setIsDone(undefined);
            getUsersOfTask(panel);
            setIsDone(getTaskById(panel));
            console.log(isDone, " | ", currentIsDone)
        }
    }

    useEffect(() => {
        if (selectedTask !== -1)
            setCurrentIsDone(getTaskById(selectedTask));
            console.log(currentIsDone)
    }, [selectedTask])

    const deleteTaskById = (id) => {
        const newTasks = tasks.filter(task => task.id !== id);
        setTasks(newTasks);
        axios.delete(`${BASE_URL}/api/task/${id}`, {
            headers: {
                'Authorization': getToken()
            }
        }).then(response => {
            console.log(response.data)
        }).catch(err => {
            console.log(err)
        })
    }

    const getUsersOfTask = (id) => {
        axios.get(`${BASE_URL}/api/task/${id}/users`, {
            headers: {
                'Authorization': getToken()
            }
        }).then(response => {
            console.log('User by id >>', response)
            setUsers(response.data)
        }).catch(error => {
            console.log('User by id error >>', error)
        })
    }

    function getTaskById (id) {
        let result;
        axios.get(`${BASE_URL}/api/task/${id}`, {
            headers: {
                'Authorization': getToken()
            }
        }).then(response => {
            result = response.data.done
            console.log(response.data.done, "| |", result)
            console.log(response.data)
        }).catch(error => {
            console.log(error)
        })
        return result
    }

    const updateTaskById = (id) => {
        axios.put(`${BASE_URL}/api/task/${id}/?isDone=${isDone}`, null
            , {
                headers: {
                    'Authorization': getToken()
                }
            }).then(response => {
            console.log("response update user ", response.data)
            window.location.reload();
        }).catch(err => {
            console.log(err)
        })
    }

    const handleAddTask = () => {
        axios.post(BASE_URL + "/api/task/add", {
            title: title,
            description: description,
            deadline: deadline,
            isDone: false,
            emails: emails
        }, {
            headers: {
                'Authorization': getToken()
            }
        }).then(response => {
            console.log('Add Task response: ', response)
            window.location.reload()
        }).catch(err => {
            console.log('Add task err: ', err)
        })
    }

    useEffect(() => {
        async function fetchTasks() {
            const email = getEmail();
            await axios.get(`${BASE_URL}/api/task/all`, {
                headers: {
                    'Authorization': getToken()
                }
            }).then(response => {
                console.log('Tasks response >>', response)
                setTasks(response.data)

            }).catch(error => {
                console.log(`${BASE_URL}/api/user/tasks?email=${email}`)
                console.log('error >>', error)
            })
        }

        fetchTasks();
        if (isAdmin) {
            axios.get(`${BASE_URL}/api/user/all`, {
                headers: {
                    'Authorization': getToken()
                }
            }).then(response => {
                console.log('members >> ', response.data)
                setUsersNames(response.data.map(function (user) {
                    return user.email
                }))
            });
        } else {
            setEmails(getEmail().split(" "));
        }
    }, []);

    return (
        <div className="profile">
            <div className="task-page">
                <div align="center">
                    <h4>Here you can create a new task</h4>
                    <hr/>
                </div>
                <div className="row">
                    <div className="col-lg-6">
                        <div className="mb-3">
                            <input
                                type="text"
                                className="form-control"
                                placeholder="Title of the task"
                                value={title}
                                onChange={e => setTitle(e.target.value)}
                            />
                        </div>
                    </div>
                    <div className="col-lg-6">
                        <div className="mb-3">
                            <input
                                type="date"
                                className="form-control"
                                value={deadline}
                                min={currentDate}
                                onChange={e => setDeadline(e.target.value)}
                            />
                        </div>
                    </div>
                </div>
                <div className="mb-3">
                    <input
                        type="text"
                        className="form-control"
                        placeholder="Description"
                        value={description}
                        onChange={e => setDescription(e.target.value)}
                    />
                </div>
                {isAdmin &&
                    <div className="mb-3">
                        <MultiSelectComponent
                            placeholder="Emails"
                            dataSource={usersNames}
                            showSelectAll={true}
                            change={e => setEmails(e.value)}
                        >

                        </MultiSelectComponent>
                    </div>}
                <div className="mb-3" align="center">
                    <button className="btn btn-primary" onClick={handleAddTask}>Add</button>
                </div>
                <hr/>
            </div>
            <div className="tasks">
                <hr/>
                <ul className={'list-group'}>
                    {tasks.length ? (tasks.map((task) => (
                            <Accordion expanded={selectedTask === task.id} onChange={selectTask(task.id)} key={task.id}>
                                <AccordionSummary expandIcon={<ExpandMoreOutlined/>}>
                                    <Work sx={{width: '5%', color: 'text.secondary'}}/>
                                    <Typography sx={{width: '70%', flexShrink: 0}}>{task.title}</Typography>
                                    <Typography sx={{color: 'text.secondary'}}>Deadline: {task.deadline}</Typography>
                                </AccordionSummary>
                                <AccordionDetails>
                                    <Typography sx={{width: '60%', flexShrink: 0}} align={"left"}>
                                        <Description sx={{width:'10%', color: 'text.secondary'}}/>
                                        Description:
                                        <strong>{task.description}</strong>
                                    </Typography>
                                    <Typography sx={{width: '60%', flexShrink: 0}} align={"left"}>
                                        <Done sx={{width:'10%', color: 'text.secondary'}}/>
                                        Done:
                                        <Checkbox defaultChecked={task.done} color="success"
                                                  onChange={(e) => setIsDone(e.target.checked)}/>
                                    </Typography>
                                    {isAdmin && <List>
                                        {users.map(user => (
                                            <ListItem key={user.id}>
                                                <ListItemIcon>
                                                    <AccountBox/>
                                                </ListItemIcon>
                                                <ListItemText>
                                                    {user.name} {user.lastName}
                                                </ListItemText>
                                            </ListItem>
                                        ))}
                                    </List>}
                                </AccordionDetails>
                                {isAdmin && <AccordionActions>
                                    <Button variant="outlined" color="success"
                                            disabled={task.isDone === isDone}
                                            onClick={() => updateTaskById(task.id)}>Update</Button>
                                    <Button variant="outlined" color="error"
                                            onClick={() => deleteTaskById(task.id)}>Delete</Button>
                                </AccordionActions>}
                            </Accordion>
                        )))
                        : (<li className="list-group-item note">
                            <div align="center">
                                <strong>You don't have any tasks right now.</strong>
                            </div>
                        </li>)}
                </ul>
            </div>
        </div>

    )
}