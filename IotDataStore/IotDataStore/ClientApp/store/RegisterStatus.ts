import { Action, Reducer } from 'redux';
import { AppThunkAction, LocationChangeAction, LOCATION_CHANGE } from './';
import axios from 'axios'

export interface RegisterStatusState {
  error?: string,
  message?: string
};

export interface UserRegisterForm {
    username: string,
    email: string,
    password: string
}

interface RegisterUser { type: "REGISTER_USER", payload: RegisterStatusState }
interface ClearStatus { type: "CLEAR_STATUS" }

type KnownAction = RegisterUser | ClearStatus | LocationChangeAction

export const actionCreators = {
    registerUser: (newUserData: UserRegisterForm): AppThunkAction<KnownAction> => async (dispatch, getState) => {
        axios.post("/api/Users", newUserData)
            .then((res) => {
                dispatch({type: "REGISTER_USER", payload: res.data})
            })
            .catch((err) => {
                dispatch({ type: "REGISTER_USER", payload: err.data })
            })
    },
    clearStatus: (): AppThunkAction<KnownAction> => (dispatch) => {
        dispatch({ type: "CLEAR_STATUS" })
    }
};

const unloadedState: RegisterStatusState = { }

export const reducer: Reducer<RegisterStatusState> = (state: RegisterStatusState, incomingAction: Action) => {
    const action = incomingAction as KnownAction;
    switch (action.type) {
        case "REGISTER_USER":
            return action.payload
        case "CLEAR_STATUS":
            return unloadedState
        case LOCATION_CHANGE:
            return unloadedState
    }

    return state || unloadedState
};