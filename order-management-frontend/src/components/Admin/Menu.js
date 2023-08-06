import React from 'react'
import {
    BrowserRouter as Router,
    Switch,
    Route,
    Link, useRouteMatch
  } from "react-router-dom";
 
function Nav() {
    let match = useRouteMatch();
    return ( 
        <div >
            <div className='row'>
                <div className='col-md-3'>
                    <h1 className='mytitle'>Store Management</h1>
                    </div>
                <div className='col-md-9'>
                    <nav className='nav3'>
                        <Link to={`${match.url}/home`} style={{width:'100px'}}>Home</Link>
                        <Link to={`${match.url}/category`} style={{width:'130px'}}>Categories</Link>
                        <Link to={`${match.url}/subCategory`} style={{width:'140px'}}>Sub Categories</Link>
                        <Link to={`${match.url}/addUser`} style={{width:'130px'}}>Add User</Link>
                        <Link to={`${match.url}/viewUsers`} style={{width:'130px'}}>View Users</Link>
                        <Link to={`${match.url}/logout`} style={{width:'130px'}}>Logout</Link>
                        <div class="animation start-home"></div>
                    </nav>
                </div>
            </div>
        </div>
    )
}

export default Nav
