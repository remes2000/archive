import * as React from 'react'
import { ApplicationState } from '../../store'
import { connect } from 'react-redux'
import { RouteComponentProps, RouteProps, Route, Redirect, withRouter } from 'react-router-dom'
import ConditionalRoute from './ConditionalRoute'

interface GuestRouteProps extends RouteProps {
    isAuthenticated: boolean
};

class GuestRoute extends React.PureComponent<GuestRouteProps & RouteComponentProps<any>>{
    render() {
        const { isAuthenticated, ...rest } = this.props
        return (
            <ConditionalRoute
                routeCondition={!isAuthenticated}
                redirectTo="/"
                {...rest}
            />
        )
    }
}

const mapStateToProps = (state: ApplicationState, ownProps: RouteProps & RouteComponentProps<any>) => {
    return {
        isAuthenticated: !!state.user.username,
        ...ownProps
    }
}

export default withRouter(connect(mapStateToProps, {})(GuestRoute))