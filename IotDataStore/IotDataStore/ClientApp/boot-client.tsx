import './css/wysiwyg.css';
import * as React from 'react';
import * as ReactDOM from 'react-dom';
import { AppContainer } from 'react-hot-loader';
import { Provider } from 'react-redux';
import { ConnectedRouter } from 'react-router-redux';
import { createBrowserHistory } from 'history';
import configureStore from './configureStore';
import { ApplicationState }  from './store';
import * as RoutesModule from './routes';
let routes = RoutesModule.routes;
import decode from 'jwt-decode';
import { UserState, FetchUser } from './store/User'
import setAuthorizationHeader from './utils/setAuthorizationHeader';

// Create browser history to use in the Redux store
const baseUrl = document.getElementsByTagName('base')[0].getAttribute('href')!;
const history = createBrowserHistory({ basename: baseUrl });

// Get the application-wide store instance, prepopulating with state from the server where available.
const initialState = (window as any).initialReduxState as ApplicationState;
const store = configureStore(history, initialState);

if (localStorage.iotDataStoreJWT) {
    const payload = decode(localStorage.iotDataStoreJWT)
    const user: UserState = {
        username: payload.unique_name,
        email: payload.email,
        id: parseInt(payload.jti),
        token: localStorage.iotDataStoreJWT
    }
    setAuthorizationHeader(localStorage.iotDataStoreJWT)
    const action: FetchUser = {
        type: "FETCH_USER",
        payload: user
    }
    store.dispatch(action)
}

function renderApp() {
    // This code starts up the React app when it runs in a browser. It sets up the routing configuration
    // and injects the app into a DOM element.
    ReactDOM.render(
        <AppContainer>
            <Provider store={ store }>
                <ConnectedRouter history={ history } children={ routes } />
            </Provider>
        </AppContainer>,
        document.getElementById('react-app')
    );
}

renderApp();

// Allow Hot Module Replacement
if (module.hot) {
    module.hot.accept('./routes', () => {
        routes = require<typeof RoutesModule>('./routes').routes;
        renderApp();
    });
}
