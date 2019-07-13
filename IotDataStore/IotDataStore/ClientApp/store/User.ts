import { Action, Reducer } from 'redux';
import { AppThunkAction, LocationChangeAction} from './';
import axios from 'axios';
import setAuthorizationHeader from '../utils/setAuthorizationHeader'
import decode from "jwt-decode";

export interface UserCredentials {
    username: string,
    password: string
}

export interface UserState {
    id: number,
    username: string,
    email: string,
    token: string,
    error?: string
}

export interface FetchUser { type: "FETCH_USER", payload: UserState }
interface AuthenticateError { type: "AUTHENTICATE_ERROR", payload: string }
interface Logout {type: "LOGOUT_USER"}

type KnownAction = FetchUser | AuthenticateError | Logout

export const actionCreators = {
    authenticateUser: (userCredentials: UserCredentials): AppThunkAction<KnownAction> => async (dispatch, getState) => {
        axios.post('/api/Users/Authenticate', userCredentials)
            .then(res => {
                localStorage.iotDataStoreJWT = res.data.token
                setAuthorizationHeader(res.data.token)
                dispatch({ type: "FETCH_USER", payload: res.data })
            })
            .catch(err => {
                dispatch({ type: "AUTHENTICATE_ERROR", payload: "Invalid password or username" })
            })
    },
    logoutUser: (): AppThunkAction<KnownAction> => async (dispatch, getState) => {
        dispatch({ type: "LOGOUT_USER" })
    }
};

const unloadedState: UserState = { username: '', email: '', token: '', id: -1 }

export const reducer: Reducer<UserState> = (state: UserState, incomingAction: Action) => {
    const action = incomingAction as KnownAction;
    switch (action.type) {
        case "FETCH_USER":
            return action.payload
        case "AUTHENTICATE_ERROR":
            const clone = Object.assign({}, unloadedState)
            clone.error = action.payload
            return clone
        case "LOGOUT_USER":
            return unloadedState
    }

    return state || unloadedState
};