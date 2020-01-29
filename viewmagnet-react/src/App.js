import React from 'react';
import LoginForm from './components/Auth/LoginForm';
import RegisterForm from './components/Auth/RegisterForm';
import { Route, Switch, Redirect } from 'react-router-dom';
import './App.css';
import Bars from './components/Bars'
import Profile from './components/Profile'

function App() {
  let routes = (
    <Switch>
      <Route path="/login" component={LoginForm} />
      <Route path="/register" component={RegisterForm} />
      <Route path="/profile" component={Profile} />
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