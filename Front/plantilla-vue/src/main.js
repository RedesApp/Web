import Vue from 'vue';
import VueResource from 'vue-resource';
import router from './router/router.js'
import GoogleAuth from 'vue-google-oauth'
import Chart from 'chart.js';

import App from './components/app/app.vue';

Vue.use(VueResource);

Vue.use(GoogleAuth, { client_id: '1033226075080-e30m87jrkr6q9ohg2okp2edt8u2qfjlp.apps.googleusercontent.com' })
Vue.googleAuth().load()
new Vue({
  el: '#app',
  router,
  render: h => h(App)
})

