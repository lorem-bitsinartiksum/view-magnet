import { LOGIN, LOGOUT } from './../actions'

const initialState = {
    token: null,
    email: null,
    loggedIn: false
};

const auth = (state = initialState, action) => {
    switch (action.type) {
        case LOGIN:
            console.log(action)
            return {
                ...state,
                token: action.token,
                email: action.email,
                loggedIn: true
            }
        case LOGOUT:
            return {
                ...state,
                token: null,
                email: null,
                loggedIn: false
            }
        default:
            return state
    }
}
export default auth;