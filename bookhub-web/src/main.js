import Vue from 'vue';
import ElementUI from 'element-ui';
import 'element-ui/lib/theme-chalk/index.css';
import App from './App.vue';
import router from './router';
import './styles/global.css';

Vue.config.productionTip = false;
Vue.use(ElementUI);

new Vue({
  router,
  render: function render(createElement) {
    return createElement(App);
  }
}).$mount('#app');
