import { Action, Reducer } from 'redux';
import { AppThunkAction, LocationChangeAction, LOCATION_CHANGE } from './';
import axios from 'axios'

export interface NewProjectStatusState{
    status: string,
    id?: number,
    error?: string
};

interface NewProjectData {
    title: string,
    description: string
}

interface CreateProject { type: "PROJECT_CREATED", payload: NewProjectStatusState }

type KnownAction = CreateProject | LocationChangeAction

export const actionCreators = {
    createProject: (newProjectData: NewProjectData): AppThunkAction<KnownAction> => async (dispatch, getState) => {
        axios.post('/api/Projects', newProjectData)
            .then(res => {
                dispatch({ type: "PROJECT_CREATED", payload: { status: "New project has been created!", id: res.data.id }})
            })
            .catch(res => {
                dispatch({ type: "PROJECT_CREATED", payload: { error: "We have some troubles with this action. Try again later!", status: '' } })
            })
    },
};

const unloadedState: NewProjectStatusState = { status: '' }

export const reducer: Reducer<NewProjectStatusState> = (state: NewProjectStatusState, incomingAction: Action) => {
    const action = incomingAction as KnownAction;
    switch (action.type) {
        case "PROJECT_CREATED":
            return action.payload
        case LOCATION_CHANGE:
            return unloadedState
    }

    return state || unloadedState
};