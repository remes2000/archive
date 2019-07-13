import { Action, Reducer } from 'redux';
import { AppThunkAction, LocationChangeAction, LOCATION_CHANGE } from './';
import axios from 'axios'

export interface ProjectState {
    title: string,
    description: string,
    id: number,
    userId: number,
    notFound?: string,
    secretKey?: string
}

interface NotFound {
    message: string
}

interface SecretKey {
    secretKey: string
}

interface GetProject { type: "GET_PROJECT", payload: ProjectState }
interface NotFoundProject { type: "NOT_FOUND_PROJECT", payload: NotFound }
interface GetSecretKey { type: "GET_SECRET_KEY", payload: SecretKey }

type KnownAction = GetProject | NotFoundProject | GetSecretKey

export const actionCreators = {
    getProject: (id: number): AppThunkAction<KnownAction> => async (dispatch, getState) => {
        axios.get(`/api/projects/${id}`)
            .then(res => {
                dispatch({ type: "GET_PROJECT", payload: res.data })
            })
            .catch(err => {
                dispatch({ type: "NOT_FOUND_PROJECT", payload: { message: "Project with this ID does not exist!" } })
            })
    },
    getSecretKey: (id: number): AppThunkAction<KnownAction> => async (dispatch, getState) => {
        axios.get(`/api/projects/${id}/secretkey`)
            .then(res => {
                dispatch({ type: "GET_SECRET_KEY", payload: { secretKey: res.data } })
            })
    }
};

const unloadedState: ProjectState = { title: '', description: '', id: -1, userId: -1}

export const reducer: Reducer<ProjectState> = (state: ProjectState, incomingAction: Action) => {
    const action = incomingAction as KnownAction;
    switch (action.type) {
        case "GET_PROJECT":
            return action.payload
        case "NOT_FOUND_PROJECT":
            const notFoundState = Object.assign({}, unloadedState)
            notFoundState.notFound = action.payload.message
            return notFoundState
        case "GET_SECRET_KEY":
            const apiSecretState = Object.assign({}, state)
            apiSecretState.secretKey = action.payload.secretKey
            return apiSecretState
    }

    return state || unloadedState
};