import * as React from 'react'
import { RouteComponentProps } from 'react-router';
import { Container, SubPageContainer, SubPageHeader, SubPageNavigationContainer, SubPageNavigationList, SubPageNavigationListItem, MoveToTheRight, Button } from '../../StyledComponents'
import { connect } from 'react-redux'
import EditPen from '../../../resources/svg/edit.svg'
import { Link, Route } from 'react-router-dom'
import { ApplicationState } from '../../../store';
import * as TableStore from '../../../store/Table';
import * as ProjectStore from '../../../store/Project';
import * as UserStore from '../../../store/User';
import ValidJson from './ValidJson'
import ViewData from './ViewData'
import Message from '../../Message'
import EditHomepage from './EditHomepage'
import UserRoute from '../../Routes/UserRoute'

interface TableComponentCustomProps {
    tableId: number;
    projectId: number;
    table: TableStore.TableState;
    project: ProjectStore.ProjectState;
    user: UserStore.UserState;
    baseUrl: string;
}

type TableComponentProps =
    & RouteComponentProps<{}>
    & typeof TableStore.actionCreators
    & typeof ProjectStore.actionCreators
    & TableStore.TableState
    & TableComponentCustomProps

class Table extends React.Component<TableComponentProps>{

    componentWillMount() {
        this.props.getTable(this.props.tableId)
        this.props.getProject(this.props.projectId)
    }

    render() {

        const { table, baseUrl } = this.props

        const urls = {
            homepage: `${baseUrl}`,
            validJson: `${baseUrl}/validjson`,
            data: `${baseUrl}/data`,
            edithomepage: `${baseUrl}/edithomepage`
        }

        const { pathname } = this.props.location

        return (
            <Container>
                {this.props.table.error &&
                    <Message type="negative" content={this.props.table.error} />
                }
                {!this.props.table.error &&
                    <SubPageContainer>
                        <SubPageHeader>{table.title}</SubPageHeader>
                        <SubPageNavigationContainer>
                            <SubPageNavigationList>
                                <SubPageNavigationListItem active={pathname === urls.homepage}>
                                    <Link to={urls.homepage}>homepage</Link>
                                </SubPageNavigationListItem>
                                <SubPageNavigationListItem active={pathname === urls.data} >
                                    <Link to={urls.data}>view data</Link>
                                </SubPageNavigationListItem>
                                {this.props.project.userId === this.props.user.id &&
                                    <div>
                                        <SubPageNavigationListItem active={pathname === urls.validJson}>
                                            <Link to={urls.validJson}>valid json</Link>
                                        </SubPageNavigationListItem>
                                    </div>
                                }
                            </SubPageNavigationList>
                    </SubPageNavigationContainer>
                        {pathname === baseUrl &&
                            <div>
                                <MoveToTheRight>
                                    <Link to={urls.edithomepage} style={{ textDecoration: 'none' }}>
                                        <Button color="#52a34e">
                                            <img src={EditPen} alt="Add Table" style={{ height: '100%', marginRight: '1rem' }} />
                                            Edit homepage
                                        </Button>
                                    </Link>
                                </MoveToTheRight>
                            </div>
                         }
                        <Route path={urls.validJson} component={ValidJson} />
                        <Route path={urls.data} component={ViewData} />
                        <UserRoute path={urls.edithomepage} component={EditHomepage} />
                    </SubPageContainer>
                }
            </Container>
        )
    }
}

const mapStateToProps = (state: ApplicationState, ownProps: RouteComponentProps<any>) => {
    return {
        tableId: ownProps.match.params.id,
        projectId: ownProps.match.params.projectId,
        table: state.table,
        project: state.project,
        user: state.user,
        baseUrl: ownProps.match.url,
    }
}

export default connect(mapStateToProps, { ...TableStore.actionCreators, ...ProjectStore.actionCreators })(Table) as typeof Table