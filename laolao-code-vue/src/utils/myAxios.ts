import axios from "axios";
import { toast } from "vue-sonner"
import router from '@/router'
import { useUserStore } from '@/stores/UserStore'

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
            // 登录页、注册页未登录是正常状态，不弹"登录已过期"气泡
            const isAuthPage = router.currentRoute.value.path === '/sign-in'
                || router.currentRoute.value.path === '/sign-up'
            switch (status) {
                case 401:
                    if (!isAuthPage) {
                        router.replace('/sign-in')
                        toast.info("登录已过期，请重新登录！")
                        const userStore = useUserStore()
                        userStore.clearUser()
                    }
                    break
                case 403:
                    // 跳转到用户首页
                    router.replace('/')
                    toast.error("非管理员，无权限！")
                    break
                default:
                    // 优先显示后端返回的错误信息，没有再用兜底
                    const msg = error.response.data?.msg
                    toast.error(msg || "未知错误！")
            }
        }
        return Promise.reject(error)
    })

export default request