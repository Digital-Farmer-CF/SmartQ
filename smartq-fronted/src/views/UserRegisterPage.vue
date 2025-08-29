<template>
  <!-- 🔶 页面外层容器 -->
  <div id="userRegisterPage">
    <!-- 🔹 页面标题 -->
    <h2 style="margin-bottom: 16px">用户注册</h2>

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

      <!-- 🔸 表单项：确认密码输入 -->
      <a-form-item field="checkPassword" label="确认密码">
        <a-input-password
          v-model="form.checkPassword"
          placeholder="请再次输入密码"
        />
      </a-form-item>

      <!-- 🔸 表单项：提交按钮 -->
      <a-form-item>
        <a-button type="primary" html-type="submit" style="width: 120px">
          注册
        </a-button>
      </a-form-item>
    </a-form>
  </div>
</template>

<script setup lang="ts">
/* 🔶 引入各种工具函数和组件 */
import { reactive } from "vue"; // 用来创建响应式数据
import message from "@arco-design/web-vue/es/message"; // 提示信息组件（弹出成功或失败提示）
import { useRouter } from "vue-router"; // Vue 路由，用来跳转页面
import { useLoginUserStore } from "@/store/userStore"; // 用户登录的状态管理
import { userRegisterUsingPost } from "@/api/userController"; // 登录接口函数（OpenAPI 自动生成）
import API from "@/api"; // 引入接口类型定义

/* 🔶 定义表单数据对象，绑定给输入框使用 */
const form = reactive({
  userAccount: "", // 用户账号
  userPassword: "", // 用户密码
  checkPassword: "", // 确认密码
} as API.UserRegisterRequest); // 使用接口自动生成的类型，避免字段写错

/* 🔶 初始化路由工具和用户 Store */
const router = useRouter(); // 控制跳转页面
const loginUserStore = useLoginUserStore(); // 用于登录后存用户信息

/* 🔶 表单提交处理函数：点击“登录”按钮会执行 */
const handleSubmit = async () => {
  // 把用户输入的账号密码送去后端验证
  const res = await userRegisterUsingPost(form);

  // 登录成功的处理
  if (res.data.code === 0) {
    await loginUserStore.fetchLoginUser(); // 更新全局用户信息
    message.success("注册成功"); // 弹出“成功”提示
    router.push({
      path: "/user/login", // 跳转到登录页
      replace: true,
    });
  } else {
    // 登录失败的处理
    message.error("注册失败，" + res.data.message); // 弹出“失败”提示
  }
};
</script>
