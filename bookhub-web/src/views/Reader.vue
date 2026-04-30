<template>
  <div class="reader-page">
    <header class="reader-header">
      <el-button type="text" class="reader-back" @click="$router.back()">返回</el-button>
      <div class="reader-title">{{ readInfo.title || '阅读中' }}</div>
    </header>

    <main v-loading="loading" class="reader-main">
      <section v-if="readInfo.readMode === 'EXTERNAL'" class="external-reader">
        <div class="external-card">
          <h2>当前作品来自站外来源</h2>
          <p>这本书不在 BookHub 站内分发正文，点击下方按钮后会前往原站继续阅读。</p>
          <el-button type="primary" round @click="goExternal">前往原站阅读</el-button>
        </div>
      </section>

      <iframe
        v-else-if="readInfo.fileType === 'html' && htmlContent"
        :srcdoc="htmlContent"
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
import { getBookReadContent, getBookReadInfo, updateBookshelfProgress } from '../api/book';

export default {
  name: 'ReaderView',
  data: function data() {
    return {
      loading: false,
      readInfo: {},
      htmlContent: '',
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
          _this.syncProgress(10);
          if (_this.readInfo.readMode !== 'EXTERNAL') {
            _this.fetchReadContent();
          }
        })
        .catch(function onError(error) {
          _this.$message.error(error.message || '获取阅读内容失败');
        })
        .finally(function onFinally() {
          _this.loading = false;
        });
    },
    fetchReadContent: function fetchReadContent() {
      var _this = this;
      getBookReadContent(this.$route.params.id)
        .then(function onSuccess(response) {
          var fileType = (_this.readInfo.fileType || '').toLowerCase();
          if (fileType === 'html') {
            _this.htmlContent = response || '';
            _this.txtContent = '';
            return;
          }
          _this.txtContent = response || '';
          _this.htmlContent = '';
        })
        .catch(function onError() {
          _this.$message.error('获取阅读内容失败');
        });
    },
    syncProgress: function syncProgress(progress) {
      updateBookshelfProgress({
        bookId: Number(this.$route.params.id),
        readProgress: progress
      }).catch(function onError() {
        return null;
      });
    },
    goExternal: function goExternal() {
      if (this.readInfo.readUrl) {
        window.location.href = this.readInfo.readUrl;
      }
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

.external-reader {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: calc(100vh - 66px);
  padding: 24px;
}

.external-card {
  max-width: 620px;
  padding: 36px 32px;
  border-radius: 24px;
  background: rgba(255, 255, 255, 0.92);
  box-shadow: 0 18px 45px rgba(39, 58, 74, 0.08);
}

.external-card h2 {
  margin-top: 0;
}

.external-card p {
  color: #5f6b76;
  line-height: 1.8;
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
