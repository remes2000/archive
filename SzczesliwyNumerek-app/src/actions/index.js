import axios from 'axios'
import { GET_LUCKY_NUMBERS, ADD_EMAIL_RECIPIENT } from './types'

export const getLuckyNumbers = () => async dispatch => {
   const res = await axios.get('https://szczesliwy-numerek-api.herokuapp.com/api/getWeeklySchedule')
   dispatch({ type: GET_LUCKY_NUMBERS, payload: res.data })
}

export const addEmailRecipient = data => dispatch => {
    axios.post('https://szczesliwy-numerek-api.herokuapp.com/api/addNewRecipient', data)
        .then( res => { dispatch({ type: ADD_EMAIL_RECIPIENT, payload: res.data })})
        .catch( err => { dispatch({ type: ADD_EMAIL_RECIPIENT, payload: { errorCode: err.response.status } }) })
}