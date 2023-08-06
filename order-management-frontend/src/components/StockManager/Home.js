import React, { useEffect, useRef, useState } from 'react'
import axios from 'axios'
import useInput from '../../hooks/useInput'
import { useHistory } from "react-router-dom";
import Cookies from 'js-cookie'
const rest = require('../../BackEndApis')
function Home() {
    const messageRef = useRef()
    const history = useHistory();
    const [userType, setUserType, bindUserType, resetUserType] = useInput('stockManagers')
    const [user, setUser] = useState({})
    const header = {
        headers: {
            "Content-type": "Application/json",
            "Authorization": `Bearer ${Cookies.get('token')}`
        }
    }
    useEffect(() => {
        axios.get(rest.endPointUserByEmail + "?email=" + Cookies.get('email'),header)
            .then(response => {
                console.log(response.data.data);
                setUser(response.data.data)
            })
            .catch(err => {
                console.log(err)
            })
    }, []);
    return (
        <div className="container mt-5">
            <div className="mt-5 pt-5 h3 text-center text-white">Welcome Stock Manager Home</div>
            <table className='table table-bordered text-white mt-5'>
                    <thead>
                        <tr>
                            <th>Name</th>
                            <th>Email</th>
                            <th>Phone</th>
                            <th>Address</th>
                            <th>Role Name</th>
                        </tr>
                    </thead>
                    <tbody>
                        <tr>
                            <td>{user['name']}</td>
                            <td>{user['email']}</td>
                            <td>{user['phone']}</td>
                            <td>{user['address']}</td>
                            <td>{Cookies.get('userType')}</td>
                        </tr>
                    </tbody>
                </table>
        </div>
    )
}
export default Home