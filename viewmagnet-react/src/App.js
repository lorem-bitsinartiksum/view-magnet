import React from 'react';
import LoginForm from './components/Auth/LoginForm';
import RegisterForm from './components/Auth/RegisterForm';
import { Route, Switch, Redirect } from 'react-router-dom';
import './App.css';
import SiderDemo from './components/Bars'

function App() {
  let routes = (
    <Switch>
      <Route path="/login" component={LoginForm} />
      <Route path="/register" component={RegisterForm} />
      <Redirect to="/" />
    </Switch>
  )

  return (
    <div className="App">
      <SiderDemo>{routes}</SiderDemo>
    </div>
  );
}

export default App;