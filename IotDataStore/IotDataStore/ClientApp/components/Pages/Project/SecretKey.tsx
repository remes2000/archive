import * as React from 'react'
import { RouteComponentProps } from 'react-router-dom';
import { Container, InputTextField, InputContainer, InputLabel, SubmitButton, MoveToTheRight } from '../../StyledComponents'
import { connect } from 'react-redux'
import * as ProjectStore from '../../../store/Project'
import { ApplicationState } from '../../../store';
import * as copy from 'copy-to-clipboard'

type SecretKeyProps =
    & RouteComponentProps<{}>
    & ProjectStore.ProjectState
    & typeof ProjectStore.actionCreators

class SecretKey extends React.Component<SecretKeyProps> {

    public render() {
        return (
            <div>
                <h2>Secret Key</h2>
                <InputContainer style={{ borderColor: '#22333B'}}>
                    <InputLabel>Secret Key</InputLabel>
                    <InputTextField type="text" disabled placeholder="Click button below to get secret key!" value={this.props.secretKey} />
                </InputContainer>
                <MoveToTheRight>
                    <SubmitButton type="button" value="Copy to clipboard" style={{ borderColor: '#22333B', marginRight: '1rem' }} onClick={() => copy(this.props.secretKey ? this.props.secretKey : '')} />
                    <SubmitButton type="button" value="Get Secret Key" style={{ borderColor: '#22333B' }} onClick={() => this.props.getSecretKey(this.props.id)} />
                </MoveToTheRight>
            </div>
        )
    }
}

export default connect((state: ApplicationState) => state.project, ProjectStore.actionCreators)(SecretKey) as typeof SecretKey