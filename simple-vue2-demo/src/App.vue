<template>
  <div id="app">
    <h1>Vue2 Router Demo</h1>
    <div style="margin-bottom: 20px;">
      <button @click="goHome">home</button>
      <button @click="goAbout">about</button>
    </div>
    <router-view />
  </div>
</template>

<script>
export default {
  name: 'App',
  methods: {
    goHome() {
      this.$router.push('/')
    },
    goAbout() {
      this.$router.push('/about')
    }
  }
}
</script>
