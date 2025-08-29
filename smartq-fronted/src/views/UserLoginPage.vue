<template>
  <!-- 🔶 页面外层容器 -->
  <div id="userLoginPage">
    <!-- 🔹 页面标题 -->
    <h2 style="margin-bottom: 16px">用户登录</h2>

    <!-- 🔶 登录表单 -->
    <a-form
      style="max-width: 480px; margin: 0 auto"
      label-align="left"
      auto-label-width
      :model="form"
      @submit="handleSubmit"
    >
      <!-- 🔸 表单项：账号输入 -->
      <a-form-item field="userAccount" label="账号">
        <a-input v-model="form.userAccount" placeholder="请输入账号" />
      </a-form-item>

      <!-- 🔸 表单项：密码输入 -->
      <a-form-item field="userPassword" tooltip="密码不少于 8 位" label="密码">
        <a-input-password
          v-model="form.userPassword"
          placeholder="请输入密码"
        />
      </a-form-item>

      <!-- 🔸 表单项：操作按钮行 -->
      <!-- 这里将“登录”按钮和“新用户注册”链接放同一行 -->
      <a-form-item class="action-row">
        <!-- 登录按钮：默认靠左 -->
        <a-button type="primary" html-type="submit" style="width: 120px">
          登录
        </a-button>
        <!-- 新用户注册：靠右推到最右端（即在密码输入框下方最右侧） -->
        <span class="register-link" @click="goToRegister"> 新用户注册 </span>
      </a-form-item>
    </a-form>
  </div>
</template>

<script setup lang="ts">
/* 🔶 引入工具 & API */
import { reactive } from "vue"; // 响应式数据
import message from "@arco-design/web-vue/es/message"; // 提示框
import { useRouter } from "vue-router"; // 路由跳转
import { useLoginUserStore } from "@/store/userStore"; // 用户状态管理
import { userLoginUsingPost } from "@/api/userController"; // 登录接口
import API from "@/api"; // 接口类型定义

/* 🔶 定义表单数据对象 */
const form = reactive({
  userAccount: "", // 存账号
  userPassword: "", // 存密码
} as API.UserLoginRequest);

/* 🔶 初始化路由 & Store */
const router = useRouter();
const loginUserStore = useLoginUserStore();

/* 🔶 表单提交处理：点击“登录” */
const handleSubmit = async () => {
  const res = await userLoginUsingPost(form);
  if (res.data.code === 0) {
    // 登录成功
    await loginUserStore.fetchLoginUser(); // 更新用户信息
    message.success("登录成功"); // 成功提示
    router.push({ path: "/", replace: true }); // 跳转首页
  } else {
    // 登录失败
    message.error("登录失败，" + res.data.message); // 失败提示
  }
};

/* 🔶 新用户注册跳转：点击“新用户注册” */
const goToRegister = () => {
  router.push({ path: "/user/register" });
};
</script>

<style scoped>
/* 🔶 action-row：同一行布局 */
.action-row {
  display: flex; /* 水平排列内部元素 */
  align-items: center; /* 垂直居中对齐 */
  margin-top: 8px; /* 与上方密码输入框保留间距 */
}

/* 🔶 新用户注册链接：靠右推到最右端 */
.register-link {
  margin-left: auto; /* 把它推到最右侧 */
  cursor: pointer; /* 鼠标悬停显示手型 */
  color: #1890ff; /* Arco 默认主题色 */
  font-size: 14px;
}
.register-link:hover {
  text-decoration: underline; /* 悬停下划线 */
}
</style>
