import React, { useEffect } from 'react'
import Cookies from 'js-cookie'
import { useHistory } from "react-router-dom";


function Logout() {
    const history = useHistory();
    useEffect(()=>{
        Cookies.remove('token')
        Cookies.remove('userType')
        history.push("./../home");
    },[]);
    return (
        <div>
            Logging out.............
        </div>
    )
}

export default Logout
