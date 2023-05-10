import axios from "axios";

const service = axios.create({
    baseURL: 'http://127.0.0.1:8002',
    timeout: 6000
})

const err = error => {
    return Promise.reject(error)
}

service.interceptors.response.use(response => {
    return response.data
}, err)

export default service
