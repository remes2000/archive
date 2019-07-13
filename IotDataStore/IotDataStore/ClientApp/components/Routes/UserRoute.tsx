import * as React from 'react'
import { ApplicationState } from '../../store'
import { connect } from 'react-redux'
import { RouteComponentProps, RouteProps, Route, Redirect, withRouter } from 'react-router-dom'
import ConditionalRoute from './ConditionalRoute'

interface UserRouteProps extends RouteProps {
    isAuthenticated: boolean
};

class UserRoute extends React.PureComponent<UserRouteProps & RouteComponentProps<any>>{
    render() {
        const { isAuthenticated, ...rest } = this.props
        return (
            <ConditionalRoute
                routeCondition={isAuthenticated}
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

export default withRouter(connect(mapStateToProps, {})(UserRoute))