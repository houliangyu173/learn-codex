import request from '../utils/request';

export function getBookList(params) {
  return request({
    url: '/book/list',
    method: 'get',
    params: params
  });
}

export function getBookDetail(id) {
  return request({
    url: '/book/' + id,
    method: 'get'
  });
}

export function getBookReadInfo(id) {
  return request({
    url: '/book/read/' + id,
    method: 'get'
  });
}

export function getBookReadContent(id) {
  return request({
    url: '/book/read/content/' + id,
    method: 'get'
  });
}

export function getCategoryList() {
  return request({
    url: '/book/category/list',
    method: 'get'
  });
}

export function syncBooks(data) {
  return request({
    url: '/admin/book/sync',
    method: 'post',
    data: data || {}
  });
}

export function updateBookStatus(data) {
  return request({
    url: '/admin/book/status',
    method: 'put',
    data: data
  });
}

export function getSyncLogList(params) {
  return request({
    url: '/admin/book/sync/log/list',
    method: 'get',
    params: params
  });
}

export function addBookshelfItem(data) {
  return request({
    url: '/bookshelf/add',
    method: 'post',
    data: data
  });
}

export function updateBookshelfProgress(data) {
  return request({
    url: '/bookshelf/progress',
    method: 'put',
    data: data
  });
}

export function getBookshelfList(params) {
  return request({
    url: '/bookshelf/list',
    method: 'get',
    params: params
  });
}
