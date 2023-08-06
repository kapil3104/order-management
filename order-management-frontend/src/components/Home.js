import React, { useEffect, useRef, useState } from 'react'
import axios from 'axios'
import useInput from '../hooks/useInput'
import { useHistory } from "react-router-dom";
import Cookies from 'js-cookie'
const rest = require('../BackEndApis')
 
function Home() {
    const messageRef = useRef()
    const history = useHistory();
    const [email, setEmail,  bindEmail, resetEmail] = useInput('')
    const [password, setPassword, bindPassword, resetPassword] = useInput('')
    useEffect(() => {
        axios.get(rest.endPointAdminReg)
            .then(response => {
                console.log(response.data);
                messageRef.current.innerHTML = "<span>"+response.data+"</span>"
            })
            .catch(err => {
                console.log(err)
            })
    }, []);
    const submitHandler = (e) => {
        e.preventDefault()
        if(email==='' || password==='') {
            messageRef.current.innerHTML = "<span>* Please Fill All Fields</span>"
        }  else {
            let data = {"username":email,"password":password}
            axios.post(rest.endPointAuthentication,data)
            .then(response => {
                console.log(response.data.data);
                if(response.data=="Invalid Role"){
                    messageRef.current.innerHTML = "<span>* Invalid Role</span>"
                }else {
                    const roleName = response.data.data.roleName;
                    Cookies.set('token',response.data.data.token)
                    Cookies.set('userType',roleName)
                    Cookies.set('role',roleName)
                    Cookies.set('email',email)
                    console.log('roleName : ' + response.data.data.roleName);
                    console.log('token : ' + response.data.data.token);
                    if(roleName=='Admin_Role'){
                        history.push("/admin");   
                    }else if(roleName=='Stock_Manager'){
                        history.push("/stockManager");  
                    }else if(roleName=='Sales_Person'){
                        history.push("/salesPerson");    
                    }else if(roleName=='Customer_Representative'){
                        history.push("/customerRepresentative");   
                    }
                }
            })
            .catch((err) => {
                console.log(err.response.data.error);
                alert( "Invalid Login Details")
            })
        }
    }
    return (
        <div className='my-background'>
            <h1 className='mytitle'>Store Management System</h1>
            <div className='row'>
                <div className='col-md-4'></div>
                <div className='col-md-4'>
                    <div className='card p-3 mt-5'>
                        <form onSubmit={submitHandler}>  
                            <div className='text-center mytitle2'>Login Here</div>
                            <div className='mt-2'>
                                <label className='form-control-lable'>Email</label>
                                <input type='email' {...bindEmail}  className='form-control' placeholder='Enter Email'  required/>
                            </div>
                            <div className='mt-2'>
                                <label className='form-control-lable'>Password</label>
                                <input type='password' {...bindPassword}  className='form-control' placeholder='Enter Password'  required/>
                            </div>
                            <div className='mt-3'>
                                <input type='submit' value='Login' className='btn w-100' style={{backgroundColor:'#34495e',color:'white'}}/>
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

export default Home
