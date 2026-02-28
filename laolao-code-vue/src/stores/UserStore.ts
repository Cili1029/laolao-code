import axios from "@/utils/myAxios"
import { defineStore } from 'pinia'

interface User {
    id: number
    avatar: string
    username: string
    name: string
    role: number
}

export const useUserStore = defineStore('user', {
    state: () => ({
        user: {
            id: 0,
            username: '',
            name: '',
            role: 0
        } as User,
        signedIn: false
    }),

    actions: {
        setUser(user: User) {
            this.user.id = user.id
            this.user.username = user.username
            this.user.name = user.name
            this.user.role = user.role
            this.signedIn = true
        },

        clearUser() {
            this.user.id = 0
            this.user.username = ''
            this.user.name = ''
            this.user.role = 0
            this.signedIn = false
        },

        async getInfo() {
            try {
                const res = await axios.get("/api/user/info")
                if (res.data.code === 1) {
                    this.setUser(res.data.data)
                }
            } catch (e) {
                console.log(e);
            }
        }
    }
})