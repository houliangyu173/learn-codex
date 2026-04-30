<template>
  <div class="page-shell admin-page">
    <div class="page-container">
      <section class="section-card admin-header">
        <div>
          <h2>书籍管理</h2>
          <p>执行手动同步、查看书籍状态并完成上下架。</p>
        </div>
        <div class="admin-actions">
          <el-button @click="$router.push('/')">返回前台</el-button>
          <el-button type="primary" :loading="syncLoading" @click="handleSync">手动同步</el-button>
        </div>
      </section>

      <section class="section-card table-panel">
        <el-table v-loading="loading" :data="books" stripe>
          <el-table-column prop="title" label="书名" min-width="220" />
          <el-table-column prop="author" label="作者" min-width="160" />
          <el-table-column prop="categoryName" label="分类" width="120" />
          <el-table-column prop="language" label="语言" width="100" />
          <el-table-column label="状态" width="120">
            <template slot-scope="scope">
              <el-tag :type="scope.row.status === 1 ? 'success' : 'info'">
                {{ scope.row.status === 1 ? '已上架' : '已下架' }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column label="操作" width="160" fixed="right">
            <template slot-scope="scope">
              <el-button
                type="text"
                @click="changeStatus(scope.row, scope.row.status === 1 ? 0 : 1)"
              >
                {{ scope.row.status === 1 ? '下架' : '上架' }}
              </el-button>
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
            @current-change="fetchBooks"
          />
        </div>
      </section>

      <section class="section-card log-panel">
        <div class="panel-title">
          <h3>同步日志</h3>
          <span>查看最近的采集执行记录</span>
        </div>
        <el-table v-loading="logLoading" :data="syncLogs" stripe>
          <el-table-column prop="source" label="来源" width="120" />
          <el-table-column prop="triggerType" label="触发方式" width="120" />
          <el-table-column prop="successCount" label="成功数" width="100" />
          <el-table-column prop="failCount" label="失败数" width="100" />
          <el-table-column prop="status" label="状态" width="140" />
          <el-table-column prop="message" label="说明" min-width="180" />
          <el-table-column prop="startTime" label="开始时间" min-width="180" />
        </el-table>
      </section>
    </div>
  </div>
</template>

<script>
import { getBookList, getSyncLogList, syncBooks, updateBookStatus } from '../../api/book';

export default {
  name: 'AdminBookView',
  data: function data() {
    return {
      loading: false,
      logLoading: false,
      syncLoading: false,
      query: {
        pageNum: 1,
        pageSize: 10
      },
      books: [],
      total: 0,
      syncLogs: []
    };
  },
  created: function created() {
    this.fetchBooks();
    this.fetchSyncLogs();
  },
  methods: {
    fetchBooks: function fetchBooks() {
      var _this = this;
      this.loading = true;
      getBookList(this.query)
        .then(function onSuccess(data) {
          _this.books = data.records || data.list || [];
          _this.total = data.total || 0;
        })
        .catch(function onError(error) {
          _this.$message.error(error.message || '获取后台书籍列表失败');
        })
        .finally(function onFinally() {
          _this.loading = false;
        });
    },
    handleSync: function handleSync() {
      var _this = this;
      this.syncLoading = true;
      syncBooks()
        .then(function onSuccess() {
          _this.$message.success('同步任务已触发');
          _this.fetchBooks();
          _this.fetchSyncLogs();
        })
        .catch(function onError(error) {
          _this.$message.error(error.message || '同步失败');
        })
        .finally(function onFinally() {
          _this.syncLoading = false;
        });
    },
    changeStatus: function changeStatus(row, status) {
      var _this = this;
      updateBookStatus({
        id: row.id,
        status: status
      })
        .then(function onSuccess() {
          _this.$message.success(status === 1 ? '上架成功' : '下架成功');
          _this.fetchBooks();
        })
        .catch(function onError(error) {
          _this.$message.error(error.message || '状态更新失败');
        });
    },
    fetchSyncLogs: function fetchSyncLogs() {
      var _this = this;
      this.logLoading = true;
      getSyncLogList({
        pageNum: 1,
        pageSize: 5
      })
        .then(function onSuccess(data) {
          _this.syncLogs = data.records || [];
        })
        .catch(function onError(error) {
          _this.$message.error(error.message || '获取同步日志失败');
        })
        .finally(function onFinally() {
          _this.logLoading = false;
        });
    }
  }
};
</script>

<style scoped>
.admin-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 28px 32px;
  margin-bottom: 24px;
}

.admin-header h2 {
  margin: 0 0 8px;
  font-size: 28px;
}

.admin-header p {
  margin: 0;
  color: #67727e;
}

.admin-actions {
  display: flex;
  gap: 12px;
}

.table-panel {
  padding: 20px 20px 8px;
  margin-bottom: 24px;
}

.pagination-box {
  display: flex;
  justify-content: flex-end;
  padding: 20px 0 10px;
}

.log-panel {
  padding: 20px;
}

.panel-title {
  display: flex;
  align-items: baseline;
  gap: 12px;
  margin-bottom: 16px;
}

.panel-title h3 {
  margin: 0;
  font-size: 20px;
}

.panel-title span {
  color: #67727e;
  font-size: 13px;
}

@media (max-width: 768px) {
  .admin-header {
    flex-direction: column;
    align-items: flex-start;
    gap: 16px;
  }
}
</style>
