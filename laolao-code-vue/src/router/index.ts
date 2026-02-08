import SignIn from "@/components/common/SignIn.vue";
import SignUp from "@/components/common/SignUp.vue";
import Test from "@/components/common/Test.vue";
import Ai from "@/components/user/Ai.vue";
import Exam from "@/components/user/Exam.vue";
import ExamRecord from "@/components/user/ExamRecord.vue";
import Group from "@/components/user/Group.vue";
import Common from "@/components/user/Common.vue";
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
        redirect: 'common',
        children: [
            {
                path: 'common',
                component: Common,
            },
            {
                path: 'group/:id',
                component: Group,
            },
            {
                path: 'exam/:id',
                component: Exam,
            },
            {
                path: 'exam-record/:id',
                component: ExamRecord,
            },
            {
                path: 'ai/:id',
                component: Ai,
            },
        ]
    }
]

const router = createRouter({
    history: createWebHistory(),
    routes: routes
})

export default router