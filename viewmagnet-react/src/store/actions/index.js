// action types
export const LOGIN = 'LOGIN'
export const LOGOUT = 'LOGOUT'

// actions
export const login = (token) => { return {type: LOGIN, token}}
export const logout = () => { return { type: LOGOUT } }

// action functions