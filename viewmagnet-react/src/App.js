import React from 'react';
import LoginForm from './components/Auth/LoginForm';
import RegisterForm from './components/Auth/RegisterForm';
import { Route, Switch, Redirect } from 'react-router-dom';
import './App.css';
import Bars from './components/Bars'
import Profile from './components/Profile'
import CreateAdvert from './components/Advert/CreateAdvert'
import Advert from './components/Advert/Advert';

function App() {
  let routes = (
    <Switch>
      <Route path="/login" component={LoginForm} />
      <Route path="/register" component={RegisterForm} />
      <Route path="/profile" component={Profile} />
      <Route path="/create-ad" component={CreateAdvert} />
      <Route path="/ad" component={Advert} />
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