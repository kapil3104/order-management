import React, { useEffect, useRef, useState } from 'react'
import axios from 'axios'
import useInput from '../../hooks/useInput'
import { useHistory } from "react-router-dom";
import Cookies from 'js-cookie'
const rest = require('../../BackEndApis')

function ViewUsers() {
    const messageRef = useRef()
    const history = useHistory();
    const [userType, setUserType, bindUserType, resetUserType] = useInput('stockManagers')
    const [users, setUsers] = useState([])
    const header = {
        headers: { 
            "Content-type": "Application/json",
            "Authorization": `Bearer ${Cookies.get('token')}`
        }
    }
    useEffect(() => {
        axios.get(rest.endPointFetchAllUsers,header)
            .then(response => {
                console.log(response.data.data);
                setUsers(response.data.data)
            })
            .catch(err => {
                console.log(err)
            })
    }, []);
    const changeUserType = (e) => {
        setUserType(e.target.value)
        let url = rest.endPointFetchAllUsers;
        if(e.target.value==='All'){
            url = rest.endPointFetchAllUsers;
        }else if(e.target.value==='StockManager'){
            url = rest.endPointFetchAllUsers + "/Stock_Manager";
        }else if(e.target.value==='SalesPerson'){
            url = rest.endPointFetchAllUsers + "/Sales_Person";
        }else if(e.target.value==='CustomerRepresentative'){
            url = rest.endPointFetchAllUsers + "/Customer_Representative";
        }
        axios.get(url,header)
            .then(response => {
                console.log(response.data.data);
                setUsers(response.data.data)
            })
            .catch(err => {
                console.log(err)
            })
    }
    return (
        <div className='my-background'>
            <div className='container'>
                <div className='row'>
                    <div className='col-md-3'>
                        <div className='mt-2'>
                            <select required className='form-control' onChange={changeUserType} >
                                <option value='All'>All</option>
                                <option value='StockManager'>Stock Manager</option>
                                <option value='SalesPerson'>Sales Person</option>
                                <option value='CustomerRepresentative'>Customer Representative</option>
                            </select>
                        </div>
                    </div>
                    <div className='col-md-6'>
                            <div className='mytitle3'>Available {userType}'s List</div>
                    </div>
                    <div className='col-md-3'>

                    </div>
                </div>
                <table className='table table-bordered text-white mt-3'>
                    <thead>
                        <tr>
                            <th>Name</th>
                            <th>Email</th>
                            <th>Phone</th>
                            <th>Address</th>
                            <th>Role</th>
                        </tr>
                    </thead>
                    <tbody>
                        {users.map((user,index)=><tr key={index}>
                            <td>{user['name']}</td>
                            <td>{user['email']}</td>
                            <td>{user['phone']}</td>
                            <td>{user['address']}</td>
                            <td>{user['role']['roleName']}</td>
                        </tr>)}
                    </tbody>
                </table>
            </div>
            
        </div>
    )
}
export default ViewUsers