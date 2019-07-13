import { Action, Reducer } from 'redux';
import { AppThunkAction } from './';
import axios from 'axios'

export interface ItemModelInterface {
    [key: string]: any
}

export interface TableState {
    id: number,
    projectId: number,
    title: string,
    tableItemModel: ItemModelInterface,
    error?: string
}

interface GetTableAction { type: "GET_TABLE", payload: TableState }
interface TableNotFound { type: "TABLE_NOT_FOUND", payload: string }

type KnownAction = GetTableAction | TableNotFound

export const actionCreators = {
    getTable: (id: number): AppThunkAction<KnownAction> => async (dispatch, getState) => {
        axios.get(`/api/Tables/${id}`)
            .then(res => {
                const tableItemModel = JSON.parse(res.data.tableItemModel)
                const { id, projectId, title } = res.data
                dispatch({ type: "GET_TABLE", payload: { tableItemModel, id, projectId, title} })
            })
            .catch(err => {
                dispatch({ type: "TABLE_NOT_FOUND", payload: "Table with this ID does not exist!" })
            })
    },
};

const unloadedState: TableState = { id: -1, projectId: -1, title: '', tableItemModel: {} }

export const reducer: Reducer<TableState> = (state: TableState, incomingAction: Action) => {
    const action = incomingAction as KnownAction;
    switch (action.type) {
        case "GET_TABLE":
            return action.payload
        case "TABLE_NOT_FOUND":
            const stateWithError = Object.assign({}, unloadedState)
            stateWithError.error = action.payload
            return stateWithError
    }

    return state || unloadedState
};