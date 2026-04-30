<template>
  <div class="page-shell admin-page">
    <div class="page-container">
      <section class="section-card admin-header">
        <div>
          <h2>书籍管理</h2>
          <p>执行真实采集、查看日志、完成上下架与联调运营。</p>
        </div>
        <div class="admin-actions">
          <el-button @click="$router.push('/bookshelf')">查看书架</el-button>
          <el-button @click="$router.push('/')">返回前台</el-button>
        </div>
      </section>

      <section class="section-card sync-panel">
        <div class="panel-title">
          <h3>手动同步</h3>
          <span>支持公开书源采集，也支持 17K 榜单元数据同步。</span>
        </div>
        <el-form :inline="true" @submit.native.prevent>
          <el-form-item>
            <el-select v-model="syncForm.sourceChannel" placeholder="来源渠道">
              <el-option label="公开书源" value="gutendex" />
              <el-option label="17K 榜单" value="17k" />
            </el-select>
          </el-form-item>
          <el-form-item v-if="syncForm.sourceChannel === '17k'">
            <el-select v-model="syncForm.rankType" placeholder="榜单类型">
              <el-option label="男生作品点击榜" value="17k_male_click" />
              <el-option label="女生作品点击榜" value="17k_female_click" />
            </el-select>
          </el-form-item>
          <el-form-item>
            <el-input
              v-model="syncForm.keyword"
              clearable
              :disabled="syncForm.sourceChannel === '17k'"
              placeholder="关键字，如 pride"
            />
          </el-form-item>
          <el-form-item>
            <el-input
              v-model="syncForm.language"
              clearable
              :disabled="syncForm.sourceChannel === '17k'"
              placeholder="语言，如 en"
            />
          </el-form-item>
          <el-form-item>
            <el-input
              v-model="syncForm.topic"
              clearable
              :disabled="syncForm.sourceChannel === '17k'"
              placeholder="主题，如 history"
            />
          </el-form-item>
          <el-form-item>
            <el-input-number v-model="syncForm.maxCount" :min="1" :max="20" />
          </el-form-item>
          <el-form-item>
            <el-button type="primary" :loading="syncLoading" @click="handleSync">执行同步</el-button>
          </el-form-item>
        </el-form>
      </section>

      <section class="section-card table-panel">
        <el-table v-loading="loading" :data="books" stripe>
          <el-table-column prop="title" label="书名" min-width="220" />
          <el-table-column prop="author" label="作者" min-width="160" />
          <el-table-column prop="categoryName" label="分类" width="120" />
          <el-table-column prop="source" label="来源" width="120" />
          <el-table-column prop="readMode" label="阅读模式" width="120" />
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
        <el-form :inline="true" class="log-filter" @submit.native.prevent>
          <el-form-item>
            <el-select v-model="logQuery.source" clearable placeholder="来源">
              <el-option label="gutendex" value="gutendex" />
              <el-option label="17k" value="17k" />
            </el-select>
          </el-form-item>
          <el-form-item>
            <el-select v-model="logQuery.status" clearable placeholder="状态">
              <el-option label="SUCCESS" value="SUCCESS" />
              <el-option label="FAILED" value="FAILED" />
            </el-select>
          </el-form-item>
          <el-form-item>
            <el-select v-model="logQuery.triggerType" clearable placeholder="触发方式">
              <el-option label="MANUAL" value="MANUAL" />
            </el-select>
          </el-form-item>
          <el-form-item>
            <el-button type="primary" plain @click="fetchSyncLogs">筛选</el-button>
          </el-form-item>
        </el-form>
        <el-table v-loading="logLoading" :data="syncLogs" stripe>
          <el-table-column prop="source" label="来源" width="120" />
          <el-table-column prop="triggerType" label="触发方式" width="120" />
          <el-table-column prop="successCount" label="成功数" width="100" />
          <el-table-column prop="failCount" label="失败数" width="100" />
          <el-table-column prop="status" label="状态" width="140" />
          <el-table-column prop="message" label="说明" min-width="180" />
          <el-table-column prop="requestParams" label="请求参数" min-width="260" show-overflow-tooltip />
          <el-table-column prop="errorMessage" label="错误信息" min-width="220" show-overflow-tooltip />
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
      syncForm: {
        sourceChannel: 'gutendex',
        rankType: '17k_male_click',
        keyword: 'classic',
        language: 'en',
        topic: '',
        maxCount: 5
      },
      query: {
        pageNum: 1,
        pageSize: 10
      },
      logQuery: {
        pageNum: 1,
        pageSize: 5,
        source: '',
        status: '',
        triggerType: ''
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
      syncBooks(this.syncForm)
        .then(function onSuccess(data) {
          _this.$message.success('同步完成，成功 ' + (data.successCount || 0) + ' 条');
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
      getSyncLogList(this.logQuery)
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

.sync-panel {
  padding: 20px 24px 8px;
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

.log-filter {
  margin-bottom: 8px;
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
