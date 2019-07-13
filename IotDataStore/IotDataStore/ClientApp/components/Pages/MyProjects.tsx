import * as React from 'react'
import { RouteComponentProps } from 'react-router-dom';
import { Container, MoveToTheMiddle } from '../StyledComponents'
import * as UsersProjectsStore from '../../store/UsersProjects'
import * as UserStore from '../../store/User'
import { connect } from 'react-redux'
import { ApplicationState } from '../../store';
import styled from 'styled-components'
import { Link } from 'react-router-dom'
import { Loading, MoveToTheHorizontalMiddle } from '../StyledComponents'
import Message from '../Message'

interface MyProjectsCustomProps {
    usersProjects: UsersProjectsStore.UsersProjectsState
}

type MyProjectsProps =
    & RouteComponentProps<{}>
    & typeof UsersProjectsStore.actionCreators
    & MyProjectsCustomProps
    & UserStore.UserState

const ListOfProjects = styled.ul`
    list-style-type: none;
    padding: 0;
    background-color: #22333B;
    border-right: .2rem solid #171f23;
    border-left: .2rem solid #171f23;
    margin: 1.5rem;
`

const Project = styled.li`
    width: 100%;
    border-bottom: .2rem dashed #171f23;
    padding: 5rem;
    box-sizing: border-box;

    &:hover{
        background-color: #171f23;
    }

    &:last-child{
        border-bottom: .2rem solid #171f23;
    }
`

const ProjectTitle = styled.h3`
    color: white;
    font-size: 2rem;
`

const ProjectDescription = styled.p`
    color: white;
    font-size: 1.5rem;
`

class MyProjects extends React.Component<MyProjectsProps> {

    public componentWillMount() {
        this.props.getUsersProjects(this.props.id)
    }

    public render() {
        return (
            <Container>
                {this.props.usersProjects.error &&
                    <Message type="negative" content={this.props.usersProjects.error} />
                }
                {(this.props.usersProjects.projects.length === 0 && !this.props.usersProjects.error) &&
                    <MoveToTheHorizontalMiddle>
                        <Loading color="#fff"/>
                    </MoveToTheHorizontalMiddle>
                }
                <ListOfProjects>
                    {
                        this.props.usersProjects.projects.map(project => {
                            return (
                                <Project key={`project${project.id}`}>
                                    <ProjectTitle>{project.title}</ProjectTitle>
                                    <ProjectDescription>{project.description}</ProjectDescription>
                                    <Link to={`/project/${project.id}`} style={{ color: 'white', fontSize: '1.3rem' }}>Go to project...</Link>
                                </Project>
                            )
                        })
                     }
                </ListOfProjects>
            </Container>
        )
    }
}

const mapStateToProps = (state: ApplicationState) => ({usersProjects: state.usersProjects, ...state.user})

export default connect(mapStateToProps, UsersProjectsStore.actionCreators)(MyProjects) as typeof MyProjects