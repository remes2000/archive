import * as React from 'react';
import { RouteComponentProps } from 'react-router-dom';
import { connect } from 'react-redux';
import { ApplicationState } from '../../store';
import * as RegisterStatusStore from '../../store/RegisterStatus';
import { CounterState } from '../../store/Counter';
import { UserRegisterForm } from '../../store/RegisterStatus';
import Message from '../Message'
import styled from 'styled-components'
import { MoveToTheMiddle, InputContainer, InputLabel, InputTextField, SubmitButton, MoveToTheRight, Form, media } from '../StyledComponents'

interface RegisterComponentState {
    username: string;
    email: string;
    password: string;
    repeatPassword: string;
    validateError: any;
    loading: boolean
}

type RegisterProps =
    & typeof RegisterStatusStore.actionCreators
    & RegisterStatusStore.RegisterStatusState
    & RouteComponentProps<{}>;

const FormContainer = styled.div`
    width: 70rem;
    
    ${media.tablet`
        width: 80%;
        margin: 0 auto;
    `}

    ${media.phone`
        width: 90%;
        margin: 0 auto;
    `}
`

class Register extends React.Component<RegisterProps, RegisterComponentState> {

    constructor() {
        super()
        this.state = {
            username: '',
            email: '',
            password: '',
            repeatPassword: '',
            validateError: '',
            loading: false
        }
    }

    private validate = () => {
        const { password, repeatPassword, username, email} = this.state
        if (password !== repeatPassword)
            return "Password and repeat password fields must be equal!"
        if (!(password && email && username && repeatPassword))
            return "All fields can't be empty!"
        if (!this.validateEmail(email))
            return 'This email adress is incorrect!'
        return ''
    }

    private registerUser = (e: React.FormEvent<HTMLFormElement>) => {
        e.preventDefault();
        this.props.clearStatus()
        this.setState({loading: false})

        const validateError = this.validate()
        this.setState({ validateError })
        if (validateError)
            return 

        this.setState({loading: true})
        const newUserData: UserRegisterForm = {
            username: this.state.username,
            email: this.state.email,
            password: this.state.password
        }
        this.props.registerUser(newUserData)
    }

    private validateEmail = (email: string) => {
        var re = /^(([^<>()\[\]\\.,;:\s@"]+(\.[^<>()\[\]\\.,;:\s@"]+)*)|(".+"))@((\[[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\])|(([a-zA-Z\-0-9]+\.)+[a-zA-Z]{2,}))$/;
        return re.test(String(email).toLowerCase());
    }

    public render() {
        return (
            <MoveToTheMiddle>
                <FormContainer>
                    {this.props.message &&
                        <Message content={this.props.message} type="positive" />
                    }
                    {(this.props.error || this.state.validateError) &&
                        <Message content={this.state.validateError || this.props.error} type="negative" />
                    }
                    {this.state.loading && !(this.props.message || this.props.error) &&
                        <Message content="Loading..." type="neutral" />
                    }
                    <Form onSubmit={e => this.registerUser(e)}>
                        <InputContainer>
                            <InputLabel>Username</InputLabel>
                            <InputTextField type="text" name="username" id="username" value={this.state.username} onChange={e => this.setState({ username: e.target.value })} />
                        </InputContainer>
                        <InputContainer>
                            <InputLabel>Email</InputLabel>
                            <InputTextField type="text" name="email" id="email" value={this.state.email} onChange={e => this.setState({ email: e.target.value })} />
                        </InputContainer>
                        <InputContainer>
                            <InputLabel>Password</InputLabel>
                            <InputTextField type="password" name="password" id="password" value={this.state.password} onChange={e => this.setState({ password: e.target.value })} />
                        </InputContainer>
                        <InputContainer>
                            <InputLabel>Repeat Password</InputLabel>
                            <InputTextField type="password" name="repeat-password" id="repeat-password" value={this.state.repeatPassword} onChange={e => this.setState({ repeatPassword: e.target.value })} />
                        </InputContainer>
                        <MoveToTheRight>
                            <SubmitButton type="submit" value="sign in"/>
                        </MoveToTheRight>
                    </Form>
                </FormContainer>
            </MoveToTheMiddle>
        )
    }
}

export default connect(
    (state: ApplicationState) => state.registerStatus,
    RegisterStatusStore.actionCreators
)(Register) as typeof Register;