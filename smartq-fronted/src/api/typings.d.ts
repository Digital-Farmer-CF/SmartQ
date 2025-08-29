declare namespace API {
  type AiGenerateQuestionRequest = {
    appId?: number;
    optionNumber?: number;
    questionNumber?: number;
  };

  type aiGenerateQuestionSSEUsingGETParams = {
    appId?: number;
    optionNumber?: number;
    questionNumber?: number;
  };

  type App = {
    appDesc?: string;
    appIcon?: string;
    appName?: string;
    appType?: number;
    createTime?: string;
    id?: number;
    isDelete?: number;
    reviewMessage?: string;
    reviewStatus?: number;
    reviewTime?: string;
    reviewerId?: number;
    scoringStrategy?: number;
    updateTime?: string;
    userId?: number;
  };

  type AppAddRequest = {
    appDesc?: string;
    appIcon?: string;
    appName?: string;
    appType?: number;
    createTime?: string;
    id?: number;
    scoringStrategy?: number;
    updateTime?: string;
    userId?: number;
  };

  type AppAnswerCountDTO = {
    answerCount?: number;
    appId?: number;
  };

  type AppEditRequest = {
    appDesc?: string;
    appIcon?: string;
    appId?: string;
    appName?: string;
    appType?: number;
    id?: number;
    scoringStrategy?: number;
    updateTime?: string;
  };

  type AppQueryRequest = {
    appDesc?: string;
    appIcon?: string;
    appName?: string;
    appType?: number;
    createTime?: string;
    current?: number;
    id?: number;
    isDelete?: number;
    pageSize?: number;
    reviewMessage?: string;
    reviewStatus?: number;
    reviewTime?: string;
    reviewerId?: number;
    scoringStrategy?: number;
    searchText?: string;
    sortField?: string;
    sortOrder?: string;
    updateTime?: string;
    userId?: number;
  };

  type AppUpdateRequest = {
    appDesc?: string;
    appIcon?: string;
    appName?: string;
    appType?: number;
    createTime?: string;
    id?: number;
    isDelete?: number;
    reviewMessage?: string;
    reviewStatus?: number;
    reviewTime?: string;
    reviewerId?: number;
    scoringStrategy?: number;
    updateTime?: string;
    userId?: number;
  };

  type AppVO = {
    appDesc?: string;
    appIcon?: string;
    appName?: string;
    appType?: number;
    createTime?: string;
    id?: number;
    reviewStatus?: number;
    scoringStrategy?: number;
    tagList?: string[];
    updateTime?: string;
    user?: UserVO;
    userId?: number;
  };

  type BaseResponseAppVO_ = {
    code?: number;
    data?: AppVO;
    message?: string;
  };

  type BaseResponseBoolean_ = {
    code?: number;
    data?: boolean;
    message?: string;
  };

  type BaseResponseListAppAnswerCountDTO_ = {
    code?: number;
    data?: AppAnswerCountDTO[];
    message?: string;
  };

  type BaseResponseListQuestionContent_ = {
    code?: number;
    data?: QuestionContent[];
    message?: string;
  };

  type BaseResponseListUserResultCountDTO_ = {
    code?: number;
    data?: UserResultCountDTO[];
    message?: string;
  };

  type BaseResponseLoginUserVO_ = {
    code?: number;
    data?: LoginUserVO;
    message?: string;
  };

  type BaseResponseLong_ = {
    code?: number;
    data?: number;
    message?: string;
  };

  type BaseResponsePageApp_ = {
    code?: number;
    data?: PageApp_;
    message?: string;
  };

  type BaseResponsePageAppVO_ = {
    code?: number;
    data?: PageAppVO_;
    message?: string;
  };

  type BaseResponsePageQuestion_ = {
    code?: number;
    data?: PageQuestion_;
    message?: string;
  };

  type BaseResponsePageQuestionVO_ = {
    code?: number;
    data?: PageQuestionVO_;
    message?: string;
  };

  type BaseResponsePageScoringResult_ = {
    code?: number;
    data?: PageScoringResult_;
    message?: string;
  };

  type BaseResponsePageScoringResultVO_ = {
    code?: number;
    data?: PageScoringResultVO_;
    message?: string;
  };

  type BaseResponsePageUser_ = {
    code?: number;
    data?: PageUser_;
    message?: string;
  };

  type BaseResponsePageUserAnswer_ = {
    code?: number;
    data?: PageUserAnswer_;
    message?: string;
  };

  type BaseResponsePageUserAnswerVO_ = {
    code?: number;
    data?: PageUserAnswerVO_;
    message?: string;
  };

  type BaseResponsePageUserVO_ = {
    code?: number;
    data?: PageUserVO_;
    message?: string;
  };

  type BaseResponseQuestionVO_ = {
    code?: number;
    data?: QuestionVO;
    message?: string;
  };

  type BaseResponseScoringResultVO_ = {
    code?: number;
    data?: ScoringResultVO;
    message?: string;
  };

  type BaseResponseString_ = {
    code?: number;
    data?: string;
    message?: string;
  };

  type BaseResponseUser_ = {
    code?: number;
    data?: User;
    message?: string;
  };

  type BaseResponseUserAnswerVO_ = {
    code?: number;
    data?: UserAnswerVO;
    message?: string;
  };

  type BaseResponseUserVO_ = {
    code?: number;
    data?: UserVO;
    message?: string;
  };

  type DeleteRequest = {
    id?: number;
  };

  type getAppAnswerResultCountUsingGETParams = {
    /** appId */
    appId?: number;
  };

  type getAppVOByIdUsingGETParams = {
    /** id */
    id?: number;
  };

  type getQuestionVOByIdUsingGETParams = {
    /** id */
    id?: number;
  };

  type getScoringResultVOByIdUsingGETParams = {
    /** id */
    id?: number;
  };

  type getUserAnswerVOByIdUsingGETParams = {
    /** id */
    id?: number;
  };

  type getUserByIdUsingGETParams = {
    /** id */
    id?: number;
  };

  type getUserVOByIdUsingGETParams = {
    /** id */
    id?: number;
  };

  type LoginUserVO = {
    createTime?: string;
    id?: number;
    updateTime?: string;
    userAvatar?: string;
    userName?: string;
    userProfile?: string;
    userRole?: string;
  };

  type Option = {
    key?: string;
    result?: string;
    score?: number;
    value?: string;
  };

  type OrderItem = {
    asc?: boolean;
    column?: string;
  };

  type PageApp_ = {
    countId?: string;
    current?: number;
    maxLimit?: number;
    optimizeCountSql?: boolean;
    orders?: OrderItem[];
    pages?: number;
    records?: App[];
    searchCount?: boolean;
    size?: number;
    total?: number;
  };

  type PageAppVO_ = {
    countId?: string;
    current?: number;
    maxLimit?: number;
    optimizeCountSql?: boolean;
    orders?: OrderItem[];
    pages?: number;
    records?: AppVO[];
    searchCount?: boolean;
    size?: number;
    total?: number;
  };

  type PageQuestion_ = {
    countId?: string;
    current?: number;
    maxLimit?: number;
    optimizeCountSql?: boolean;
    orders?: OrderItem[];
    pages?: number;
    records?: Question[];
    searchCount?: boolean;
    size?: number;
    total?: number;
  };

  type PageQuestionVO_ = {
    countId?: string;
    current?: number;
    maxLimit?: number;
    optimizeCountSql?: boolean;
    orders?: OrderItem[];
    pages?: number;
    records?: QuestionVO[];
    searchCount?: boolean;
    size?: number;
    total?: number;
  };

  type PageScoringResult_ = {
    countId?: string;
    current?: number;
    maxLimit?: number;
    optimizeCountSql?: boolean;
    orders?: OrderItem[];
    pages?: number;
    records?: ScoringResult[];
    searchCount?: boolean;
    size?: number;
    total?: number;
  };

  type PageScoringResultVO_ = {
    countId?: string;
    current?: number;
    maxLimit?: number;
    optimizeCountSql?: boolean;
    orders?: OrderItem[];
    pages?: number;
    records?: ScoringResultVO[];
    searchCount?: boolean;
    size?: number;
    total?: number;
  };

  type PageUser_ = {
    countId?: string;
    current?: number;
    maxLimit?: number;
    optimizeCountSql?: boolean;
    orders?: OrderItem[];
    pages?: number;
    records?: User[];
    searchCount?: boolean;
    size?: number;
    total?: number;
  };

  type PageUserAnswer_ = {
    countId?: string;
    current?: number;
    maxLimit?: number;
    optimizeCountSql?: boolean;
    orders?: OrderItem[];
    pages?: number;
    records?: UserAnswer[];
    searchCount?: boolean;
    size?: number;
    total?: number;
  };

  type PageUserAnswerVO_ = {
    countId?: string;
    current?: number;
    maxLimit?: number;
    optimizeCountSql?: boolean;
    orders?: OrderItem[];
    pages?: number;
    records?: UserAnswerVO[];
    searchCount?: boolean;
    size?: number;
    total?: number;
  };

  type PageUserVO_ = {
    countId?: string;
    current?: number;
    maxLimit?: number;
    optimizeCountSql?: boolean;
    orders?: OrderItem[];
    pages?: number;
    records?: UserVO[];
    searchCount?: boolean;
    size?: number;
    total?: number;
  };

  type Question = {
    appId?: number;
    createTime?: string;
    id?: number;
    isDelete?: number;
    questionContent?: string;
    updateTime?: string;
    userId?: number;
  };

  type QuestionAddRequest = {
    appId?: number;
    createTime?: string;
    id?: number;
    questionContent?: QuestionContent[];
    updateTime?: string;
    userId?: number;
  };

  type QuestionContent = {
    options?: Option[];
    title?: string;
  };

  type QuestionEditRequest = {
    appId?: number;
    id?: number;
    questionContent?: QuestionContent[];
    updateTime?: string;
  };

  type QuestionQueryRequest = {
    appId?: number;
    createTime?: string;
    current?: number;
    id?: number;
    isDelete?: number;
    pageSize?: number;
    questionContent?: QuestionContent[];
    sortField?: string;
    sortOrder?: string;
    updateTime?: string;
    userId?: number;
  };

  type QuestionUpdateRequest = {
    appId?: number;
    id?: number;
    isDelete?: number;
    questionContent?: QuestionContent[];
    updateTime?: string;
    userId?: number;
  };

  type QuestionVO = {
    appId?: number;
    appName?: string;
    createTime?: string;
    id?: number;
    questionContent?: QuestionContent[];
    updateTime?: string;
    userId?: number;
    userVO?: UserVO;
  };

  type ReviewRequest = {
    id?: number;
    reviewMessage?: string;
    reviewStatus?: number;
  };

  type ScoringResult = {
    appId?: number;
    createTime?: string;
    id?: number;
    isDelete?: number;
    resultDesc?: string;
    resultName?: string;
    resultPicture?: string;
    resultProp?: string;
    resultScoreRange?: number;
    updateTime?: string;
    userId?: number;
  };

  type ScoringResultAddRequest = {
    appId?: number;
    createTime?: string;
    id?: number;
    isDelete?: number;
    resultDesc?: string;
    resultName?: string;
    resultPicture?: string;
    resultProp?: string[];
    resultScoreRange?: number;
    updateTime?: string;
    userId?: number;
  };

  type ScoringResultEditRequest = {
    appId?: number;
    createTime?: string;
    id?: number;
    isDelete?: number;
    resultDesc?: string;
    resultName?: string;
    resultPicture?: string;
    resultProp?: string[];
    resultScoreRange?: number;
    updateTime?: string;
    userId?: number;
  };

  type ScoringResultQueryRequest = {
    appId?: number;
    current?: number;
    id?: number;
    pageSize?: number;
    resultDesc?: string;
    resultName?: string;
    resultScoreRange?: number;
    searchText?: string;
    sortField?: string;
    sortOrder?: string;
    userId?: number;
  };

  type ScoringResultUpdateRequest = {
    appId?: number;
    id?: number;
    isDelete?: number;
    resultDesc?: string;
    resultName?: string;
    resultPicture?: string;
    resultProp?: string[];
    resultScoreRange?: number;
  };

  type ScoringResultVO = {
    appId?: number;
    createTime?: string;
    id?: number;
    resultDesc?: string;
    resultName?: string;
    resultPicture?: string;
    resultProp?: string[];
    resultScoreRange?: number;
    updateTime?: string;
    userId?: number;
    userVO?: UserVO;
  };

  type SseEmitter = {
    timeout?: number;
  };

  type uploadFileUsingPOSTParams = {
    biz?: string;
  };

  type User = {
    createTime?: string;
    id?: number;
    isDelete?: number;
    mpOpenId?: string;
    unionId?: string;
    updateTime?: string;
    userAccount?: string;
    userAvatar?: string;
    userName?: string;
    userPassword?: string;
    userProfile?: string;
    userRole?: string;
  };

  type UserAddRequest = {
    createTime?: string;
    id?: number;
    isDelete?: number;
    mpOpenId?: string;
    unionId?: string;
    userAccount?: string;
    userAvatar?: string;
    userName?: string;
    userPassword?: string;
    userProfile?: string;
    userRole?: string;
  };

  type UserAnswer = {
    appId?: number;
    appType?: number;
    choices?: string;
    createTime?: string;
    id?: number;
    isDelete?: number;
    resultDesc?: string;
    resultId?: number;
    resultName?: string;
    resultPicture?: string;
    resultScore?: number;
    scoringStrategy?: number;
    updateTime?: string;
    userId?: number;
  };

  type UserAnswerAddRequest = {
    appId?: number;
    appType?: number;
    choices?: string[];
    createTime?: string;
    id?: number;
    isDelete?: number;
    resultDesc?: string;
    resultId?: number;
    resultName?: string;
    resultPicture?: string;
    resultScore?: number;
    scoringStrategy?: number;
    updateTime?: string;
    userId?: number;
  };

  type UserAnswerEditRequest = {
    appId?: number;
    appType?: number;
    choices?: string[];
    id?: number;
    isDelete?: number;
    resultDesc?: string;
    resultId?: number;
    resultName?: string;
    resultScore?: number;
    scoringStrategy?: number;
    updateTime?: string;
    userId?: number;
  };

  type UserAnswerQueryRequest = {
    appId?: number;
    appType?: number;
    choices?: string[];
    createTime?: string;
    current?: number;
    id?: number;
    isDelete?: number;
    pageSize?: number;
    resultDesc?: string;
    resultId?: number;
    resultName?: string;
    resultPicture?: string;
    resultScore?: number;
    scoringStrategy?: number;
    searchText?: string;
    sortField?: string;
    sortOrder?: string;
    updateTime?: string;
    userId?: number;
  };

  type UserAnswerUpdateRequest = {
    choices?: string[];
    id?: number;
    isDelete?: number;
    updateTime?: string;
  };

  type UserAnswerVO = {
    appId?: number;
    appType?: number;
    choices?: string[];
    createTime?: string;
    id?: number;
    resultDesc?: string;
    resultId?: number;
    resultName?: string;
    resultPicture?: string;
    resultScore?: number;
    scoringStrategy?: number;
    userId?: number;
    userVO?: UserVO;
  };

  type userLoginByWxOpenUsingGETParams = {
    /** code */
    code: string;
  };

  type UserLoginRequest = {
    mpOpenId?: string;
    unionId?: string;
    userAccount?: string;
    userPassword?: string;
    userRole?: string;
  };

  type UserQueryRequest = {
    createTime?: string;
    current?: number;
    id?: number;
    isDelete?: number;
    pageSize?: number;
    sortField?: string;
    sortOrder?: string;
    unionId?: string;
    updateTime?: string;
    userAccount?: string;
    userName?: string;
  };

  type UserRegisterRequest = {
    checkPassword?: string;
    createTime?: string;
    id?: number;
    isDelete?: number;
    unionId?: string;
    userAccount?: string;
    userAvatar?: string;
    userName?: string;
    userPassword?: string;
    userProfile?: string;
    userRole?: string;
  };

  type UserResultCountDTO = true;

  type UserUpdateMyRequest = {
    id?: number;
    isDelete?: number;
    mpOpenId?: string;
    unionId?: string;
    updateTime?: string;
    userAvatar?: string;
    userName?: string;
    userPassword?: string;
    userProfile?: string;
    userRole?: string;
  };

  type UserUpdateRequest = {
    id?: number;
    isDelete?: number;
    mpOpenId?: string;
    unionId?: string;
    updateTime?: string;
    userAvatar?: string;
    userName?: string;
    userPassword?: string;
    userProfile?: string;
    userRole?: string;
  };

  type UserVO = {
    createTime?: string;
    id?: number;
    updateTime?: string;
    userAvatar?: string;
    userName?: string;
    userProfile?: string;
    userRole?: string;
  };
}
