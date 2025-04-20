<template>
  <div>
    <el-card>
      <h2>about page</h2>
      <el-button type="success" @click="fetchData">load info</el-button>
      <el-list v-if="info" style="margin-top: 20px;">
        <el-list-item>
          <strong>Title:</strong> {{ info.title }}
        </el-list-item>
        <el-list-item>
          <strong>Description:</strong> {{ info.body }}
        </el-list-item>
      </el-list>
    </el-card>
  </div>
</template>

<script>
import axios from 'axios'

export default {
  name: 'About',
  data() {
    return {
      info: null
    }
  },
  methods: {
    fetchData() {
      axios.get('https://jsonplaceholder.typicode.com/posts/1')
          .then(response => {
            this.info = response.data
          })
          .catch(error => {
            console.error(error)
          })
    }
  }
}
</script>

<style scoped>
/* element ui has no <el-list>, fake it */
.el-list {
  list-style: none;
  padding: 0;
}
.el-list-item {
  padding: 8px 0;
  border-bottom: 1px solid #eee;
}
</style>
