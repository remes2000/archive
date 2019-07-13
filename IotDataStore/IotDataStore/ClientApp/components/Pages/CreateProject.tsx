import * as React from 'react'
import { RouteComponentProps } from 'react-router-dom';
import styled from 'styled-components'
import * as NewProjectStatusStore from '../../store/NewProjectStatus'
import { connect } from 'react-redux'
import { Container, MoveToTheMiddle, Form, InputContainer, InputTextField, InputLabel, media, SubmitButton, MoveToTheRight, InputTextAreaField } from '../StyledComponents'
import Message from '../Message'
import { ApplicationState } from '../../store';
import { Link } from 'react-router-dom'

const FormContainer = styled.div`
    width: 60rem;
    
    ${media.tablet`
        width: 80%;
        margin: 0 auto;
    `}

    ${media.phone`
        width: 90%;
        margin: 0 auto;
    `}
`

type CreateProjectProps =
    & NewProjectStatusStore.NewProjectStatusState
    & typeof NewProjectStatusStore.actionCreators
    & RouteComponentProps<{}>;

interface CreateProjectState{
    title: string,
    description: string,
    loading: boolean
};

class CreateProject extends React.Component<CreateProjectProps, CreateProjectState> {

    constructor() {
        super()
        this.state = {
            title: '',
            description: '',
            loading: false
        }
    }

    private submitForm = (e: React.FormEvent<HTMLFormElement>) => {
        e.preventDefault()
        this.setState({loading: true})
        this.props.createProject({
            title: this.state.title,
            description: this.state.description
        })
    }
    
    public render() {
        return (
            <MoveToTheMiddle>
                <FormContainer>
                    {(!(this.props.error || this.props.status) && this.state.loading) &&
                        <Message type="neutral" content="loading..." />
                    }
                    {(this.props.error || this.props.status) &&
                        <Message type={this.props.status ? "positive" : "negative"} content={this.props.error || this.props.status} />
                    }
                    {!this.props.status &&
                        <Form onSubmit={this.submitForm}>
                            <InputContainer>
                            <InputLabel>Title</InputLabel>
                            <InputTextField type="text" onChange={e => this.setState({ title: e.target.value })} />
                            </InputContainer>
                            <InputTextAreaField placeholder="Project description..." onChange={e => this.setState({ description: e.target.value })} />
                                <MoveToTheRight>
                                    <SubmitButton type="submit" value="Create Project" />
                                </MoveToTheRight>
                        </Form>
                    }
                    {this.props.id &&
                        <MoveToTheRight>
                            <Link to={`/project/${this.props.id}`} style={{ color: 'white', fontSize: '2rem' }}>Move to project!</Link>
                        </MoveToTheRight>
                    }
                </FormContainer>
            </MoveToTheMiddle>
        )
    }
}

export default connect(
    (state: ApplicationState) => state.newProjectStatus,
    NewProjectStatusStore.actionCreators
)(CreateProject) as typeof CreateProject