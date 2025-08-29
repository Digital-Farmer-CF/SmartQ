<template>
  <div id="userProfilePage">
    <a-card title="个人信息设置" style="max-width: 600px; margin: 0 auto">
      <a-form :model="form" layout="vertical" @submit="handleSubmit">
        <!-- 头像上传 -->
        <a-form-item label="头像">
          <div class="avatar-container">
            <!-- 修复头像显示 -->
            <div style="position: relative; width: 80px; height: 80px">
              <img
                v-if="form.userAvatar"
                :src="form.userAvatar"
                style="
                  width: 80px;
                  height: 80px;
                  border-radius: 50%;
                  object-fit: cover;
                  border: 2px solid #e5e7eb;
                "
                @error="handleImageError"
                @load="handleImageLoad"
              />
              <div
                v-else
                style="
                  width: 80px;
                  height: 80px;
                  border-radius: 50%;
                  background-color: #1890ff;
                  color: white;
                  font-size: 24px;
                  display: flex;
                  align-items: center;
                  justify-content: center;
                  border: 2px solid #e5e7eb;
                "
              >
                {{ getAvatarText() }}
              </div>
            </div>
            <a-upload
              ref="uploadRef"
              action="/api/file/upload"
              :file-list="fileList"
              :show-file-list="false"
              :data="{ biz: 'user_avatar' }"
              accept="image/*"
              :auto-upload="true"
              @success="handleAvatarSuccess"
              @error="handleAvatarError"
              @before-upload="handleBeforeUpload"
            >
              <a-button type="primary" class="upload-btn" :loading="uploading">
                {{ uploading ? "上传中..." : "上传头像" }}
              </a-button>
            </a-upload>
          </div>
          <div style="color: #666; font-size: 12px; margin-top: 8px">
            支持 JPG、PNG、SVG、WebP 格式，文件大小不超过 1MB
          </div>
        </a-form-item>

        <!-- 用户名 -->
        <a-form-item label="用户名" field="userName">
          <a-input
            v-model="form.userName"
            placeholder="请输入用户名"
            :max-length="20"
          />
        </a-form-item>

        <!-- 用户简介 -->
        <a-form-item label="个人简介" field="userProfile">
          <a-textarea
            v-model="form.userProfile"
            placeholder="请输入个人简介"
            :max-length="200"
            :auto-size="{ minRows: 3, maxRows: 6 }"
          />
        </a-form-item>

        <!-- 提交按钮 -->
        <a-form-item>
          <a-space>
            <a-button type="primary" html-type="submit" :loading="loading">
              保存修改
            </a-button>
            <a-button @click="resetForm">重置</a-button>
          </a-space>
        </a-form-item>
      </a-form>
    </a-card>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from "vue";
import {
  updateMyUserUsingPost,
  getLoginUserUsingGet,
} from "@/api/userController";
import { useLoginUserStore } from "@/store/userStore";
import message from "@arco-design/web-vue/es/message";
import API from "@/api";

const loginUserStore = useLoginUserStore();
const loading = ref(false);
const uploading = ref(false);
const uploadRef = ref();
const fileList = ref([]);

// 表单数据
const form = reactive({
  userName: "",
  userAvatar: "",
  userProfile: "",
} as API.UserUpdateMyRequest);

// 初始表单数据（用于重置）
let initialForm = {};

/**
 * 获取头像文字（用户名首字母或默认）
 */
const getAvatarText = () => {
  if (form.userName && form.userName.trim()) {
    return form.userName.charAt(0).toUpperCase();
  }
  return "用";
};

/**
 * 图片加载成功
 */
const handleImageLoad = () => {
  console.log("头像图片加载成功:", form.userAvatar);
};

/**
 * 图片加载失败
 */
const handleImageError = () => {
  console.log("头像图片加载失败:", form.userAvatar);
  message.error("头像加载失败，请检查图片链接");
};

/**
 * 获取当前用户信息
 */
const loadUserInfo = async () => {
  try {
    const res = await getLoginUserUsingGet();
    console.log("获取用户信息:", res.data);
    if (res.data.code === 0 && res.data.data) {
      const userData = res.data.data;
      form.userName = userData.userName || "";
      form.userAvatar = userData.userAvatar || "";
      form.userProfile = userData.userProfile || "";
      console.log("用户头像:", form.userAvatar);
      // 保存初始数据
      initialForm = { ...form };
    }
  } catch (error) {
    console.error("获取用户信息失败:", error);
    message.error("获取用户信息失败");
  }
};

/**
 * 上传前的验证
 */
const handleBeforeUpload = (file: File) => {
  console.log("准备上传文件:", file.name, file.size);

  // 文件大小验证（1MB = 1024 * 1024 bytes）
  const isLt1M = file.size / 1024 / 1024 < 1;
  if (!isLt1M) {
    message.error("文件大小不能超过 1MB");
    return false;
  }

  // 文件类型验证
  const allowedTypes = [
    "image/jpeg",
    "image/jpg",
    "image/png",
    "image/svg+xml",
    "image/webp",
  ];
  if (!allowedTypes.includes(file.type)) {
    message.error("只支持 JPG、PNG、SVG、WebP 格式的图片");
    return false;
  }

  uploading.value = true;
  return true;
};

/**
 * 头像上传成功回调
 */
const handleAvatarSuccess = async (fileItem: any) => {
  console.log("=== 上传回调开始 ===");
  console.log("fileItem:", fileItem);
  console.log("response:", fileItem.response);

  try {
    if (fileItem.response && fileItem.response.code === 0) {
      const newAvatarUrl = fileItem.response.data;
      console.log("新头像URL:", newAvatarUrl);

      // 直接更新表单
      form.userAvatar = newAvatarUrl;
      console.log("表单更新后:", form.userAvatar);

      // 更新到后端
      const updateData = { userAvatar: newAvatarUrl };
      console.log("提交数据:", updateData);

      const res = await updateMyUserUsingPost(updateData);
      console.log("后端响应:", res.data);

      if (res.data.code === 0) {
        await loginUserStore.fetchLoginUser();
        message.success("头像上传成功");
        initialForm = { ...form };
      } else {
        message.error("保存失败：" + res.data.message);
      }
    } else {
      console.error("上传失败:", fileItem.response);
      message.error("上传失败");
    }
  } catch (error) {
    console.error("处理失败:", error);
    message.error("处理失败");
  } finally {
    uploading.value = false;
    fileList.value = [];
  }
};

/**
 * 头像上传失败回调
 */
const handleAvatarError = (fileItem: any) => {
  console.error("上传失败:", fileItem);
  uploading.value = false;
  message.error("头像上传失败，请重试");
  fileList.value = [];
};

/**
 * 提交表单
 */
const handleSubmit = async () => {
  if (!form.userName?.trim()) {
    message.error("用户名不能为空");
    return;
  }

  loading.value = true;
  try {
    const res = await updateMyUserUsingPost(form);
    if (res.data.code === 0) {
      message.success("更新成功");
      await loginUserStore.fetchLoginUser();
      initialForm = { ...form };
    } else {
      message.error("更新失败：" + res.data.message);
    }
  } catch (error) {
    message.error("更新失败");
  } finally {
    loading.value = false;
  }
};

/**
 * 重置表单
 */
const resetForm = () => {
  Object.assign(form, initialForm);
};

// 页面加载时获取用户信息
onMounted(() => {
  loadUserInfo();
});
</script>

<style scoped>
#userProfilePage {
  min-height: 100vh;
  background: linear-gradient(135deg, #f5f7fa 0%, #c3cfe2 100%);
  padding: 40px 20px;
}

/* 🎨 卡片美化 */
:deep(.arco-card) {
  border-radius: 20px;
  box-shadow: 0 20px 25px -5px rgba(0, 0, 0, 0.1);
  border: none;
  overflow: hidden;
}

:deep(.arco-card-header) {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  padding: 24px;
  border-bottom: none;
}

:deep(.arco-card-header-title) {
  color: white;
  font-size: 1.5rem;
  font-weight: 600;
}

:deep(.arco-card-body) {
  padding: 32px;
  background: white;
}

/* 🎨 表单美化 */
:deep(.arco-form-item-label) {
  font-weight: 600;
  color: #374151;
}

:deep(.arco-input) {
  border-radius: 12px;
  border: 2px solid #e5e7eb;
  transition: all 0.3s ease;
}

:deep(.arco-input:focus) {
  border-color: #667eea;
  box-shadow: 0 0 0 3px rgba(102, 126, 234, 0.1);
}

:deep(.arco-textarea) {
  border-radius: 12px;
  border: 2px solid #e5e7eb;
}

:deep(.arco-textarea:focus) {
  border-color: #667eea;
  box-shadow: 0 0 0 3px rgba(102, 126, 234, 0.1);
}

/* 🎨 按钮美化 */
:deep(.arco-btn-primary) {
  background: linear-gradient(45deg, #667eea, #764ba2);
  border: none;
  border-radius: 12px;
  padding: 12px 32px;
  font-weight: 500;
  transition: all 0.3s ease;
}

:deep(.arco-btn-primary:hover) {
  transform: translateY(-2px);
  box-shadow: 0 8px 15px rgba(102, 126, 234, 0.3);
}

:deep(.arco-btn-secondary) {
  border-radius: 12px;
  border: 2px solid #e5e7eb;
  transition: all 0.3s ease;
}

:deep(.arco-btn-secondary:hover) {
  border-color: #667eea;
  color: #667eea;
}

/* 🎨 头像区域美化 */
.avatar-container {
  display: flex;
  align-items: center;
  gap: 24px;
  padding: 24px;
  background: linear-gradient(135deg, #f8fafc 0%, #e2e8f0 100%);
  border-radius: 16px;
  margin-bottom: 16px;
}

/* 🎨 上传按钮美化 */
:deep(.arco-upload) {
  border-radius: 12px;
}

.upload-btn {
  background: linear-gradient(45deg, #10b981, #059669) !important;
  border: none !important;
  border-radius: 12px !important;
  color: white !important;
  transition: all 0.3s ease !important;
}

.upload-btn:hover {
  transform: translateY(-2px) !important;
  box-shadow: 0 8px 15px rgba(16, 185, 129, 0.3) !important;
}
</style>
