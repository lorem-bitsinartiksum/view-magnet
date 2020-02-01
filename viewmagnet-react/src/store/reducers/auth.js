import { LOGIN, LOGOUT } from './../actions'

const initialState = {
    token: null,
    loggedIn: false
};

const auth = (state = initialState, action) => {
    switch (action.type) {
        case LOGIN:
            return {
                ...state,
                token: action.token,
                loggedIn: true
            }
        case LOGOUT:
            return {
                ...state,
                token: null,
                loggedIn: false
            }
        default:
            return state
    }
}
export default auth;