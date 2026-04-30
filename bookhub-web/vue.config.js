module.exports = {
  devServer: {
    port: 3000,
    proxy: {
      '/book': {
        target: 'http://localhost:8080',
        changeOrigin: true
      },
      '/admin': {
        target: 'http://localhost:8080',
        changeOrigin: true
      },
      '/bookshelf': {
        target: 'http://localhost:8080',
        changeOrigin: true
      }
    }
  }
};
