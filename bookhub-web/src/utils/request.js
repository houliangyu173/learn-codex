import axios from 'axios';

var service = axios.create({
  timeout: 10000
});

service.interceptors.response.use(
  function onResponse(response) {
    var result = response.data;
    if (result && result.code === 0) {
      return result.data;
    }
    return Promise.reject(new Error((result && result.message) || '请求失败'));
  },
  function onError(error) {
    return Promise.reject(error);
  }
);

export default service;
