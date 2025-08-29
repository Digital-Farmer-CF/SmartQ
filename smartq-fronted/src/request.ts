import axios from "axios";
const DEV_BASE_URL = "http://localhost:8101";
const PROD_BASE_URL = "http://193.112.187.169";
const myAxios = axios.create({
  baseURL: PROD_BASE_URL,
  timeout: 60000,
  withCredentials: true,
});

// Add dev override without removing existing config
try {
  // In dev, call local backend to keep session cookies on same origin
  const isDevEnv =
    process.env.NODE_ENV === "development" ||
    (typeof window !== "undefined" &&
      (window.location.hostname === "localhost" ||
        window.location.hostname === "127.0.0.1"));
  if (isDevEnv) {
    // prettier-ignore
    myAxios.defaults.baseURL = DEV_BASE_URL;
  }
} catch (e) {
  // noop
}

// Add a request interceptor
myAxios.interceptors.request.use(
  function (config) {
    // Do something before request is sent
    return config;
  },
  function (error) {
    // Do something with request error
    return Promise.reject(error);
  }
);

// Add a response interceptor
myAxios.interceptors.response.use(
  function (response) {
    // Any status code that lie within the range of 2xx cause this function to trigger
    // Do something with response data
    console.log(response);

    const { data } = response;
    // 未登录
    if (data.code === 40100) {
      // 不是获取用户信息接口，或者不是登录页面，则跳转到登录页面
      if (
        !response.request.responseURL.includes("user/get/login") &&
        !window.location.pathname.includes("/user/login")
      ) {
        window.location.href = `/user/login?redirect=${window.location.href}`;
      }
    }
    return response;
  },
  function (error) {
    // Any status codes that falls outside the range of 2xx cause this function to trigger
    // Do something with response error
    return Promise.reject(error);
  }
);

export default myAxios;
