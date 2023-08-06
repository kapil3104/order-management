import React, { useEffect } from 'react';
import './App.css';
import {
  BrowserRouter as Router,
  Switch,
  Route,
  Link,
  Redirect,
  useRouteMatch,
  useParams
} from "react-router-dom";
import Home from './components/Home';
import AdminHome from './components/AdminHome';
import StockManagerHome from './components/StockManagerHome';
import SalesPersonHome from './components/SalesPersonHome';
import CustomerRepresentativeHome from './components/CustomerRepresentativeHome';
import NotFound from './components/NotFound';
import Logout from './components/Logout';
function App() {
  return (
    <Router>
      <div className="App">
        <Route path="/home" component={Home} />
        <Route path="/logout" component={Logout} />
        <Route path="/admin" component={AdminHome} />
        <Route path="/stockManager" component={StockManagerHome} />
        <Route path="/salesPerson" component={SalesPersonHome} />
        <Route path="/customerRepresentative" component={CustomerRepresentativeHome} />
        <Route path="/notFound" component={NotFound} />
        <Route exact path="/" render={() => { return (<Redirect to="/home"/>  ) }} />
      </div>
  </Router>
  );
}

export default App;
