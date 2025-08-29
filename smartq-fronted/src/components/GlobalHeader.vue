<template>
  <!-- @vue-ignore -->
  <!-- eslint-disable-next-line -->
  <a-row id="globalHeader" align="center" wrap="false">
    <a-col flex="auto">
      <a-menu
        mode="horizontal"
        :default-selected-keys="selectedKeys"
        @menu-item-click="doMenuClick"
      >
        <a-menu-item
          key="0"
          :style="{ padding: 0, marginRight: '38px' }"
          disabled
        >
          <div class="title-bar">
            <img class="logo" src="../assets/logo.png" />
            <div class="title">SmartQ</div>
          </div>
        </a-menu-item>
        <a-menu-item v-for="item in visibleRoutes" :key="item.path">
          {{ item.name }}
        </a-menu-item>
      </a-menu>
    </a-col>
    <a-col flex="150px">
      <div v-if="loginUserStore.loginUser.id">
        <!-- 用户下拉菜单 -->
        <a-dropdown>
          <a-button type="text">
            <div style="display: flex; align-items: center">
              <div style="position: relative; width: 32px; height: 32px">
                <img
                  v-if="loginUserStore.loginUser.userAvatar"
                  :src="loginUserStore.loginUser.userAvatar"
                  style="
                    width: 32px;
                    height: 32px;
                    border-radius: 50%;
                    object-fit: cover;
                  "
                />
                <div
                  v-else
                  style="
                    width: 32px;
                    height: 32px;
                    border-radius: 50%;
                    background-color: #1890ff;
                    color: white;
                    font-size: 14px;
                    display: flex;
                    align-items: center;
                    justify-content: center;
                  "
                >
                  {{ getUserAvatarText() }}
                </div>
              </div>
              <span style="margin-left: 8px">
                {{ loginUserStore.loginUser.userName ?? "无名" }}
              </span>
            </div>
          </a-button>
          <template #content>
            <a-doption @click="goToProfile">
              <template #icon>
                <icon-user />
              </template>
              个人中心
            </a-doption>
            <a-doption @click="doLogout">
              <template #icon>
                <icon-poweroff />
              </template>
              退出登录
            </a-doption>
          </template>
        </a-dropdown>
      </div>
      <div v-else>
        <a-button type="primary" href="/user/login">登录</a-button>
      </div>
    </a-col>
  </a-row>
</template>

<script setup lang="ts">
import { routes } from "@/router/routes";
import { useRouter } from "vue-router";
import { ref, computed } from "vue";
import { useLoginUserStore } from "@/store/userStore";
import checkAccess from "@/access/checkAccess";
import { userLogoutUsingPost } from "@/api/userController";
import message from "@arco-design/web-vue/es/message";
import ACCESS_ENUM from "@/access/accessEnum";
import { IconUser, IconPoweroff } from "@arco-design/web-vue/es/icon";

const loginUserStore = useLoginUserStore();
const router = useRouter();
const selectedKeys = ref(["/"]);

router.afterEach((to) => {
  selectedKeys.value = [to.path as any];
});

const doMenuClick = (key: string) => {
  router.push({
    path: key,
  });
};

// 跳转到个人中心
const goToProfile = () => {
  router.push("/user/profile");
};

/**
 * 获取用户头像文字
 */
const getUserAvatarText = () => {
  const userName = loginUserStore.loginUser.userName;
  if (userName && userName.trim()) {
    return userName.charAt(0).toUpperCase();
  }
  return "用";
};

// 🔥 关键修改：改为响应式计算属性
const visibleRoutes = computed(() => {
  return routes.filter((item) => {
    if (item.meta?.hideInMenu) {
      return false;
    }
    // 根据权限过滤菜单
    if (!checkAccess(loginUserStore.loginUser, item.meta?.access as string)) {
      return false;
    }
    return true;
  });
});

const doLogout = async () => {
  try {
    const res = await userLogoutUsingPost();
    if (res.data.code === 0) {
      message.success("已退出登录");
      loginUserStore.setLoginUser({
        userName: "未登录",
        userRole: ACCESS_ENUM.NOT_LOGIN,
      } as any);
      router.push("/");
    } else {
      message.error("退出失败，" + res.data.message);
    }
  } catch (e) {
    message.error("网络异常，退出失败");
  }
};
</script>

<style scoped>
#globalHeader {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  padding: 0 24px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.15);
}

.title-bar {
  display: flex;
  align-items: center;
}

.logo {
  height: 48px;
  width: 48px;
  margin-right: 16px;
  transition: transform 0.3s ease;
}

.logo:hover {
  transform: scale(1.05);
}

.title {
  color: white;
  font-weight: bold;
  font-size: 20px;
  text-shadow: 0 2px 4px rgba(0, 0, 0, 0.2);
}

/* 🎨 菜单整体样式 */
:deep(.arco-menu-horizontal) {
  background: transparent;
  border-bottom: none;
  line-height: 64px;
}

/* 🎨 菜单项默认状态 - 完全透明 */
:deep(.arco-menu-item) {
  background: transparent !important;
  color: rgba(255, 255, 255, 0.9) !important;
  border-radius: 8px;
  margin: 0 4px;
  font-weight: 500;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  border-bottom: none !important;
}

/* 🎨 菜单项悬停状态 */
:deep(.arco-menu-item:hover) {
  background: rgba(255, 255, 255, 0.15) !important;
  color: white !important;
  transform: translateY(-1px);
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
}

/* 🎨 菜单项选中状态 */
:deep(.arco-menu-item-selected) {
  background: rgba(255, 255, 255, 0.2) !important;
  color: white !important;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

/* 🎨 移除菜单项的下边框 */
:deep(.arco-menu-item::after) {
  display: none;
}

/* 🎨 用户菜单美化 */
:deep(.arco-dropdown-trigger) {
  border-radius: 25px;
  padding: 6px 16px;
  transition: all 0.3s ease;
  background: rgba(255, 255, 255, 0.1);
  backdrop-filter: blur(10px);
}

:deep(.arco-dropdown-trigger:hover) {
  background: rgba(255, 255, 255, 0.2);
  transform: translateY(-1px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
}

:deep(.arco-btn-text) {
  color: white !important;
  border: none;
  font-weight: 500;
}

/* 🎨 登录按钮美化 */
:deep(.arco-btn-primary) {
  background: linear-gradient(45deg, #ff6b6b, #ffa500);
  border: none;
  border-radius: 25px;
  padding: 8px 24px;
  font-weight: 600;
  transition: all 0.3s ease;
  box-shadow: 0 4px 12px rgba(255, 107, 107, 0.3);
}

:deep(.arco-btn-primary:hover) {
  transform: translateY(-2px);
  box-shadow: 0 8px 20px rgba(255, 107, 107, 0.4);
}
</style>
