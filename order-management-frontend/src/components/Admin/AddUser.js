import React, { useEffect, useRef, useState } from 'react'
import axios from 'axios'
import useInput from '../../hooks/useInput'
import { useHistory } from "react-router-dom";
import Cookies from 'js-cookie'
const rest = require('../../BackEndApis')
function AddUser() {
    const messageRef = useRef()
    const history = useHistory();
    const [name, setName,  bindName, resetName] = useInput('')
    const [email, setEmail,  bindEmail, resetEmail] = useInput('')
    const [address, setAddress,  bindAddress, resetAddress] = useInput('')
    const [phone, setPhone,  bindPhone, resetPhone] = useInput('')
    const [password, setPassword, bindPassword, resetPassword] = useInput('')
    const [roleId, setRoleId, bindRoleId, resetRoleId] = useInput('')
    const [distributorId, setDistributorId,  bindDistributorId, resetDistributorId] = useInput('')
    const [roles, setRoles] = useState([])
    const [distributors, setDistributors] = useState([])
    const header = {
        headers: {
            "Content-type": "Application/json",
            "Authorization": `Bearer ${Cookies.get('token')}`
        }
    }
    useEffect(() => {
        axios.get(rest.endPointFetchAllRoles, header)
            .then(response => {
                console.log(response.data.data);
                setRoles(response.data.data)
                .catch(err => {
                    console.log(err)
                })
            })
            .catch(err => {
                console.log(err)
            })
    }, []);

    useEffect(() => {
        axios.get(rest.endPointFetchAllDistributors, header)
            .then(response => {
                console.log(response.data.data);
                setDistributors(response.data.data)
                .catch(err => {
                    console.log(err)
                })
            })
            .catch(err => {
                console.log(err)
            })
    }, []);
    const submitHandler = (e) => {
        e.preventDefault()
        if(name === '' || email==='' || phone==='' || password==='' || roleId==='' || distributorId==='' || address === '') {
            messageRef.current.innerHTML = "<span>* Please Fill All Fields</span>"
        }  else {
            let data = {
                "name":name,
                "email":email,
                "phone":phone,
                "password":password,
                "roleId":parseInt(roleId),
                "address":address,
                "distributorId":parseInt(distributorId)
            }
            console.log(data)
            axios.post(rest.endPointUsers,data,header)
            .then(response => {
                console.log(response.data);
                alert("User Added Successfully")
                setName('')
                setEmail('')
                setAddress('')
                setPhone('')
                setPassword('')
                setRoleId('')
                setDistributorId('')
            })
            .catch((err) => {
                console.log(err.response.data.error);
                messageRef.current.innerHTML = "<span>* Invalid Login Details</span>"
            })
        }
    }

    const isEmailValid = (email) => {
        const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
        return emailRegex.test(email);
    };

    const isPhoneNumberValid = (phoneNumber) => {
        return /^\d{10}$/.test(phoneNumber);
    };

    const isPasswordValid = (password) => {
        return password.length >= 5;
    };

    const isNameValid = (name) => {
        return /^[A-Za-z]+$/.test(name);
    };

    const isFormValid = () => {
        return (
          isEmailValid(email) &&
          isPhoneNumberValid(phone) &&
          isPasswordValid(password) &&
          isNameValid(name)
        );
    };

    return (
        <div className='my-background'>
            <div className='row'>
                <div className='col-md-4'></div>
                <div className='col-md-4'>
                    <div className='card p-3 mt-2'>
                        <form onSubmit={submitHandler}>  
                            <div className='text-center mytitle2'>Add New User</div>
                            <div className='mt-2'>
                                <label className='form-control-lable'>Name</label>
                                <input type='text' {...bindName} className='form-control' placeholder='Enter Name'  required/>
                                {!isNameValid(name) && (
                                <span style={{ color: 'red' }}>Invalid name format</span>
                                )}
                            </div>
                            <div className='mt-2'>
                                <label className='form-control-lable'>Email</label>
                                <input type='email' {...bindEmail} className='form-control' placeholder='Enter Email'  required pattern='[^\s@]+@[^\s@]+\.[^\s@]+' title='Please enter a valid email address'/>
                                {!isEmailValid(email) && (
                                <span style={{ color: 'red' }}>Invalid email format</span>
                                )}
                            </div>
                            <div className='mt-2'>
                                <label className='form-control-lable'>Phone</label>
                                <input type='number' {...bindPhone} className='form-control' placeholder='Enter Phone Number'  required pattern='\d{10}' title='Please enter a 10-digit phone number'/>
                                {!isPhoneNumberValid(phone) && (
                                <span style={{ color: 'red' }}>Invalid phone number format</span>
                                )}
                            </div>
                            <div className='mt-2'>
                                <label className='form-control-lable'>Password</label>
                                <input type='password' {...bindPassword} className='form-control' placeholder='Enter Password'  required minLength='5' title='Password must be at least 5 characters long'/>
                                {!isPasswordValid(password) && (
                                <span style={{ color: 'red' }}>Password must be at least 5 characters long</span>
                                )}
                            </div>
                            <div className='mt-2'>
                                <label className='form-control-lable'>Choose Role</label>
                                <select className='form-control' {...bindRoleId}>
                                    <option value=''>Choose Role</option>
                                    {roles.map((role, index) =>
                                        <option className="list-group-item" key={role.roleId} value={role.roleId}>
                                        {role.roleName}
                                    </option>)}
                                </select>
                            </div>
                            <div className='mt-2'>
                                <label className='form-control-lable'>Choose Distributor</label>
                                <select className='form-control' {...bindDistributorId}>
                                    <option value=''>Choose Distributor</option>
                                    {distributors.map((distributor, index) =>
                                        <option className="list-group-item" key={distributor.distributorId} value={distributor.distributorId}>
                                        {distributor.distributorName}
                                    </option>)}
                                </select>
                            </div>
                            <div className='mt-2'>
                                <label className='form-control-lable'>Address</label>
                                <textarea {...bindAddress}  className='form-control' placeholder='Enter Address'  required></textarea>
                            </div>
                            <div className='mt-3'>
                                <input type='submit' value='Add User' className='btn w-100' disabled={!isFormValid()} style={{backgroundColor:'#34495e',color:'white'}}/>
                            </div>
                            
                        </form>
                    </div>
                    <div>
                        <div ref={messageRef} className="m-5 text-end" style={{color:'white'}}></div>
                    </div>
                </div>
                <div className='col-md-4'></div>
            </div>
        </div>
    )
}
export default AddUser