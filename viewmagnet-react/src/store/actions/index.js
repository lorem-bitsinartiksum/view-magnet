// action types
export const LOGIN = 'LOGIN'
export const LOGOUT = 'LOGOUT'

// actions
export const login = (token, email, isAdmin) => { localStorage.setItem('token', token); localStorage.setItem('email', email);  localStorage.setItem('isAdmin', isAdmin); return { type: LOGIN, token, email, isAdmin} }
export const logout = () => { localStorage.clear(); return { type: LOGOUT } }

// action functions