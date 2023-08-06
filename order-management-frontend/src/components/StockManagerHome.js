import React, { useEffect } from 'react'
import Menu from './StockManager/Menu'
import Home from './StockManager/Home'
import AddProduct from './StockManager/AddProduct'
import UpdateProduct from './StockManager/UpdateProduct'
import ViewProducts from './Shared/ViewProducts'
import ViewSales from './Shared/ViewSales'
import Logout from './Logout'
import Cookies from 'js-cookie'
import {
    BrowserRouter as Router,
    Switch,
    Route,
    Link,
    Redirect,
    useRouteMatch,
    useParams, useHistory
} from "react-router-dom";

function StockManagerHome() {
    const history = useHistory();
    let match = useRouteMatch();
    return (
        <div className='my-background'>
            <Menu/>
            <Switch>
                <div>
                    <Route path={`${match.path}/home`} component={Home} />
                    <Route path={`${match.path}/addProduct`} component={AddProduct} />
                    <Route path={`${match.path}/viewProducts`} component={ViewProducts} />
                    <Route path={`${match.path}/updateProduct`} component={UpdateProduct} />
                    <Route path={`${match.path}/viewSales`} component={ViewSales} />
                    <Route path={`${match.path}/logout`} component={Logout} />
                    <Route exact path="" render={() => { return (<Redirect to={`${match.url}/home`}/>  ) }} />
                </div>
            </Switch>
        </div>
    )
}

export default StockManagerHome
