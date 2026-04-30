<template>
  <div class="page-shell home-page">
    <div class="page-container">
      <section class="hero section-card">
        <div class="hero-copy">
          <span class="hero-tag">BookHub</span>
          <h1>探索公开电子书，沉浸式阅读每一本好书</h1>
          <p>支持搜索、分类筛选和在线阅读，快速构建你的数字书单。</p>
        </div>
        <div class="hero-action">
          <el-button type="primary" round @click="$router.push('/admin/books')">进入后台</el-button>
        </div>
      </section>

      <section class="filter-panel section-card">
        <el-form :inline="true" @submit.native.prevent>
          <el-form-item>
            <el-input
              v-model="query.keyword"
              placeholder="搜索书名 / 作者"
              clearable
              @keyup.enter.native="handleSearch"
            />
          </el-form-item>
          <el-form-item>
            <el-select v-model="query.categoryId" clearable placeholder="选择分类">
              <el-option
                v-for="item in categories"
                :key="item.value"
                :label="item.label"
                :value="item.value"
              />
            </el-select>
          </el-form-item>
          <el-form-item>
            <el-button type="primary" @click="handleSearch">搜索</el-button>
            <el-button @click="handleReset">重置</el-button>
          </el-form-item>
        </el-form>
      </section>

      <section class="book-grid">
        <el-skeleton :rows="6" animated :loading="loading">
          <div class="grid-list">
            <article
              v-for="item in books"
              :key="item.id"
              class="book-card section-card"
              @click="goDetail(item.id)"
            >
              <img :src="item.coverUrl || fallbackCover" class="cover" alt="封面" />
              <div class="book-info">
                <h3>{{ item.title }}</h3>
                <p class="author">{{ item.author || '未知作者' }}</p>
                <div class="meta-row">
                  <el-tag size="mini" effect="plain">{{ item.categoryName || '未分类' }}</el-tag>
                  <span>{{ item.language || 'unknown' }}</span>
                </div>
                <p class="desc">{{ item.description || '暂无简介' }}</p>
              </div>
            </article>
          </div>
        </el-skeleton>
      </section>

      <section class="pagination-box">
        <el-pagination
          background
          layout="prev, pager, next, total"
          :current-page.sync="query.pageNum"
          :page-size="query.pageSize"
          :total="total"
          @current-change="fetchBooks"
        />
      </section>
    </div>
  </div>
</template>

<script>
import { getBookList, getCategoryList } from '../api/book';

var fallbackCover = 'https://dummyimage.com/300x420/e8edf2/7b8794&text=BookHub';

export default {
  name: 'HomeView',
  data: function data() {
    return {
      loading: false,
      fallbackCover: fallbackCover,
      query: {
        keyword: '',
        categoryId: '',
        pageNum: 1,
        pageSize: 12
      },
      categories: [],
      books: [],
      total: 0
    };
  },
  created: function created() {
    this.fetchCategories();
    this.fetchBooks();
  },
  methods: {
    fetchCategories: function fetchCategories() {
      var _this = this;
      getCategoryList()
        .then(function onSuccess(data) {
          _this.categories = (data || []).map(function mapCategory(item) {
            return {
              label: item.name,
              value: item.id
            };
          });
        })
        .catch(function onError(error) {
          _this.$message.error(error.message || '获取分类列表失败');
        });
    },
    fetchBooks: function fetchBooks() {
      var _this = this;
      this.loading = true;
      getBookList(this.query)
        .then(function onSuccess(data) {
          _this.books = data.records || data.list || [];
          _this.total = data.total || 0;
        })
        .catch(function onError(error) {
          _this.$message.error(error.message || '获取书籍列表失败');
        })
        .finally(function onFinally() {
          _this.loading = false;
        });
    },
    handleSearch: function handleSearch() {
      this.query.pageNum = 1;
      this.fetchBooks();
    },
    handleReset: function handleReset() {
      this.query.keyword = '';
      this.query.categoryId = '';
      this.query.pageNum = 1;
      this.fetchBooks();
    },
    goDetail: function goDetail(id) {
      this.$router.push('/book/' + id);
    }
  }
};
</script>

<style scoped>
.hero {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 40px;
  margin-bottom: 24px;
  background: linear-gradient(135deg, #f5f0df 0%, #dce8f5 100%);
}

.hero-copy {
  max-width: 680px;
}

.hero-tag {
  display: inline-block;
  padding: 6px 12px;
  border-radius: 999px;
  background: rgba(255, 255, 255, 0.75);
  color: #8f5e25;
  font-size: 13px;
  letter-spacing: 1px;
}

.hero h1 {
  margin: 16px 0 12px;
  font-size: 40px;
  line-height: 1.2;
}

.hero p {
  margin: 0;
  color: #5c6773;
  font-size: 16px;
}

.filter-panel {
  padding: 20px 24px 4px;
  margin-bottom: 24px;
}

.book-grid {
  margin-bottom: 24px;
}

.grid-list {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(240px, 1fr));
  gap: 20px;
}

.book-card {
  overflow: hidden;
  cursor: pointer;
  transition: transform 0.2s ease, box-shadow 0.2s ease;
}

.book-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 22px 48px rgba(39, 58, 74, 0.12);
}

.cover {
  width: 100%;
  height: 320px;
  object-fit: cover;
  background: #eef2f6;
}

.book-info {
  padding: 18px;
}

.book-info h3 {
  margin: 0 0 10px;
  font-size: 18px;
  line-height: 1.4;
}

.author {
  margin: 0 0 12px;
  color: #7b8794;
}

.meta-row {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 12px;
  color: #7b8794;
  font-size: 12px;
}

.desc {
  margin: 0;
  color: #5f6b76;
  line-height: 1.7;
  font-size: 13px;
  display: -webkit-box;
  -webkit-line-clamp: 3;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.pagination-box {
  display: flex;
  justify-content: center;
  padding-bottom: 28px;
}

@media (max-width: 768px) {
  .page-container {
    padding: 16px;
  }

  .hero {
    flex-direction: column;
    align-items: flex-start;
    gap: 20px;
    padding: 28px 22px;
  }

  .hero h1 {
    font-size: 30px;
  }
}
</style>
