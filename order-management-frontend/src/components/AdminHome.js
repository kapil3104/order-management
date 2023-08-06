import React, { useEffect } from 'react'
import Menu from './Admin/Menu'
import Home from './Admin/Home'
import Category from './Admin/Category'
import SubCategory from './Admin/SubCategory'
import AddUser from './Admin/AddUser'
import ViewUsers from './Admin/ViewUsers'
import Logout from './Logout'
import {
    BrowserRouter as Router,
    Switch,
    Route,
    Link,
    Redirect,
    useRouteMatch, 
    useParams, useHistory
} from "react-router-dom";

function AdminHome() {
    const history = useHistory();
    let match = useRouteMatch();
    return (
        <div className='my-background'>
            <Menu/>
            <Switch>
                <div>
                    <Route path={`${match.path}/home`} component={Home} />
                    <Route path={`${match.path}/category`} component={Category} />
                    <Route path={`${match.path}/subCategory`} component={SubCategory} />
                    <Route path={`${match.path}/addUser`} component={AddUser} />
                    <Route path={`${match.path}/viewUsers`} component={ViewUsers} />
                    <Route path={`${match.path}/logout`} component={Logout} />
                    <Route exact path="" render={() => { return (<Redirect to={`${match.url}/home`}/>  ) }} />
                </div>
            </Switch>
        </div>
    )
}

export default AdminHome
