<template>
  <div class="page-shell detail-page">
    <div class="page-container">
      <el-button type="text" class="back-link" @click="$router.push('/')">返回首页</el-button>

      <section v-loading="loading" class="detail-card section-card">
        <img :src="book.coverUrl || fallbackCover" class="detail-cover" alt="书籍封面" />
        <div class="detail-content">
          <el-tag effect="dark" size="small">{{ book.categoryName || '未分类' }}</el-tag>
          <el-tag v-if="book.readMode === 'EXTERNAL'" type="warning" size="small" class="mode-tag">站外阅读</el-tag>
          <h1>{{ book.title || '--' }}</h1>
          <p class="author">{{ book.author || '未知作者' }}</p>
          <p class="language">语言：{{ book.language || 'unknown' }}</p>
          <p v-if="book.rankType" class="rank-line">
            榜单：{{ formatRankType(book.rankType) }} · 第 {{ book.rankNo || '--' }} 名
            <span v-if="book.rankValue"> · 值 {{ book.rankValue }}</span>
          </p>
          <div class="summary">
            {{ book.description || '暂无简介' }}
          </div>
          <div class="action-bar">
            <el-button plain round @click="addToBookshelf">加入书架</el-button>
            <el-button type="primary" round @click="goRead">
              {{ book.readMode === 'EXTERNAL' ? '前往原站阅读' : '开始阅读' }}
            </el-button>
          </div>
        </div>
      </section>
    </div>
  </div>
</template>

<script>
import { addBookshelfItem, getBookDetail } from '../api/book';

var fallbackCover = 'https://dummyimage.com/320x460/e8edf2/7b8794&text=BookHub';

export default {
  name: 'BookDetailView',
  data: function data() {
    return {
      loading: false,
      fallbackCover: fallbackCover,
      book: {}
    };
  },
  created: function created() {
    this.fetchDetail();
  },
  methods: {
    fetchDetail: function fetchDetail() {
      var _this = this;
      this.loading = true;
      getBookDetail(this.$route.params.id)
        .then(function onSuccess(data) {
          _this.book = data || {};
        })
        .catch(function onError(error) {
          _this.$message.error(error.message || '获取书籍详情失败');
        })
        .finally(function onFinally() {
          _this.loading = false;
        });
    },
    addToBookshelf: function addToBookshelf() {
      var _this = this;
      addBookshelfItem({
        bookId: Number(this.$route.params.id)
      })
        .then(function onSuccess() {
          _this.$message.success('已加入书架');
        })
        .catch(function onError(error) {
          _this.$message.error(error.message || '加入书架失败');
        });
    },
    formatRankType: function formatRankType(rankType) {
      if (rankType === '17k_male_click') {
        return '17K 男生作品点击榜';
      }
      if (rankType === '17k_female_click') {
        return '17K 女生作品点击榜';
      }
      return rankType || '榜单';
    },
    goRead: function goRead() {
      this.$router.push('/reader/' + this.$route.params.id);
    }
  }
};
</script>

<style scoped>
.detail-card {
  display: flex;
  gap: 32px;
  padding: 32px;
}

.back-link {
  margin-bottom: 12px;
}

.detail-cover {
  width: 320px;
  height: 460px;
  object-fit: cover;
  border-radius: 18px;
  background: #eef2f6;
}

.detail-content {
  flex: 1;
}

.detail-content h1 {
  margin: 18px 0 12px;
  font-size: 34px;
  line-height: 1.25;
}

.author,
.language {
  color: #6c7680;
}

.mode-tag {
  margin-left: 8px;
}

.rank-line {
  color: #8a6337;
}

.summary {
  margin-top: 22px;
  padding: 20px;
  border-radius: 16px;
  background: #f7f9fb;
  color: #3e4750;
  line-height: 1.9;
  white-space: pre-wrap;
}

.action-bar {
  margin-top: 28px;
}

@media (max-width: 768px) {
  .detail-card {
    flex-direction: column;
    padding: 20px;
  }

  .detail-cover {
    width: 100%;
    height: auto;
  }

  .detail-content h1 {
    font-size: 28px;
  }
}
</style>
