import * as React from 'react'
import { connect, MapStateToPropsParam } from 'react-redux'
import { Redirect, Route, RouteProps, RouteComponentProps, withRouter } from 'react-router'
import { ApplicationState } from '../../store';

export interface ConditionalRouteProps {
    routeCondition: boolean,
    redirectTo: string
}

type RouteComponent = React.StatelessComponent<RouteComponentProps<{}>> | React.ComponentClass<any>

class ConditionalRoute extends React.Component<ConditionalRouteProps & RouteProps, {}> {

    private renderFn = (Component?: RouteComponent) => (props: RouteProps) => {
        if (!Component) {
            return null
        }

        if (this.props.routeCondition) {
            return <Component {...props} />
        }

        const redirectProps = {
            to: {
                pathname: this.props.redirectTo,
                state: { from: props.location },
            },
        }

        return <Redirect {...redirectProps} />
    }

    public render() {
        const { component: Component, routeCondition, redirectTo, ...rest } = this.props
        return <Route {...rest} render={this.renderFn(Component)} />
    }
}

export default ConditionalRoute
