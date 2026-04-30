import Vue from 'vue';
import Router from 'vue-router';
import Home from '../views/Home.vue';
import BookDetail from '../views/BookDetail.vue';
import Reader from '../views/Reader.vue';
import AdminBook from '../views/admin/AdminBook.vue';

Vue.use(Router);

export default new Router({
  mode: 'hash',
  routes: [
    {
      path: '/',
      name: 'Home',
      component: Home
    },
    {
      path: '/book/:id',
      name: 'BookDetail',
      component: BookDetail
    },
    {
      path: '/reader/:id',
      name: 'Reader',
      component: Reader
    },
    {
      path: '/admin/books',
      name: 'AdminBook',
      component: AdminBook
    }
  ]
});
