// action types
export const LOGIN = 'LOGIN'
export const LOGOUT = 'LOGOUT'

// actions
export const login = (token, email) => { localStorage.setItem('token', token); localStorage.setItem('email', email); return { type: LOGIN, token, email} }
export const logout = () => { localStorage.clear(); return { type: LOGOUT } }

// action functions