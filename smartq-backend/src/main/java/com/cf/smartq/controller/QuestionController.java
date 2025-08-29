package com.cf.smartq.controller;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cf.smartq.annotation.AuthCheck;
import com.cf.smartq.common.BaseResponse;
import com.cf.smartq.common.DeleteRequest;
import com.cf.smartq.common.ErrorCode;
import com.cf.smartq.common.ResultUtils;
import com.cf.smartq.constant.UserConstant;
import com.cf.smartq.exception.BusinessException;
import com.cf.smartq.exception.ThrowUtils;
import com.cf.smartq.manager.AiManager;
import com.cf.smartq.model.dto.question.*;
import com.cf.smartq.model.entity.App;
import com.cf.smartq.model.entity.Question;
import com.cf.smartq.model.entity.User;
import com.cf.smartq.model.enums.AppTypeEnum;
import com.cf.smartq.model.vo.QuestionVO;
import com.cf.smartq.service.AppService;
import com.cf.smartq.service.QuestionService;
import com.cf.smartq.service.UserService;
import com.zhipu.oapi.service.v4.model.ModelData;
import io.reactivex.Flowable;
import io.reactivex.Scheduler;
import io.reactivex.schedulers.Schedulers;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 问题接口
 *
 */
@RestController
@RequestMapping("/question")
@Slf4j
public class QuestionController {

    @Resource
    private QuestionService questionService;

    @Resource
    private UserService userService;

    @Resource
    private AppService appService;

    @Resource
    private AiManager aiManager;

    @Resource
    private Scheduler vipScheduler;


    // region 增删改查

    /**
     * 创建问题
     *
     * @param questionAddRequest
     * @param request
     * @return
     */
    @PostMapping("/add")
    @ApiOperation(value = "创建问题")
    public BaseResponse<Long> addQuestion(@RequestBody QuestionAddRequest questionAddRequest, HttpServletRequest request) {
        ThrowUtils.throwIf(questionAddRequest == null, ErrorCode.PARAMS_ERROR);
        // 将实体类和 DTO 进行转换
        Question question = new Question();
        BeanUtils.copyProperties(questionAddRequest, question);
        List<QuestionContent> questionContent = questionAddRequest.getQuestionContent();
        question.setQuestionContent(JSONUtil.toJsonStr(questionContent));
        // todo 填充默认值
        User loginUser = userService.getLoginUser(request);
        question.setUserId(loginUser.getId());
        // 数据校验
        questionService.validQuestion(question, true);
        // 写入数据库
        boolean result = questionService.save(question);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        // 返回新写入的数据 id
        long newQuestionId = question.getId();
        return ResultUtils.success(newQuestionId);
    }

    /**
     * 删除问题
     *
     * @param deleteRequest
     * @param request
     * @return
     */
    @PostMapping("/delete")
    @ApiOperation(value = "删除问题")
    public BaseResponse<Boolean> deleteQuestion(@RequestBody DeleteRequest deleteRequest, HttpServletRequest request) {
        if (deleteRequest == null || deleteRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User user = userService.getLoginUser(request);
        long id = deleteRequest.getId();
        // 判断是否存在
        Question oldQuestion = questionService.getById(id);
        ThrowUtils.throwIf(oldQuestion == null, ErrorCode.NOT_FOUND_ERROR);
        // 仅本人或管理员可删除
        if (!oldQuestion.getUserId().equals(user.getId()) && !userService.isAdmin(request)) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }
        // 操作数据库
        boolean result = questionService.removeById(id);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        return ResultUtils.success(true);
    }

    /**
     * 更新问题（仅管理员可用）
     *
     * @param questionUpdateRequest
     * @return
     */
    @PostMapping("/update")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    @ApiOperation(value = "更新问题")
    public BaseResponse<Boolean> updateQuestion(@RequestBody QuestionUpdateRequest questionUpdateRequest) {
        if (questionUpdateRequest == null || questionUpdateRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        // todo 在此处将实体类和 DTO 进行转换
        Question question = new Question();
        BeanUtils.copyProperties(questionUpdateRequest, question);
        // 数据校验
        questionService.validQuestion(question, false);
        // 判断是否存在
        long id = questionUpdateRequest.getId();
        Question oldQuestion = questionService.getById(id);
        ThrowUtils.throwIf(oldQuestion == null, ErrorCode.NOT_FOUND_ERROR);
        // 操作数据库
        boolean result = questionService.updateById(question);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        return ResultUtils.success(true);
    }

    /**
     * 根据 id 获取问题（封装类）
     *
     * @param id
     * @return
     */
    @GetMapping("/get/vo")
    @ApiOperation(value = "根据id获取问题")
    public BaseResponse<QuestionVO> getQuestionVOById(long id, HttpServletRequest request) {
        ThrowUtils.throwIf(id <= 0, ErrorCode.PARAMS_ERROR);
        // 查询数据库
        Question question = questionService.getById(id);
        ThrowUtils.throwIf(question == null, ErrorCode.NOT_FOUND_ERROR);
        // 获取封装类
        return ResultUtils.success(questionService.getQuestionVO(question, request));
    }

    /**
     * 分页获取问题列表（仅管理员可用）
     *
     * @param questionQueryRequest
     * @return
     */
    @PostMapping("/list/page")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    @ApiOperation(value = "分页获取问题列表(管理员)")
    public BaseResponse<Page<Question>> listQuestionByPage(@RequestBody QuestionQueryRequest questionQueryRequest) {
        long current = questionQueryRequest.getCurrent();
        long size = questionQueryRequest.getPageSize();
        // 查询数据库
        Page<Question> questionPage = questionService.page(new Page<>(current, size),
                questionService.getQueryWrapper(questionQueryRequest));
        return ResultUtils.success(questionPage);
    }

    /**
     * 分页获取问题列表（封装类）
     *
     * @param questionQueryRequest
     * @param request
     * @return
     */
    @PostMapping("/list/page/vo")
    @ApiOperation(value = "分页获取问题列表")
    public BaseResponse<Page<QuestionVO>> listQuestionVOByPage(@RequestBody QuestionQueryRequest questionQueryRequest,
                                                               HttpServletRequest request) {
        long current = questionQueryRequest.getCurrent();
        long size = questionQueryRequest.getPageSize();
        // 限制爬虫
        ThrowUtils.throwIf(size > 20, ErrorCode.PARAMS_ERROR);
        // 查询数据库
        Page<Question> questionPage = questionService.page(new Page<>(current, size),
                questionService.getQueryWrapper(questionQueryRequest));
        // 获取封装类
        return ResultUtils.success(questionService.getQuestionVOPage(questionPage, request));
    }

    /**
     * 分页获取当前登录用户创建的问题列表
     *
     * @param questionQueryRequest
     * @param request
     * @return
     */
    @PostMapping("/my/list/page/vo")
    @ApiOperation(value = "分页获取当前登录用户创建的问题列表")
    public BaseResponse<Page<QuestionVO>> listMyQuestionVOByPage(@RequestBody QuestionQueryRequest questionQueryRequest,
                                                                 HttpServletRequest request) {
        ThrowUtils.throwIf(questionQueryRequest == null, ErrorCode.PARAMS_ERROR);
        // 补充查询条件，只查询当前登录用户的数据
        User loginUser = userService.getLoginUser(request);
        questionQueryRequest.setUserId(loginUser.getId());
        long current = questionQueryRequest.getCurrent();
        long size = questionQueryRequest.getPageSize();
        // 限制爬虫
        ThrowUtils.throwIf(size > 20, ErrorCode.PARAMS_ERROR);
        // 查询数据库
        Page<Question> questionPage = questionService.page(new Page<>(current, size),
                questionService.getQueryWrapper(questionQueryRequest));
        // 获取封装类
        return ResultUtils.success(questionService.getQuestionVOPage(questionPage, request));
    }

    /**
     * 编辑问题（给用户使用）
     *
     * @param questionEditRequest
     * @param request
     * @return
     */
    @PostMapping("/edit")
    @ApiOperation(value = "编辑问题")
    public BaseResponse<Boolean> editQuestion(@RequestBody QuestionEditRequest questionEditRequest, HttpServletRequest request) {
        if (questionEditRequest == null || questionEditRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        // 1. 获取当前登录用户
        User loginUser = userService.getLoginUser(request);
        Long userId = loginUser.getId();

        // 在此处将实体类和 DTO 进行转换
        Question question = new Question();
        BeanUtils.copyProperties(questionEditRequest, question);
        List<QuestionContent> questionContent = questionEditRequest.getQuestionContent();
        question.setQuestionContent(JSONUtil.toJsonStr(questionContent));
        // 3. 设置 userId
        question.setUserId(userId);
        // 数据校验
        questionService.validQuestion(question, false);
        // 判断是否存在
        long id = questionEditRequest.getId();
        Question oldQuestion = questionService.getById(id);
        ThrowUtils.throwIf(oldQuestion == null, ErrorCode.NOT_FOUND_ERROR);
        // 仅本人或管理员可编辑
        if (!oldQuestion.getUserId().equals(loginUser.getId())) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }
        // 操作数据库
        boolean result = questionService.updateById(question);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        return ResultUtils.success(true);
    }

    // endregion

    // region AI 生成题目功能
    private static final String GENERATE_QUESTION_SYSTEM_MESSAGE = "你是一位严谨且专业的出题专家，我会给你如下信息：\n" +
            "```\n" +
            "应用名称，\n" +
            "【【【应用描述】】】，\n" +
            "应用类别，\n" +
            "要生成的题目数，\n" +
            "每个题目的选项数\n" +
            "```\n" +
            "\n" +
            "重要提示：**绝对不要出关于应用本身的题目**，而是要根据应用描述中的主题领域出题。\n" +
            "\n" +
            "出题原则：\n" +
            "1. 如果是「地理知识」应用，出地理常识题（如：哪个国家的首都是巴黎？）\n" +
            "2. 如果是「历史知识」应用，出历史事件题（如：第一次世界大战开始于哪一年？）\n" +
            "3. 如果是「文学知识」应用，出文学作品题（如：《红楼梦》的作者是谁？）\n" +
            "4. 如果是「科学知识」应用，出科学原理题（如：光的传播速度是多少？）\n" +
            "5. 如果是「性格测试」应用，出性格评估题（如：在团队中你更倾向于？A.领导他人 B.配合他人）\n" +
            "6. 如果是「能力评估」应用，出能力水平题（如：解决问题时你通常？A.直接行动 B.深思熟虑）\n" +
            "\n" +
            "严格禁止的题型：\n" +
            "- 不要问「这个应用是什么」\n" +
            "- 不要问「应用的主要用途是什么」\n" +
            "- 不要问任何关于应用介绍、功能、特点的问题\n" +
            "- 题目必须是该领域的专业知识或测评内容\n" +
            "\n" +
            "评分策略：\n" +
            "1. 若应用类别为「得分类」：\n" +
            "   - 知识测试：正确答案10分，错误答案0分\n" +
            "   - 能力评估：不同选项递增得分（2分、4分、6分、8分）\n" +
            "2. 若应用类别为「测评类」：每个选项设置结果属性（如：I、E、S、N等）\n" +
            "\n" +
            "请严格按照以下 JSON 数组格式返回：\n" +
            "```\n" +
            "得分类格式：[{\"options\":[{\"value\":\"选项内容\",\"key\":\"A\",\"score\":10},{\"value\":\"选项内容\",\"key\":\"B\",\"score\":0}],\"title\":\"题目标题\"}]\n" +
            "测评类格式：[{\"options\":[{\"value\":\"选项内容\",\"key\":\"A\",\"result\":\"I\"},{\"value\":\"选项内容\",\"key\":\"B\",\"result\":\"E\"}],\"title\":\"题目标题\"}]\n" +
            "```\n" +
            "最终只返回 JSON 数组，不包含其它内容。";


    /**
     * 生成题目的用户消息
     *
     * @param app
     * @param questionNumber
     * @param optionNumber
     * @return
     */
    private String getGenerateQuestionUserMessage(App app, int questionNumber, int optionNumber) {
        StringBuilder userMessage = new StringBuilder();
        userMessage.append(app.getAppName()).append("\n");
        userMessage.append(app.getAppDesc()).append("\n");
        userMessage.append(AppTypeEnum.getEnumByValue(app.getAppType().toString()).getText() + "类").append("\n");
        userMessage.append(questionNumber).append("\n");
        userMessage.append(optionNumber);
        return userMessage.toString();
    }

    @PostMapping("/ai_generate")
    public BaseResponse<List<QuestionContent>> aiGenerateQuestion(@RequestBody AiGenerateQuestionRequest aiGenerateQuestionRequest) {
        ThrowUtils.throwIf(aiGenerateQuestionRequest == null, ErrorCode.PARAMS_ERROR);
        // 获取参数
        Long appId = aiGenerateQuestionRequest.getAppId();
        int questionNumber = aiGenerateQuestionRequest.getQuestionNumber();
        int optionNumber = aiGenerateQuestionRequest.getOptionNumber();
        // 获取应用信息
        App app = appService.getById(appId);
        ThrowUtils.throwIf(app == null, ErrorCode.NOT_FOUND_ERROR);
        // 封装 Prompt
        String userMessage = getGenerateQuestionUserMessage(app, questionNumber, optionNumber);
        // AI 生成
        String result = aiManager.doSyncStableRequest(GENERATE_QUESTION_SYSTEM_MESSAGE, userMessage);

        // 打印AI接口原始内容
        System.out.println("AI接口原始响应内容: " + result);

        try {
            // 1. 先把AI返回的result解析成JSONObject
            cn.hutool.json.JSONObject root = cn.hutool.json.JSONUtil.parseObj(result);
            cn.hutool.json.JSONObject message = root.getJSONObject("message");
            String content = message.getStr("content");

            System.out.println("AI实际内容 content: " + content);

            // 2. 剥壳，只取中间的 [ ... ] 部分
            if (content != null && content.contains("[")) {
                int first = content.indexOf("[");
                int last = content.lastIndexOf("]");
                if (first != -1 && last != -1 && last > first) {
                    content = content.substring(first, last + 1);
                }
            }

            System.out.println("AI实际内容去壳后 content: " + content);

            // 3. 解析成List<QuestionContent>
            List<QuestionContent> questionContentDTOList = cn.hutool.json.JSONUtil.toList(content, QuestionContent.class);

            // 根据应用类型自动设置选项分数
            if (app.getAppType() == 0) { // 得分类应用
                for (QuestionContent questionContent : questionContentDTOList) {
                    List<QuestionContent.Option> options = questionContent.getOptions();
                    
                    // 根据应用描述判断是知识测试还是能力评估
                    String appDesc = app.getAppDesc().toLowerCase();
                    boolean isKnowledgeTest = appDesc.contains("知识") || appDesc.contains("测试") || 
                                            appDesc.contains("考试") || appDesc.contains("quiz");
                    
                    if (isKnowledgeTest) {
                        // 知识测试类：只有一个正确答案得分，其他选项0分
                        // 假设第一个选项是正确答案（或可以通过其他逻辑判断）
                        for (int i = 0; i < options.size(); i++) {
                            QuestionContent.Option option = options.get(i);
                            if (i == 0) { // 或者使用其他逻辑确定正确答案
                                option.setScore(10); // 正确答案得10分
                            } else {
                                option.setScore(0);  // 错误答案0分
                            }
                        }
                    } else {
                        // 能力评估类：不同选项代表不同能力水平，递增得分
                        for (int i = 0; i < options.size(); i++) {
                            QuestionContent.Option option = options.get(i);
                            option.setScore((i + 1) * 2); // 2分、4分、6分、8分...
                        }
                    }
                }
            }

            return ResultUtils.success(questionContentDTOList);

        } catch (Exception e) {
            System.out.println("AI返回内容解析出错，原始内容: " + result);
            e.printStackTrace();
            throw new BusinessException(ErrorCode.SYSTEM_ERROR);
        }
    }


    @GetMapping("/ai_generate/sse")
    public SseEmitter aiGenerateQuestionSSE(AiGenerateQuestionRequest aiGenerateQuestionRequest, HttpServletRequest request) {
        ThrowUtils.throwIf(aiGenerateQuestionRequest == null, ErrorCode.PARAMS_ERROR);
        // 获取参数
        Long appId = aiGenerateQuestionRequest.getAppId();
        int questionNumber = aiGenerateQuestionRequest.getQuestionNumber();
        int optionNumber = aiGenerateQuestionRequest.getOptionNumber();
        // 获取应用信息
        App app = appService.getById(appId);
        ThrowUtils.throwIf(app == null, ErrorCode.NOT_FOUND_ERROR);
        // 封装 Prompt
        String userMessage = getGenerateQuestionUserMessage(app, questionNumber, optionNumber);
        // 建立 SSE 连接对象，0 表示永不超时
        SseEmitter sseEmitter = new SseEmitter(0L);
        // AI 生成，SSE 流式返回
        Flowable<ModelData> modelDataFlowable = aiManager.doStreamRequest(GENERATE_QUESTION_SYSTEM_MESSAGE, userMessage, null);
        // 左括号计数器，除了默认值外，当回归为 0 时，表示左括号等于右括号，可以截取
        AtomicInteger counter = new AtomicInteger(0);
        // 拼接完整题目
        StringBuilder stringBuilder = new StringBuilder();

        // 获取登录用户
        User loginUser = userService.getLoginUser(request);
        // 默认全局线程池
        Scheduler scheduler = Schedulers.io();
        if ("vip".equals(loginUser.getUserRole())) {
            scheduler = vipScheduler;
        }
        modelDataFlowable
                .observeOn(scheduler)
                .map(modelData -> modelData.getChoices().get(0).getDelta().getContent())
                .map(message -> message.replaceAll("\\s", ""))
                .filter(StrUtil::isNotBlank)
                .flatMap(message -> {
                    List<Character> characterList = new ArrayList<>();
                    for (char c : message.toCharArray()) {
                        characterList.add(c);
                    }
                    return Flowable.fromIterable(characterList);
                })
                .doOnNext(c -> {
                    // 如果是 '{'，计数器 + 1
                    if (c == '{') {
                        counter.addAndGet(1);
                    }
                    if (counter.get() > 0) {
                        stringBuilder.append(c);
                    }
                    if (c == '}') {
                        counter.addAndGet(-1);
                        if (counter.get() == 0) {
                            // 可以拼接题目，并且通过 SSE 返回给前端
                            sseEmitter.send(JSONUtil.toJsonStr(stringBuilder.toString()));
                            // 重置，准备拼接下一道题
                            stringBuilder.setLength(0);
                        }
                    }
                })
                .doOnError((e) -> log.error("sse error", e))
                .doOnComplete(sseEmitter::complete)
                .subscribe();
        return sseEmitter;
    }
}
