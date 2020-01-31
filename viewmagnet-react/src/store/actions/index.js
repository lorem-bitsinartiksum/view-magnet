// action types
export const LOGIN = 'LOGIN'
export const LOGOUT = 'LOGOUT'

// actions
export const login = (email, token) => { return {type: LOGIN, email, token}}
export const logout = () => { return { type: LOGOUT } }

// action functions