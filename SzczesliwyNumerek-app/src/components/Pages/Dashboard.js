import React, { Component } from 'react'
import { connect } from 'react-redux'
import * as actions from '../../actions'

import LuckyNumber from '../LuckyNumber'
import NotificationForm from '../Forms/NotificationForm'

class Dashboard extends Component{

    componentDidMount(){
        this.props.getLuckyNumbers()
    }

    render(){
        return (
            <main>
                <h3 style={{textAlign: 'center', margin: '2.5rem'}}>Szczęśliwe numery na ten tydzień: </h3>
                {  !this.props.luckyNumbers && 
                    <p>Loading...</p>
                }
                {   this.props.luckyNumbers &&
                    <div>
                        <ul style={{
                            display: 'flex',
                            flexWrap: 'wrap',
                            justifyContent: 'center'
                        }}>
                        { this.props.luckyNumbers.weeklySchedule.map( dayData => <li style={{margin: '1rem'}}><LuckyNumber dayData={dayData}/></li>) }
                        </ul>
                        <NotificationForm />
                    </div>
                }
            </main>
        )
    }

}

function mapStateToProps({ luckyNumbers }){
    return { luckyNumbers }
}

export default connect(mapStateToProps, actions)(Dashboard)