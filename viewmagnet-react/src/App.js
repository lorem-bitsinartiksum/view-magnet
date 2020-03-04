import './App.css';
import React from 'react';
import Login from './components/Auth/Login';
import Register from './components/Auth/Register';
import { Route, Switch, Redirect } from 'react-router-dom';
import Bars from './components/Bars'
import Map from './components/Map/Map'
import Profile from './components/Profile'
import CreateAdvert from './components/Advert/CreateAdvert'
import Adverts from './components/Advert/Adverts';
import AdminAuth from './components/Auth/AdminAuth';

function App() {
  let routes = (
    <Switch>
      <Route path="/login" component={Login} />
      <Route path="/register" component={Register} />
      <Route path="/admin" component={AdminAuth} />
      <Route path="/profile" component={Profile} />
      <Route path="/map" component={Map} />
      <Route path="/create-ad" component={CreateAdvert} />
      <Route path="/adverts" component={Adverts} />
      <Redirect to="/" />
    </Switch>
  )

  return (
    <div className="App">
      <Bars>{routes}</Bars>
    </div>
  );
}

export default App;