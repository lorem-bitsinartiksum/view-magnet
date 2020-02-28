import { LOGIN, LOGOUT } from './../actions'

const initialAuthState = {
    token: null,
    email: null,
    loggedIn: false
};

const auth = (state = initialAuthState, action) => {
    switch (action.type) {
        case LOGIN:
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