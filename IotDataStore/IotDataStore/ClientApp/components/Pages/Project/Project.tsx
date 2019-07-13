import * as React from 'react'
import { RouteComponentProps } from 'react-router-dom';
import { Container } from '../../StyledComponents'
import styled from 'styled-components'
import { connect } from 'react-redux'
import * as ProjectStore from '../../../store/Project'
import * as UserStore from '../../../store/User'
import { ApplicationState } from '../../../store';
import Message from '../../Message'
import { Route, Link, withRouter } from 'react-router-dom'
import UserRoute from '../../Routes/UserRoute'
import SecretKey from './SecretKey'
import Tables from './Tables'
import AddTable from './AddTable'
import { SubPageContainer, SubPageDescription, SubPageHeader, SubPageNavigationContainer, SubPageNavigationList, SubPageNavigationListItem } from '../../StyledComponents'

interface ProjectCustomProps {
    projectId: number,
    baseUrl: string,
    user: UserStore.UserState
}

type ProjectProps =
    & typeof ProjectStore.actionCreators
    & ProjectStore.ProjectState
    & RouteComponentProps<{}>
    & ProjectCustomProps
    & UserStore.UserState

class Project extends React.Component<ProjectProps> {

    public componentWillMount() {
        this.props.getProject(this.props.projectId)
    }

    constructor() {
        super()
    }

    public render() {

        const urls = {
            homepage: `${this.props.baseUrl}`,
            tables: `${this.props.baseUrl}/tables`,
            secretKey: `${this.props.baseUrl}/secretkey`
        }

        const { pathname } = this.props.location

        return (
            <Container>
                {this.props.notFound &&
                    <Message type="negative" content={this.props.notFound} />
                }
                {!this.props.notFound &&
                    <SubPageContainer>
                        <SubPageHeader>{this.props.title}</SubPageHeader>
                        <SubPageNavigationContainer>
                        <SubPageNavigationList>
                            <SubPageNavigationListItem active={pathname === urls.homepage}><Link to={urls.homepage}>Homepage</Link></SubPageNavigationListItem>
                            <SubPageNavigationListItem active={pathname === urls.tables}><Link to={urls.tables}>Tables</Link></SubPageNavigationListItem>
                            {   this.props.user.id === this.props.userId &&
                                <SubPageNavigationListItem active={pathname === urls.secretKey}><Link to={urls.secretKey}>SECRET KEY</Link></SubPageNavigationListItem>
                            }
                        </SubPageNavigationList>
                    </SubPageNavigationContainer>
                    <SubPageDescription>{this.props.description}</SubPageDescription>
                    <UserRoute path={`${this.props.baseUrl}/secretkey`} component={SecretKey} />
                    <UserRoute path={`${this.props.baseUrl}/table/add`} component={AddTable} />
                    <Route path={`${this.props.baseUrl}/tables`} component={Tables} />
                    </SubPageContainer>
                }
            </Container>
        )
    }
}

const mapStateToProps = (state: ApplicationState, ownProps: RouteComponentProps<any>) => {
    return {
        ...state.project,
        user: state.user,
        projectId: ownProps.match.params.id,
        baseUrl: ownProps.match.url,
        ...ownProps
    }
}

export default withRouter(connect(mapStateToProps, ProjectStore.actionCreators)(Project) as typeof Project);