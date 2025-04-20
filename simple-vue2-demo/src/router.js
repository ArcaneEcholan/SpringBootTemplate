import Vue from 'vue'
import VueRouter from 'vue-router'
import Home from './Home.vue'
import About from './About.vue'
Vue.use(VueRouter)

const routes = [
    { path: '/', component: Home },
    { path: '/about', component: About }
]

const router = new VueRouter({
    mode: 'history',
    routes
})

export default router
