import { combineReducers } from 'redux'
import luckyNumbersReducer from './luckyNumbersReducer'
import notificationFormReducer from './notificationFormReducer'

export default combineReducers({
    luckyNumbers: luckyNumbersReducer,
    notificationForm: notificationFormReducer
})