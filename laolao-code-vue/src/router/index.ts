import UserHome from "@/components/user/UserHome.vue";
import UserLayout from "@/components/user/UserLayout.vue";
import { createRouter, createWebHistory, type RouteRecordRaw } from "vue-router";

const routes: Array<RouteRecordRaw> = [
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