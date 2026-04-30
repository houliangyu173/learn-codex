<template>
  <div class="reader-page">
    <header class="reader-header">
      <el-button type="text" class="reader-back" @click="$router.back()">返回</el-button>
      <div class="reader-title">{{ readInfo.title || '阅读中' }}</div>
    </header>

    <main v-loading="loading" class="reader-main">
      <iframe
        v-if="readInfo.fileType === 'html' && readInfo.readUrl"
        :src="readInfo.readUrl"
        class="reader-frame"
        frameborder="0"
      />

      <section v-else-if="readInfo.fileType === 'txt'" class="txt-reader">
        <pre>{{ txtContent }}</pre>
      </section>

      <el-empty v-else description="暂不支持当前阅读格式" />
    </main>
  </div>
</template>

<script>
import axios from 'axios';
import { getBookReadInfo } from '../api/book';

export default {
  name: 'ReaderView',
  data: function data() {
    return {
      loading: false,
      readInfo: {},
      txtContent: ''
    };
  },
  created: function created() {
    this.fetchReadInfo();
  },
  methods: {
    fetchReadInfo: function fetchReadInfo() {
      var _this = this;
      this.loading = true;
      getBookReadInfo(this.$route.params.id)
        .then(function onSuccess(data) {
          _this.readInfo = data || {};
          if ((_this.readInfo.fileType || '').toLowerCase() === 'txt' && _this.readInfo.readUrl) {
            _this.fetchTxtContent();
          }
        })
        .catch(function onError(error) {
          _this.$message.error(error.message || '获取阅读内容失败');
        })
        .finally(function onFinally() {
          _this.loading = false;
        });
    },
    fetchTxtContent: function fetchTxtContent() {
      var _this = this;
      axios
        .get(this.readInfo.readUrl)
        .then(function onSuccess(response) {
          _this.txtContent = response.data || '';
        })
        .catch(function onError() {
          _this.$message.error('获取 TXT 内容失败');
        });
    }
  }
};
</script>

<style scoped>
.reader-page {
  min-height: 100vh;
  background: linear-gradient(180deg, #f2ede3 0%, #e9edf4 100%);
}

.reader-header {
  position: sticky;
  top: 0;
  z-index: 10;
  display: flex;
  align-items: center;
  padding: 16px 24px;
  backdrop-filter: blur(18px);
  background: rgba(247, 245, 239, 0.85);
  border-bottom: 1px solid rgba(120, 127, 136, 0.15);
}

.reader-back {
  margin-right: 18px;
}

.reader-title {
  font-size: 18px;
  font-weight: 600;
}

.reader-main {
  min-height: calc(100vh - 66px);
}

.reader-frame {
  width: 100%;
  height: calc(100vh - 66px);
  background: #fff;
}

.txt-reader {
  max-width: 900px;
  margin: 0 auto;
  padding: 36px 24px 60px;
}

.txt-reader pre {
  margin: 0;
  padding: 28px;
  border-radius: 22px;
  background: rgba(255, 255, 255, 0.88);
  box-shadow: 0 18px 45px rgba(39, 58, 74, 0.08);
  font-size: 16px;
  line-height: 2;
  white-space: pre-wrap;
  word-break: break-word;
  font-family: "Source Han Serif SC", "Songti SC", serif;
}
</style>
