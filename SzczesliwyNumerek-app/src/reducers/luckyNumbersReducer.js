import { GET_LUCKY_NUMBERS } from "../actions/types"

export default function(state = null, action){
    switch(action.type){
        case GET_LUCKY_NUMBERS:
            return action.payload
        default:
            return state
    }
}