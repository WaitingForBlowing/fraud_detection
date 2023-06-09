import Vue from 'vue'
import App from './App.vue'
import router from './router'
import '@/plugins/element'
import '@/plugins/loadcss'
import * as echarts from "echarts"

Vue.config.productionTip = false
new Vue({
  router,
  render: h => h(App)
}).$mount('#app')

Vue.prototype.$echarts = echarts

