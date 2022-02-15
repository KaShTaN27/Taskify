import React, {useEffect, useState} from "react";
import axios from "axios";
import {BASE_URL, getAccessToken, getEmail, getOrganizationName} from "../utils/Common";
import {MultiSelectComponent} from "@syncfusion/ej2-react-dropdowns"

export const Tasks = () => {
    const [tasks, setTasks] = useState([]);
    const [title, setTitle] = useState('');
    const [deadline, setDeadline] = useState('');
    const [description, setDescription] = useState('');
    const [emails, setEmails] = useState([]);
    const date = new Date()
    const currentDate = `${date.toISOString().substring(0, 10)}`
    const [usersNames, setUsersNames] = useState([]);

    const handleAddTask = () => {
        axios.post(BASE_URL + "/api/tasks/add", {
            title: title,
            description: description,
            deadline: deadline,
            isDone: false,
            emails: emails
        }, {
            headers: {
                'Authorization': 'Bearer ' + getAccessToken()
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
            await axios.get(`${BASE_URL}/api/tasks?email=${email}`, {
                headers: {
                    'Authorization': 'Bearer ' + getAccessToken()
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
        axios.get(`${BASE_URL}/api/user/organization/members?name=${getOrganizationName()}`, {
            headers: {
                'Authorization': 'Bearer ' + getAccessToken()
            }
        }).then(response => {
            console.log('members >> ', response.data)
            setUsersNames(response.data)
        });
    }, []);

    return (
        <div className={"container pt-4"}>
            <div>
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
                <div className="row">
                    <div className="col-lg-6">
                        <MultiSelectComponent
                            placeholder="Emails"
                            dataSource={usersNames}
                            showSelectAll={true}
                            change={e => setEmails(e.value)}
                        >

                        </MultiSelectComponent>
                    </div>
                    <div className="col-lg-6">
                        <div className="mb-3">
                            <button className="btn btn-primary" onClick={handleAddTask}>Add</button>
                        </div>
                    </div>
                </div>
            </div>
            <hr/>
            <ul className={'list-group'}>
                {tasks.length ? (tasks.map(task => (
                        <li className="list-group-item note" key={task.id}>
                            <div>
                                <strong>{task.title}</strong>
                            </div>
                        </li>
                    )))
                    : (<li className="list-group-item note">
                        <div align="center">
                            <strong>You don't have any tasks right now.</strong>
                        </div>
                    </li>)}
            </ul>
        </div>

    )
}