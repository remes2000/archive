import * as WeatherForecasts from './WeatherForecasts';
import * as Counter from './Counter';
import * as User from './User';
import * as RegisterStatus from './RegisterStatus'
import * as NewProjectStatus from './NewProjectStatus'
import * as UsersProjects from './UsersProjects'
import * as Project from './Project'
import * as NewTableStatus from './NewTableStatus'
import * as ProjectsTables from './ProjectsTables'
import * as Table from './Table'
import * as TableData from './TableData'

// The top-level state object
export interface ApplicationState {
    counter: Counter.CounterState;
    weatherForecasts: WeatherForecasts.WeatherForecastsState;
    user: User.UserState;
    registerStatus: RegisterStatus.RegisterStatusState;
    newProjectStatus: NewProjectStatus.NewProjectStatusState;
    usersProjects: UsersProjects.UsersProjectsState;
    project: Project.ProjectState;
    newTableStatus: NewTableStatus.NewTableStatusState,
    projectsTables: ProjectsTables.ProjectsTablesState,
    table: Table.TableState,
    tableData: TableData.TableDataState
}

export const LOCATION_CHANGE = '@@router/LOCATION_CHANGE'

export interface LocationChangeAction {
    type: '@@router/LOCATION_CHANGE',
    payload: any
}

// Whenever an action is dispatched, Redux will update each top-level application state property using
// the reducer with the matching name. It's important that the names match exactly, and that the reducer
// acts on the corresponding ApplicationState property type.
export const reducers = {
    counter: Counter.reducer,
    weatherForecasts: WeatherForecasts.reducer,
    user: User.reducer,
    registerStatus: RegisterStatus.reducer,
    newProjectStatus: NewProjectStatus.reducer,
    usersProjects: UsersProjects.reducer,
    project: Project.reducer,
    newTableStatus: NewTableStatus.reducer,
    projectsTables: ProjectsTables.reducer,
    table: Table.reducer,
    tableData: TableData.reducer
};

// This type can be used as a hint on action creators so that its 'dispatch' and 'getState' params are
// correctly typed to match your store.
export interface AppThunkAction<TAction> {
    (dispatch: (action: TAction) => void, getState: () => ApplicationState): void;
}
