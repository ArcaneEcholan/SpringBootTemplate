<template>
  <div>
    <el-card>
      <h2>home page</h2>
      <el-button type="primary" @click="fetchData">fetch data</el-button>
      <el-table v-if="items.length" :data="items" style="width: 100%; margin-top: 20px;">
        <el-table-column prop="title" label="title"></el-table-column>
        <el-table-column prop="body" label="body"></el-table-column>
      </el-table>
    </el-card>
  </div>
</template>

<script>
import axios from 'axios'

export default {
  name: 'Home',
  data() {
    return {
      items: []
    }
  },
  methods: {
    fetchData() {
      axios.get('https://jsonplaceholder.typicode.com/posts?_limit=5')
          .then(response => {
            this.items = response.data
          })
          .catch(error => {
            console.error(error)
          })
    }
  }
}
</script>
