import * as React from 'react';
import { RouteComponentProps } from 'react-router-dom';
import styled from 'styled-components'
import { Form, InputContainer, InputLabel, InputTextField, MoveToTheMiddle, SubmitButton, MoveToTheRight, media} from '../StyledComponents'
import Message from '../Message'
import { connect } from 'react-redux'
import * as UserStore from '../../store/User'
import { ApplicationState } from '../../store'
import { UserState } from '../../store/User';

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

interface LoginComponentState {
    username: string,
    password: string,
    validateError: string,
    loading: boolean
};

type LoginProps =
    & typeof UserStore.actionCreators
    & UserStore.UserState
    & RouteComponentProps<{}>;

class Login extends React.Component<LoginProps, LoginComponentState> {
    constructor() {
        super()
        this.state = {
            username: "",
            password: "",
            validateError: "",
            loading: false
        }
    }

    private validate = () => {
        if (!(this.state.username && this.state.password))
            return "All fields can't be empty!"
        return ''
    }

    private authenticateUser = (e: React.FormEvent<HTMLFormElement>) => {
        e.preventDefault()
        this.setState({loading: false})

        const validateError = this.validate()
        this.setState({ validateError })
        if (validateError) return

        const userCredentials: UserStore.UserCredentials = {
            username: this.state.username,
            password: this.state.password
        }

        this.setState({ loading: true })
        this.props.authenticateUser(userCredentials)
    }


    public render() {
        return (
            <MoveToTheMiddle>
                <FormContainer>
                    {(this.state.validateError || this.props.error) &&
                        <Message type="negative" content={this.state.validateError || (this.props.error ? this.props.error: "Try again later")} />
                    }
                    {(this.state.loading && !this.props.error) &&
                        <Message type="neutral" content="Loading..." />
                    }
                    <Form onSubmit={this.authenticateUser}>
                        <InputContainer>
                            <InputLabel>username</InputLabel>
                            <InputTextField type="text" onChange={e => this.setState({username: e.target.value})}></InputTextField>
                        </InputContainer>
                        <InputContainer>
                            <InputLabel>password</InputLabel>
                            <InputTextField type="password" onChange={e => this.setState({ password: e.target.value })}></InputTextField>
                        </InputContainer>
                        <MoveToTheRight>
                            <SubmitButton value="log in" type="submit"/>
                        </MoveToTheRight>
                    </Form>
                </FormContainer>
            </MoveToTheMiddle>
        )
    }
}

export default connect(
    (state: ApplicationState) => state.user,
    UserStore.actionCreators
)(Login) as typeof Login;