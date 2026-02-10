import SignIn from "@/components/common/SignIn.vue";
import SignUp from "@/components/common/SignUp.vue";
import Test from "@/components/common/Test.vue";
import Ai from "@/components/user/Ai.vue";
import ExamTool from "@/components/user/exam/ExamTool.vue";
import ExamRecord from "@/components/user/ExamRecord.vue";
import Group from "@/components/user/Group.vue";
import Common from "@/components/user/Common.vue";
import UserLayout from "@/components/user/UserLayout.vue";
import { createRouter, createWebHistory, type RouteRecordRaw } from "vue-router";
import Exam from "@/components/user/exam/Exam.vue";
import ExamInfo from "@/components/user/exam/ExamInfo.vue";

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
        redirect: '/group',
        children: [
            {
                path: 'group',
                component: Common,
                children: [
                    {
                        path: ':id',
                        component: Group,
                    }
                ]
            },
            {
                path: 'exam',
                component: Common,
                children: [
                    {
                        path: ':id',
                        component: ExamInfo,
                    }
                ]
            },
            {
                path: 'exam-record',
                component: Common,
                children: [
                    {
                        path: ':id',
                        component: ExamRecord,
                    }
                ]
            },
            {
                path: 'ai',
                component: Common,
                children: [
                    {
                        path: ':id',
                        component: Ai,
                    }
                ]
            },
        ]
    }
]

const router = createRouter({
    history: createWebHistory(),
    routes: routes
})

export default router