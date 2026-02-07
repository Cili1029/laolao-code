import SignIn from "@/components/common/SignIn.vue";
import SignUp from "@/components/common/SignUp.vue";
import Test from "@/components/common/Test.vue";
import UserHome from "@/components/user/UserHome.vue";
import UserLayout from "@/components/user/UserLayout.vue";
import { createRouter, createWebHistory, type RouteRecordRaw } from "vue-router";

const routes: Array<RouteRecordRaw> = [
    // 注册
    {
        path: '/sign-up',
        component: SignUp,
    },
    // 登录
    {
        path: '/sign-in',
        component: SignIn,
    },
    {
        path: '/test',
        component: Test,
    },
    // 用户端
    {
        path: '/',
        component: UserLayout,
        redirect: '/home',
        children: [
            {
                path: 'home',
                component: UserHome,
            }
        ]
    }
]

const router = createRouter({
    history: createWebHistory(),
    routes: routes
})

export default router