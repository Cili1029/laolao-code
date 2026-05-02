import SignIn from "@/components/common/SignIn.vue";
import SignUp from "@/components/common/SignUp.vue";
import Test from "@/components/common/Test.vue";
import ExamTool from "@/components/user/exam/ExamTool.vue";
import UserExamReport from "@/components/user/report/UserExamReport.vue";
import Group from "@/components/user/group/Group.vue";
import { createRouter, createWebHistory, type RouteRecordRaw } from "vue-router";
import ExamInfo from "@/components/user/exam/ExamInfo.vue";
import CreateExam from "@/components/user/exam/CreateExam.vue";
import GradeExam from "@/components/user/exam/GradeExam.vue";
import MyTeam from "@/components/user/group/MyTeam.vue";
import MyExam from "@/components/user/exam/MyExam.vue";
import MyReport from "@/components/user/report/MyReport.vue";
import UserLayout from "@/components/user/UserLayout.vue";
import QuestionBank from "@/components/user/question/QuestionBank.vue";

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
    {
        path: '/',
        component: UserLayout,
        redirect: '/group',
        children: [
            {
                path: 'group',
                component: MyTeam,
            },
            {
                path: 'group/:id',
                component: Group,
            },
            {
                path: 'exam',
                component: MyExam,
            },
            {
                path: 'exam/:id',
                component: ExamInfo,
            },
            {
                path: 'exam/start/:id',
                component: ExamTool,
            },
            {
                path: 'exam/create/:id',
                component: CreateExam,
            },
            {
                path: 'grade/:id',
                component: GradeExam,
            },
            {
                path: 'user-report',
                component: MyReport,
            },
            {
                path: 'user-report/:id',
                component: UserExamReport,
            },
            {
                path: 'question-bank',
                component: QuestionBank,
            },
        ]
    }
]

const router = createRouter({
    history: createWebHistory(),
    routes: routes
})

export default router