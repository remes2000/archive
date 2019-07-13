import { Action, Reducer } from 'redux';
import { AppThunkAction, LocationChangeAction, LOCATION_CHANGE } from './';
import axios from 'axios'

export interface NewTableStatusState {
    status: string,
    error?: string,
    id?: number
};

export interface NewTableData {
    projectId: number,
    title: string,
    tableItemModel: string
}

interface CreateTable { type: "TABLE_CREATED", payload: NewTableStatusState }
interface TableCreateFailed { type: "TABLE_CREATE_FAILED", payload: NewTableStatusState }

type KnownAction = CreateTable | LocationChangeAction | TableCreateFailed

export const actionCreators = {
    createTable: (newProjectData: NewTableData): AppThunkAction<KnownAction> => async (dispatch, getState) => {
        axios.post('/api/Tables', newProjectData)
            .then(res => {
                dispatch({
                    type: "TABLE_CREATED", payload: {status: "Table has been succesfull created!", id: res.data } })
            })
            .catch(err => {
                dispatch({ type: "TABLE_CREATE_FAILED", payload: { status: '', error: "We've got some troubles with this operation. Try again later!"}})
            })
    },
};

const unloadedState: NewTableStatusState = { status: '' }

export const reducer: Reducer<NewTableStatusState> = (state: NewTableStatusState, incomingAction: Action) => {
    const action = incomingAction as KnownAction;
    switch (action.type) {
        case "TABLE_CREATED":
            return action.payload
        case LOCATION_CHANGE:
            return unloadedState
        case "TABLE_CREATE_FAILED":
            return action.payload
    }

    return state || unloadedState
};