import React, { Component } from 'react'
import validator from 'validator'
import { connect } from 'react-redux'
import * as actions from '../../actions' 

class NotificationForm extends Component{
    constructor(){
        super()

        this.state = {
            email: '',
            number: '',
            errors: {}
        }
    }

    handleChange = event => {
        this.setState({[event.target.getAttribute('name')]: event.target.value})
    }

    handleNumberChange = event => {
        if(!isNaN(event.target.value))
            this.setState({number: event.target.value})
    }

    handleSubmit = e => {
        e.preventDefault()
        const errors = this.validate(this.state)
        this.setState({ errors })
        if (Object.keys(errors).length === 0){
            this.props.addEmailRecipient({email: this.state.email, number: this.state.number})
        }
    }

    validate = data => {
        let errors = {}
        if(!validator.isEmail(data.email)) errors.email = 'Podany email jest nieprawidłowy!'
        if(!data.email) errors.email = 'Uzupełnij to pole!'
        if(!data.number) errors.number = 'Uzupełnij to pole!'
        return errors
    }

    render(){
        return (
            <form style={{margin: '5rem'}} onSubmit={this.handleSubmit}>
                <h3>Ustaw powiadomienie !</h3>
                <p>Wyślemy do Ciebie maila, gdy twój numerek zostanie wylosowany !</p>
                { this.props.notificationForm && this.props.notificationForm.success &&
                    <div className="card-panel teal lighten-2">
                        <h5>Twój e-mail został poprawnie zapisany :) !</h5>
                        <p>Wyślemy do Ciebie wiadomość z samego rana, w dniu w którym twój numer będzie szczęśliwym !</p>
                    </div>
                }
                { this.props.notificationForm && this.props.notificationForm.errorCode &&
                    <div className="card-panel red lighten-2">
                        <h5>Coś poszło nie tak...</h5>
                        { this.props.notificationForm.errorCode === 409 &&
                            <p>Podany adres otrzymuje powiadomienia na temat tego numeru.</p>
                        }
                    </div>
                }
                <div>
                    <input type="text" name="email" value={this.state.email} onChange={this.handleChange}/>
                    <label>E-mail</label>
                    {this.state.errors.email && <p>{this.state.errors.email}</p>}                    
                </div>
                <div>
                    <input type="text" name="number" value={this.state.number} onChange={this.handleNumberChange}/>
                    <label>Twój numer w dzienniku</label>
                    {this.state.errors.number && <p>{this.state.errors.number}</p>}                    
                </div>
                <button style={{marginTop: '2rem'}}class="btn waves-effect waves-light" type="submit" name="action">Wyślij
                    <i class="material-icons right">send</i>
                </button>
            </form>
        )
    }
}

const mapStateToProps = state => ({ notificationForm: state.notificationForm })

export default connect(mapStateToProps, actions)(NotificationForm)