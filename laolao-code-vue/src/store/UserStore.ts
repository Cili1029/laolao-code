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
        }
    }
})