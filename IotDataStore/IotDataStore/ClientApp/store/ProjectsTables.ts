import { Action, Reducer } from 'redux';
import { AppThunkAction, LocationChangeAction, LOCATION_CHANGE } from './';
import axios from 'axios';

export interface ProjectsTable {
    title: string,
    id: number,
    TableItemModel: string,
    projectId: number,
}

export interface ProjectsTablesState {
    tables: ProjectsTable[],
    error?: string
}

interface GetProjectsTables { type: "GET_PROJECTS_TABLES", payload: ProjectsTable[] }
interface TablesNotFound { type: "TABLES_NOT_FOUND", payload: string }
type KnownAction = GetProjectsTables | LocationChangeAction | TablesNotFound

export const actionCreators = {
    getProjectsTables: (projectId: number): AppThunkAction<KnownAction> => async (dispatch, getState) => {
        axios.get(`/api/Tables/Project/${projectId}`)
            .then(res => {
                dispatch({ type: "GET_PROJECTS_TABLES", payload: res.data })
            })
            .catch(err => {
                dispatch({ type: "TABLES_NOT_FOUND", payload: "This project does not have any tables!"})
            })
    },
};

const unloadedState: ProjectsTablesState = { tables: [] }

export const reducer: Reducer<ProjectsTablesState> = (state: ProjectsTablesState, incomingAction: Action) => {
    const action = incomingAction as KnownAction;
    switch (action.type) {
        case "GET_PROJECTS_TABLES":
            return { tables: action.payload }
        case "TABLES_NOT_FOUND":
            const errorState = Object.assign({}, unloadedState)
            errorState.error = action.payload
            return errorState
        case LOCATION_CHANGE:
            return unloadedState
    }

    return state || unloadedState
};