<template>
  <div class="page-shell bookshelf-page">
    <div class="page-container">
      <section class="section-card shelf-header">
        <div>
          <h2>我的书架</h2>
          <p>保存感兴趣的书，并持续记录你的阅读进度。</p>
        </div>
        <div class="shelf-actions">
          <el-button @click="$router.push('/')">返回首页</el-button>
        </div>
      </section>

      <section class="section-card shelf-panel">
        <el-table v-loading="loading" :data="records" stripe>
          <el-table-column label="书籍" min-width="260">
            <template slot-scope="scope">
              <div class="book-cell">
                <img :src="scope.row.coverUrl || fallbackCover" alt="封面" class="cell-cover" />
                <div>
                  <div class="book-title">{{ scope.row.title }}</div>
                  <div class="book-author">{{ scope.row.author || '未知作者' }}</div>
                </div>
              </div>
            </template>
          </el-table-column>
          <el-table-column prop="categoryName" label="分类" width="120" />
          <el-table-column label="进度" min-width="220">
            <template slot-scope="scope">
              <div class="progress-box">
                <el-progress :percentage="scope.row.readProgress || 0" />
                <el-input-number
                  v-model="scope.row.readProgress"
                  :min="0"
                  :max="100"
                  :step="5"
                  size="mini"
                  @change="updateProgress(scope.row)"
                />
              </div>
            </template>
          </el-table-column>
          <el-table-column prop="lastReadTime" label="最近阅读" min-width="180" />
          <el-table-column label="操作" width="150" fixed="right">
            <template slot-scope="scope">
              <el-button type="text" @click="$router.push('/reader/' + scope.row.bookId)">继续阅读</el-button>
            </template>
          </el-table-column>
        </el-table>

        <div class="pagination-box">
          <el-pagination
            background
            layout="prev, pager, next, total"
            :current-page.sync="query.pageNum"
            :page-size="query.pageSize"
            :total="total"
            @current-change="fetchBookshelf"
          />
        </div>
      </section>
    </div>
  </div>
</template>

<script>
import { getBookshelfList, updateBookshelfProgress } from '../api/book';

var fallbackCover = 'https://dummyimage.com/300x420/e8edf2/7b8794&text=BookHub';

export default {
  name: 'BookshelfView',
  data: function data() {
    return {
      loading: false,
      fallbackCover: fallbackCover,
      query: {
        pageNum: 1,
        pageSize: 10
      },
      total: 0,
      records: []
    };
  },
  created: function created() {
    this.fetchBookshelf();
  },
  methods: {
    fetchBookshelf: function fetchBookshelf() {
      var _this = this;
      this.loading = true;
      getBookshelfList(this.query)
        .then(function onSuccess(data) {
          _this.records = data.records || [];
          _this.total = data.total || 0;
        })
        .catch(function onError(error) {
          _this.$message.error(error.message || '获取书架失败');
        })
        .finally(function onFinally() {
          _this.loading = false;
        });
    },
    updateProgress: function updateProgress(row) {
      var _this = this;
      updateBookshelfProgress({
        bookId: row.bookId,
        readProgress: row.readProgress
      })
        .then(function onSuccess(data) {
          row.readProgress = data.readProgress || 0;
          row.lastReadTime = data.lastReadTime;
          _this.$message.success('进度已更新');
        })
        .catch(function onError(error) {
          _this.$message.error(error.message || '进度更新失败');
        });
    }
  }
};
</script>

<style scoped>
.shelf-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 28px 32px;
  margin-bottom: 24px;
}

.shelf-header h2 {
  margin: 0 0 8px;
  font-size: 28px;
}

.shelf-header p {
  margin: 0;
  color: #67727e;
}

.shelf-panel {
  padding: 20px;
}

.book-cell {
  display: flex;
  align-items: center;
  gap: 14px;
}

.cell-cover {
  width: 46px;
  height: 64px;
  border-radius: 10px;
  object-fit: cover;
  background: #eef2f6;
}

.book-title {
  font-weight: 600;
  line-height: 1.5;
}

.book-author {
  color: #7b8794;
  font-size: 13px;
}

.progress-box {
  display: grid;
  grid-template-columns: 1fr auto;
  gap: 12px;
  align-items: center;
}

.pagination-box {
  display: flex;
  justify-content: flex-end;
  padding-top: 20px;
}

@media (max-width: 768px) {
  .shelf-header {
    flex-direction: column;
    align-items: flex-start;
    gap: 16px;
  }

  .progress-box {
    grid-template-columns: 1fr;
  }
}
</style>
