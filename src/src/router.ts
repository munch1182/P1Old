import { createRouter, createWebHashHistory } from 'vue-router';

const routes = [
    {
        path: '/',
        redirect: '/home'
    },
    {
        path: '/home',
        name: 'home',
        component: () => import('./components/HelloWorld.vue')
    }
]

export default createRouter({
    history: createWebHashHistory(),
    routes
})