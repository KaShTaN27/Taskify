import React, {useEffect, useState} from "react";
import axios from "axios";
import {BASE_URL, getToken, getEmail} from "../utils/Common";
import {MultiSelectComponent} from "@syncfusion/ej2-react-dropdowns"
import jwtDecode from "jwt-decode";

export const Tasks = () => {
    const [tasks, setTasks] = useState([]);
    const [title, setTitle] = useState('');
    const [deadline, setDeadline] = useState('');
    const [description, setDescription] = useState('');
    const [emails, setEmails] = useState([]);
    const date = new Date()
    const currentDate = `${date.toISOString().substring(0, 10)}`
    const [usersNames, setUsersNames] = useState([]);
    const isAdmin = jwtDecode(getToken()).roles.includes('ROLE_ADMIN');

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
                    {tasks.length ? (tasks.map(task => (
                            <div key={task.id}>
                                <a href="#users" className="list-group-item list-group-item-action">
                                    <div className="d-flex w-100 justify-content-between">
                                        <h5 className="mb-1">{task.title}</h5>
                                        <small className="text-muted">Deadline: {task.deadline}</small>
                                    </div>
                                    <div className="d-flex w-100 justify-content-between">
                                        <h6 className="mb-1">{task.description}</h6>
                                        <small className="text-muted">Done: {task.isDone ? "Yes" : "No"}</small>
                                    </div>
                                </a>
                            </div>
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