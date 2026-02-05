<template>
    <div>
        <Button @click="test(1)">
            管理员
        </Button>
        <Button @click="test(2)">
            导师
        </Button>
        <Button @click="test(3)">
            学生
        </Button>
        <Button @click="test(4)">
            退出
        </Button>
    </div>
</template>

<script setup lang="ts">
    import axios from 'axios';
    import Button from '../ui/button/Button.vue';

    const test = async (number: number) => {
        try {
            const res = await axios.get("/api/user/test")
        } catch (e) {
            console.log(e);
        }
    }
</script>