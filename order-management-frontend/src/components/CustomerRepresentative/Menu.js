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
                    <nav className='nav4'>
                        <Link to={`${match.url}/home`} style={{width:'100px'}}>Home</Link>
                        <Link to={`${match.url}/viewSales?type=ordered`} style={{width:'130px'}}>View Sales</Link>
                        <Link to={`${match.url}/viewProducts`} style={{width:'140px'}}>View Products</Link>
                        <Link to={`${match.url}/logout`} style={{width:'130px'}}>Logout</Link>
                        <div class="animation start-home"></div>
                    </nav>
                </div>
            </div>
        </div>
    )
}

export default Nav
