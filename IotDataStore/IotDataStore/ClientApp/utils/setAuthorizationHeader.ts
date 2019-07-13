import axios from 'axios'

export default (token?: string) => {
    if (token)
        axios.defaults.headers.common.authorization = `Bearer ${token}`
    else
        delete axios.defaults.headers.common.authorization
}