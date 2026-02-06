import axios from "axios";
import { toast } from "vue-sonner"
import router from '@/router'
// import { useUserStore } from '@/stores/UserStore'

const request = axios.create({
    baseURL: '',
    withCredentials: true,
})

// 请求拦截器
request.interceptors.request.use(
    (config) => {
        return config
    },
    (error) => {
        return Promise.reject(error)
    }
)

// 响应拦截器
request.interceptors.response.use(
    response => {
        // 有消息就弹出
        if (response.data.msg != null) {
            if (response.data.code == 1) {
                toast.success(response.data.msg)
            } else if (response.data.code == 0) {
                toast.error(response.data.msg)
            } else {
                toast(response.data.msg)
            }
        }
        return response
    },
    error => {
        // HTTP状态码错误
        if (error.response) {
            const status = error.response.status
            switch (status) {
                case 401:
                    // 跳转到登录页
                    router.replace('/sign-in')
                    toast.info("登录已过期，请重新登录！")
                    // 清空现有user数据
                    // const userStore = useUserStore()
                    // userStore.clearUser()
                    break
                case 403:
                    // 跳转到用户首页
                    router.replace('/')
                    toast.error("非管理员，无权限！")
                    break
                default:
                    toast.error("未知错误！")
            }
        }
        return Promise.reject(error)
    })

export default request