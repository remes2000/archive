import * as React from 'react'
import { RouteComponentProps } from 'react-router';
import { connect } from 'react-redux'
import { ApplicationState } from '../../../store';
import * as UserStore from '../../../store/User';
import * as ProjectStore from '../../../store/Project';
import * as ProjectsTablesStore from '../../../store/ProjectsTables';
import { MoveToTheRight, Button, Loading, MoveToTheHorizontalMiddle } from '../../StyledComponents'
import styled from 'styled-components'
import { Link } from 'react-router-dom'
import AddCross from '../../../resources/svg/add.svg'
import Message from '../../Message'

const TableContainer = styled.div`
    border-bottom: .05rem dashed black;
    padding: 2rem;
    margin: 3rem;
`

const TableHeader = styled.header`
    font-size: 2rem;
    font-weight: bold;
    margin-bottom: 2rem;
`

interface TablesCustomProps {
    user: UserStore.UserState,
    project: ProjectStore.ProjectState,
    baseUrl: string
}

type TablesProps =
    & RouteComponentProps<{}>
    & ProjectsTablesStore.ProjectsTablesState
    & typeof ProjectsTablesStore.actionCreators
    & TablesCustomProps

class Tables extends React.Component<TablesProps>{

    componentDidUpdate() {
        if (this.props.project.id !== -1 && this.props.tables.length === 0 )
            this.props.getProjectsTables(this.props.project.id)
    }

    componentWillMount() {
        if (this.props.project.id !== -1 && this.props.tables.length === 0)
            this.props.getProjectsTables(this.props.project.id)
    }

    render() {

        const { user, project } = this.props

        return (
            <div>
                {(!this.props.error && this.props.tables.length === 0) &&
                    <MoveToTheHorizontalMiddle>
                        <Loading color="#22333B"/>
                    </MoveToTheHorizontalMiddle>
                }
                {this.props.error &&
                    <Message type="negative" content={this.props.error} />
                }
                {user.id === project.userId &&
                    <MoveToTheRight>
                        <Link to={`${this.props.baseUrl}/table/add`} style={{ textDecoration: 'none' }}>
                            <Button color="#52a34e">
                                <img src={AddCross} alt="Add Table" style={{ height: '100%', marginRight: '1rem' }} />
                                Add Table
                            </Button>
                        </Link>
                    </MoveToTheRight>
                }
                {this.props.tables.map(table => {
                    return (
                        <TableContainer>
                            <TableHeader>{table.title}</TableHeader>
                            <Link to={`/table/${table.id}/project/${project.id}`} style={{ fontSize: '1.6rem' }}>
                                See more...
                            </Link>
                        </TableContainer>
                    )
                })}
            </div>
        )
    }
}

const mapStateToProps = (state: ApplicationState, ownProps: any) => {
    const baseUrlWithTables = ownProps.match.url
    const baseUrl = baseUrlWithTables.slice(0, baseUrlWithTables.lastIndexOf('/'))
    return {
        user: state.user,
        project: state.project,
        baseUrl,
        ...ownProps,
        ...state.projectsTables
    }
}

export default connect(mapStateToProps, ProjectsTablesStore.actionCreators)(Tables) as typeof Tables