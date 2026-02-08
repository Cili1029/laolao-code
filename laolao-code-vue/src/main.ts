import { createApp } from 'vue'
import './style.css'
import App from './App.vue'
import router from './router'
import { createPinia } from 'pinia'
import dayjs from 'dayjs'
import 'dayjs/locale/zh-cn'
import updateLocale from 'dayjs/plugin/updateLocale'
import relativeTime from 'dayjs/plugin/relativeTime'

const app = createApp(App)
const pinia = createPinia()
dayjs.locale('zh-cn')
dayjs.extend(updateLocale)
dayjs.updateLocale('zh-cn', {
    relativeTime: {
        future: '%s后',
        past: '%s前',
        s: '几秒',
        m: '1 分钟',
        mm: '%d 分钟',
        h: '1 小时',
        hh: '%d 小时',
        d: '1 天',
        dd: '%d 天',
        M: '1 个月',
        MM: '%d 个月',
        y: '1 年',
        yy: '%d 年'
    }
})
dayjs.extend(relativeTime)



app.use(router)
app.use(pinia)
app.mount('#app')