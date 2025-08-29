<template>
  <div id="homePage">
    <!-- 🎨 美化搜索栏 -->
    <div class="hero-section">
      <div class="hero-content">
        <h1 class="hero-title">SmartQ 智能问答平台</h1>
        <p class="hero-subtitle">发现优质答题应用，提升知识技能</p>
        <div class="search-container">
          <a-input-search
            v-model="searchKeyword"
            class="search-input"
            placeholder="搜索感兴趣的答题应用..."
            button-text="搜索"
            size="large"
            search-button
            @search="handleSearch"
          />
        </div>
      </div>
    </div>

    <!-- 🎨 美化应用列表 -->
    <div class="content-section">
      <div class="section-header">
        <h2>热门应用</h2>
        <span class="app-count">共 {{ total }} 个应用</span>
      </div>

      <a-list
        class="app-list"
        :grid-props="{
          gutter: [24, 24],
          xs: 1,
          sm: 2,
          md: 3,
          lg: 4,
          xl: 4,
          xxl: 5,
        }"
        :bordered="false"
        :data="dataList"
        :pagination-props="{
          pageSize: searchParams.pageSize,
          current: searchParams.current,
          total,
          showTotal: true,
          showJumper: true,
          showPageSize: true,
        }"
        @page-change="onPageChange"
      >
        <template #item="{ item }">
          <div class="app-card-wrapper">
            <AppCard :app="item" />
          </div>
        </template>

        <template #empty>
          <a-empty description="暂无应用数据">
            <a-button type="primary" @click="$router.push('/add/app')">
              创建第一个应用
            </a-button>
          </a-empty>
        </template>
      </a-list>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, watchEffect } from "vue";
import AppCard from "@/components/AppCard.vue";
import API from "@/api";
import { listAppVoByPageUsingPost } from "@/api/appController";
import message from "@arco-design/web-vue/es/message";
import { REVIEW_STATUS_ENUM } from "@/constant/app";

// 搜索关键词
const searchKeyword = ref("");

// 初始化搜索条件（不应该被修改）
const initSearchParams = {
  current: 1,
  pageSize: 12,
  sortOrder: "descend",
  sortField: "createTime",
};

const searchParams = ref<API.AppQueryRequest>({
  ...initSearchParams,
});
const dataList = ref<API.AppVO[]>([]);
const total = ref<number>(0);

/**
 * 搜索处理
 */
const handleSearch = (value: string) => {
  searchParams.value = {
    ...initSearchParams,
    searchText: value.trim() || undefined,
  };
};

/**
 * 加载数据
 */
const loadData = async () => {
  const params = {
    reviewStatus: REVIEW_STATUS_ENUM.PASS,
    ...searchParams.value,
  };
  const res = await listAppVoByPageUsingPost(params);
  if (res.data.code === 0) {
    dataList.value = res.data.data?.records || [];
    total.value = res.data.data?.total || 0;
  } else {
    message.error("获取数据失败，" + res.data.message);
  }
};

/**
 * 当分页变化时，改变搜索条件，触发数据加载
 */
const onPageChange = (page: number) => {
  searchParams.value = {
    ...searchParams.value,
    current: page,
  };
};

/**
 * 监听 searchParams 变量，改变时触发数据的重新加载
 */
watchEffect(() => {
  loadData();
});
</script>

<style scoped>
#homePage {
  min-height: 100vh;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}

/* 🎨 Hero区域样式 */
.hero-section {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  padding: 80px 20px 60px;
  text-align: center;
  position: relative;
  overflow: hidden;
}

.hero-section::before {
  content: "";
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: url('data:image/svg+xml,<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 1000 100" fill="white" opacity="0.1"><polygon points="0,0 1000,80 1000,100 0,100"/></svg>');
  background-size: cover;
}

.hero-content {
  max-width: 800px;
  margin: 0 auto;
  position: relative;
  z-index: 1;
}

.hero-title {
  font-size: 3.5rem;
  font-weight: 700;
  margin-bottom: 1rem;
  background: linear-gradient(45deg, #fff, #e0e7ff);
  background-clip: text;
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
}

.hero-subtitle {
  font-size: 1.25rem;
  opacity: 0.9;
  margin-bottom: 3rem;
  font-weight: 300;
}

.search-container {
  max-width: 600px;
  margin: 0 auto;
}

.search-input {
  border-radius: 50px !important;
  box-shadow: 0 10px 30px rgba(0, 0, 0, 0.2);
  border: none !important;
}

.search-input :deep(.arco-input) {
  border-radius: 50px 0 0 50px;
  border: none;
  padding: 16px 24px;
  font-size: 16px;
}

.search-input :deep(.arco-input-search-btn) {
  border-radius: 0 50px 50px 0;
  background: linear-gradient(45deg, #ff6b6b, #ffa500);
  border: none;
  padding: 0 32px;
}

/* 🎨 内容区域样式 */
.content-section {
  background: #f8fafc;
  padding: 60px 20px;
  min-height: calc(100vh - 400px);
}

.section-header {
  max-width: 1200px;
  margin: 0 auto 40px;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.section-header h2 {
  font-size: 2rem;
  font-weight: 600;
  color: #1e293b;
  margin: 0;
}

.app-count {
  color: #64748b;
  font-size: 0.9rem;
}

.app-list {
  max-width: 1200px;
  margin: 0 auto;
}

.app-card-wrapper {
  height: 100%;
}

.app-card-wrapper :deep(.arco-list-item) {
  border-radius: 16px;
  overflow: hidden;
  box-shadow: 0 4px 6px -1px rgba(0, 0, 0, 0.1);
  transition: all 0.3s ease;
  background: white;
  border: 1px solid #e2e8f0;
}

.app-card-wrapper :deep(.arco-list-item:hover) {
  transform: translateY(-4px);
  box-shadow: 0 20px 25px -5px rgba(0, 0, 0, 0.1);
}

/* 🎨 分页样式 */
:deep(.arco-pagination) {
  margin-top: 40px;
  justify-content: center;
}

:deep(.arco-pagination-item) {
  border-radius: 8px;
  margin: 0 4px;
}

:deep(.arco-pagination-item-active) {
  background: linear-gradient(45deg, #667eea, #764ba2);
  border-color: transparent;
}

/* 🎨 响应式设计 */
@media (max-width: 768px) {
  .hero-title {
    font-size: 2.5rem;
  }

  .hero-subtitle {
    font-size: 1rem;
  }

  .section-header {
    flex-direction: column;
    gap: 16px;
    text-align: center;
  }
}
</style>
