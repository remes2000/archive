import { ADD_EMAIL_RECIPIENT } from "../actions/types"

export default function(state = null, action){
    switch(action.type){
        case ADD_EMAIL_RECIPIENT:
            return action.payload
        default:
            return state
    }
}