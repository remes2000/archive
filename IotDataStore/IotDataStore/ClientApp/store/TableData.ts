import { Action, Reducer } from 'redux';
import { AppThunkAction, LocationChangeAction, LOCATION_CHANGE } from './';
import axios from 'axios';

export interface DataItemInterface {
    [key: string]: any
}

export interface TableDataState {
    items: DataItemInterface,
    error?: string
}

interface TableDataItem {
    id: number,
    tableId: number,
    data: string
}

interface GetTableData { type: "GET_TABLE_DATA", payload: DataItemInterface[] }
interface TableDataNotFound { type: "TABLE_DATA_NOT_FOUND", payload: string }
type KnownAction = GetTableData | LocationChangeAction | TableDataNotFound

export const actionCreators = {
    getTableData: (tableId: number): AppThunkAction<KnownAction> => async (dispatch, getState) => {
        axios.get(`/api/Measurements/Table/${tableId}`)
            .then(res => {
                const items = res.data.map((d: TableDataItem) => ({ id: d.id, tableId: d.tableId, data: JSON.parse(d.data) }))
                dispatch({ type: "GET_TABLE_DATA", payload: items})
            })
            .catch(err => {
                dispatch({ type: "TABLE_DATA_NOT_FOUND", payload: "This table does not contain any measurements!" })
            })
    },
};

const unloadedState: TableDataState = { items: [] }

export const reducer: Reducer<TableDataState> = (state: TableDataState, incomingAction: Action) => {
    const action = incomingAction as KnownAction;
    switch (action.type) {
        case "GET_TABLE_DATA":
            return { items: action.payload }
        case "TABLE_DATA_NOT_FOUND":
            const errorState = Object.assign({}, unloadedState)
            errorState.error = action.payload
            return errorState
        case LOCATION_CHANGE:
            return unloadedState
    }

    return state || unloadedState
};