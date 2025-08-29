<template>
  <div id="doAnswerPage">
    <a-card>
      <h1>{{ app.appName }}</h1>
      <p>{{ app.appDesc }}</p>
      <h2 style="margin-bottom: 16px">
        {{ current }}、{{ currentQuestion?.title }}
      </h2>
      <div>
        <a-radio-group
          direction="vertical"
          v-model="currentAnswer"
          :options="questionOptions"
          @change="doRadioChange"
        />
      </div>
      <div style="margin-top: 24px">
        <a-space size="large">
          <a-button
            type="primary"
            circle
            v-if="current < questionContent.length"
            :disabled="!currentAnswer"
            @click="current += 1"
          >
            下一题
          </a-button>
          <a-button
            type="primary"
            v-if="current === questionContent.length"
            :loading="submitting"
            :disabled="!currentAnswer || submitting"
            circle
            @click="doSubmit"
          >
            {{ submitting ? "评分中" : "查看结果" }}
          </a-button>
          <a-button v-if="current > 1" circle @click="current -= 1">
            上一题
          </a-button>
        </a-space>
      </div>
    </a-card>
  </div>
</template>

<script setup lang="ts">
import {
  computed,
  defineProps,
  reactive,
  ref,
  watchEffect,
  withDefaults,
} from "vue";
import API from "@/api";
import { getAppVoByIdUsingGet } from "@/api/appController";
import { listQuestionVoByPageUsingPost } from "@/api/questionController";
import { addUserAnswerUsingPost } from "@/api/userAnswerController";
import message from "@arco-design/web-vue/es/message";
import { useRouter } from "vue-router";

interface Props {
  appId: string;
}

const props = withDefaults(defineProps<Props>(), {
  appId: () => {
    return "";
  },
});

const router = useRouter();

// 应用信息
const app = ref<API.AppVO>({});
// 题目内容
const questionContent = ref<API.QuestionContent[]>([]);
// 当前题目
const current = ref(1);
// 当前题目
const currentQuestion = ref<API.QuestionContent>({});
// 当前答案
const currentAnswer = ref<string>("");
// 答案列表
const answerList = reactive<string[]>([]);
// 是否正在提交结果
const submitting = ref(false);
// 是否已经提交过（防重复提交）
const hasSubmitted = ref(false);

// 计算选项
const questionOptions = computed(() => {
  return currentQuestion.value?.options
    ? currentQuestion.value.options.map((option: any) => {
        return {
          label: `${option.key}. ${option.value}`,
          value: option.key,
        };
      })
    : [];
});

/**
 * 加载数据
 */
const loadData = async () => {
  if (!props.appId) {
    return;
  }

  try {
    // 获取 app
    let res: any = await getAppVoByIdUsingGet({
      id: props.appId as any,
    });
    if (res.data.code === 0) {
      app.value = res.data.data as any;
    } else {
      message.error("获取应用失败，" + res.data.message);
      return;
    }

    // 获取题目
    res = await listQuestionVoByPageUsingPost({
      appId: props.appId as any,
      current: 1,
      pageSize: 1,
      sortField: "createTime",
      sortOrder: "descend",
    });
    if (res.data.code === 0 && res.data.data?.records) {
      questionContent.value = res.data.data.records[0].questionContent;
    } else {
      message.error("获取题目失败，" + res.data.message);
    }
  } catch (error) {
    console.error("加载数据失败:", error);
    message.error("加载数据失败，请刷新重试");
  }
};

// 获取旧数据
watchEffect(() => {
  loadData();
});

// 改变 current 题号后，会自动更新当前题目和答案
watchEffect(() => {
  currentQuestion.value = questionContent.value[current.value - 1];
  currentAnswer.value = answerList[current.value - 1];
});

/**
 * 选中选项后，保存选项记录
 * @param value
 */
const doRadioChange = (value: string) => {
  answerList[current.value - 1] = value;
};

/**
 * 提交答案 - 优化版本
 */
const doSubmit = async () => {
  // 防重复提交检查
  if (submitting.value || hasSubmitted.value) {
    message.warning("正在提交中，请勿重复点击");
    return;
  }

  if (!props.appId || !answerList || answerList.length === 0) {
    message.error("答案不完整，请检查后重试");
    return;
  }

  // 检查是否所有题目都已回答
  const unansweredCount = answerList.filter((answer) => !answer).length;
  if (unansweredCount > 0) {
    message.error(`还有 ${unansweredCount} 道题未回答，请完成后提交`);
    return;
  }

  submitting.value = true;

  try {
    console.log("提交答案:", {
      appId: props.appId,
      choices: answerList,
    });

    const res = await addUserAnswerUsingPost({
      appId: props.appId as any,
      choices: answerList,
      // 🔥 不再传递ID，让后端自动生成
    });

    if (res.data.code === 0 && res.data.data) {
      hasSubmitted.value = true; // 标记已提交
      message.success("提交成功！正在跳转到结果页面...");

      // 延迟跳转，避免用户重复操作
      setTimeout(() => {
        router.push(`/answer/result/${res.data.data}`);
      }, 1000);
    } else {
      const errorMessage = res.data.message || "提交失败";
      console.error("提交答案失败:", res.data);
      message.error("提交答案失败：" + errorMessage);
    }
  } catch (error: any) {
    console.error("提交答案异常:", error);

    // 处理不同类型的错误
    if (error.response?.data?.message) {
      message.error(error.response.data.message);
    } else if (error.message) {
      message.error("网络错误：" + error.message);
    } else {
      message.error("提交失败，请检查网络连接后重试");
    }
  } finally {
    submitting.value = false;
  }
};

// 在 watchEffect 中调用
watchEffect(() => {
  loadData();
});

// 页面离开前的确认
window.addEventListener("beforeunload", (e) => {
  if (answerList.length > 0 && !hasSubmitted.value) {
    e.preventDefault();
    e.returnValue = "您的答题进度尚未提交，确定要离开吗？";
  }
});
</script>

<style scoped>
#doAnswerPage {
  padding: 24px;
}
</style>
