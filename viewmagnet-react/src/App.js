import React from 'react';
import LoginForm from './components/Auth/LoginForm';
import RegisterForm from './components/Auth/RegisterForm';
import { Route, Switch, Redirect } from 'react-router-dom';
import './App.css';

function App() {
  return (
    <div className="App">
      <Switch>
          <Route path="/login" component={LoginForm} />
          <Route path="/register" component={RegisterForm} />
          <Redirect to="/" />
        </Switch>
    </div>
  );
}

export default App;