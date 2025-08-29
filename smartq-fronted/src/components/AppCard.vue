<template>
  <a-card class="appCard" hoverable @click="doCardClick">
    <template #cover>
      <div
        :style="{
          height: '184px',
          overflow: 'hidden',
        }"
      >
        <img
          :style="{
            width: '100%',
            height: '100%',
            objectFit: 'contain',
            objectPosition: 'center',
            backgroundColor: '#f8f9fa',
          }"
          :alt="app.appName"
          :src="app.appIcon"
        />
      </div>
    </template>
    <a-card-meta :title="app.appName" :description="app.appDesc">
      <template #avatar>
        <div
          :style="{ display: 'flex', alignItems: 'center', color: '#1D2129' }"
        >
          <a-avatar
            :size="24"
            :image-url="app.user?.userAvatar"
            :style="{ marginRight: '8px' }"
          />
          <a-typography-text
            >{{ app.user?.userName ?? "无名" }}
          </a-typography-text>
        </div>
      </template>
    </a-card-meta>
  </a-card>
</template>

<script setup lang="ts">
import { defineProps, withDefaults } from "vue";
import { useRouter } from "vue-router";
import API from "@/api";

interface Props {
  app: API.AppVO;
}

const props = withDefaults(defineProps<Props>(), {
  app: () => {
    return {};
  },
});

const router = useRouter();
const doCardClick = () => {
  router.push(`/app/detail/${props.app.id}`);
};
</script>

<style scoped>
.appCard {
  cursor: pointer;
}
</style>
