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
        <div>
            <div className='row'>
                <div className='col-md-3'>
                    <h1 className='mytitle'>Lavalamp CSS Menu</h1>
                    </div>
                <div className='col-md-9'>
                    <nav>
                    <a href="#">Home</a>
                    <a href="#">About</a>
                    <a href="#">Blog</a>
                    <a href="#">Portefolio</a>
                    <a href="#">Contact</a>
                    <div class="animation start-home"></div>
                    </nav>
                </div>
            </div>
        </div>
    )
}

export default Nav
