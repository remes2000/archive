import { Action, Reducer } from 'redux';
import { AppThunkAction, LocationChangeAction, LOCATION_CHANGE } from './';
import axios from 'axios';

export interface UsersProject {
    title: string,
    description: string,
    id: number
}

export interface UsersProjectsState {
    projects: UsersProject[],
    error?: string
}

interface GetUsersProjects { type: "GET_USERS_PROJECTS", payload: UsersProject[] }
interface ProjectsNotFound { type: "PROJECTS_NOT_FOUND", payload: string }
type KnownAction = GetUsersProjects | ProjectsNotFound | LocationChangeAction

export const actionCreators = {
    getUsersProjects: (id: number): AppThunkAction<KnownAction> => async (dispatch, getState) => {
        axios.get(`/api/Projects/Owner/${id}`)
            .then(res => {
                dispatch({type: "GET_USERS_PROJECTS", payload: res.data})
            })
            .catch(err => {
                dispatch({ type: "PROJECTS_NOT_FOUND", payload: "You don't have any projects yet!" })
            })
    },
};

const unloadedState: UsersProjectsState = {projects: []}

export const reducer: Reducer<UsersProjectsState> = (state: UsersProjectsState, incomingAction: Action) => {
    const action = incomingAction as KnownAction;
    switch (action.type) {
        case "GET_USERS_PROJECTS":
            return { projects: action.payload }
        case "PROJECTS_NOT_FOUND":
            const errorState = Object.assign({}, unloadedState)
            errorState.error = action.payload
            return errorState
        case LOCATION_CHANGE:
            return unloadedState
    }

    return state || unloadedState
};