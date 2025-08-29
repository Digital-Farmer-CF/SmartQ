// main.ts
import { createApp } from "vue";
import { createPinia } from "pinia"; // ← 新增
import App from "./App.vue";
import router from "./router";
import ArcoVue from "@arco-design/web-vue";
import "@arco-design/web-vue/dist/arco.css";
import "@/access";
import "./styles/global.css"; // 🎨 引入全局样式

const app = createApp(App);
app
  .use(createPinia()) // ← 一定要先挂 Pinia
  .use(ArcoVue)
  .use(router)
  .mount("#app");
