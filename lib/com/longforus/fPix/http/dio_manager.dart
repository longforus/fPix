import 'dart:convert';
import 'dart:io';
import 'package:dio/dio.dart';
import 'package:fPix/com/longforus/fPix/utils/fpix_toast.dart';
import 'package:fPix/com/longforus/fPix/view/LoadingDialog.dart';
import 'package:flutter/foundation.dart';
import 'package:flutter/material.dart';
import 'package:flutter/widgets.dart';
import 'my_http_log.dart';

typedef SuccessCallback<T> = dynamic Function(T t);
typedef ErrorCallback = dynamic Function(int code, String msg);

/*
 * 网络请求管理类
 */
class DioManager {
  //写一个单例
  //在 Dart 里，带下划线开头的变量是私有变量
  static DioManager _instance;
  static String baseUrl ;
  static Map<String, String> reqCons={};

  static DioManager getInstance() {
    if (_instance == null) {
      _instance = DioManager();
    }
    return _instance;
  }

  Dio _dio = new Dio();

  static get currentTime => DateTime.now().millisecondsSinceEpoch ?? '';

  DioManager() {
    // Set default configs
    Map<String, String> headers = {"content-type": "application/json"};
    _dio.options.headers = headers;
    _dio.options.connectTimeout = 40000;
    _dio.options.receiveTimeout = 40000;
    _dio.options.responseType = ResponseType.json;
    _dio.options.contentType = ContentType.parse('application/x-www-form-urlencoded').toString();
    //是否开启请求日志
    if (!kReleaseMode) {
      _dio.interceptors.add(MyLogInterceptor(
          responseBody: true, request: false, requestHeader: false, responseHeader: false, requestBody: true));
    }
    // dio.interceptors.add(CookieManager(CookieJar())); //缓存相关类，具体设置见https://github.com/flutterchina/cookie_jar
  }

  //get请求
  Future<T> get<T>(String url, Map<String, dynamic> params,
      {BuildContext showContext,SuccessCallback<T> onSuccess,  loadingStr = "请求", ErrorCallback onError}) async {
    return _requestHttp<T>(showContext,baseUrl + url, onSuccess, loadingStr, onError, 'get', params);
  }

  //post请求
  Future<T> post<T>(String url, Map<String, dynamic> params,
      {BuildContext showContext,SuccessCallback<T> onSuccess, loadingStr = "请求", ErrorCallback onError}) async {
    return _requestHttp<T>(showContext,baseUrl + url, onSuccess, loadingStr, onError, "post", params);
  }

  Future<T> _requestHttp<T>(BuildContext _buildContext,String url, SuccessCallback<T> onSuccess, String loadingStr,
      [ErrorCallback onError, String method, Map<String, dynamic> params]) async {
    if (params == null) {
      params = {};
    }
    params.addAll(reqCons);
    params["timeStamp"] = DateTime.now().millisecondsSinceEpoch;
    if (_buildContext != null) {
      showSimpleLoadingDialog(context: _buildContext, msg: "$loadingStr中...");
    }
    Response response;
    try {
      if (method == 'get') {
        //get请求 拼接参数放queryParameters里面
        if (params != null && params.isNotEmpty) {
          response = await _dio.get(url, queryParameters: params);
        } else {
          response = await _dio.get(url);
        }
      } else if (method == 'post') {
        //post请求 对象放data里面
        if (params != null && params.isNotEmpty) {
          response = await _dio.post(url, data: params);
        } else {
          response = await _dio.post(url);
        }
      }
    } on DioError catch (error) {
      if (_buildContext != null ) {
        dismissDialog(_buildContext);
      }
      // 请求错误处理
      Response errorResponse;
      if (error.response != null) {
        errorResponse = error.response;
      } else {
        errorResponse = new Response(statusCode: -1, statusMessage: "未知错误");
      }
      // 请求超时
      if (error.type == DioErrorType.CONNECT_TIMEOUT || error.type == DioErrorType.RESPONSE) {
        errorResponse.statusCode = ResultCode.CONNECT_TIMEOUT;
        errorResponse.statusMessage = "请求超时";
        shortToast("请求已超时,稍后重试");
      } else if (error.type == DioErrorType.RECEIVE_TIMEOUT) {
        // 一般服务器错误
        errorResponse.statusCode = ResultCode.RECEIVE_TIMEOUT;
        errorResponse.statusMessage = "接收超时";
      } else {
        errorResponse = new Response(statusCode: -1, statusMessage: "未知错误");
      }
      if (onError != null) {
        onError(errorResponse.statusCode, errorResponse.statusMessage);
      }
      return null;
    }
    if(onSuccess!=null) {
      onSuccess(response.data);
    }
    return response.data;
  }


  void showSimpleLoadingDialog({BuildContext context, String msg}) {
    showDialog(
        context: context,
        barrierDismissible: false,
        routeSettings: RouteSettings(name: "loadingDialog"),
        builder: (BuildContext context) {
          return new LoadingDialog(text: msg);
        });
  }

  void dismissDialog(BuildContext context) {
    Navigator.of(context).pop();
  }
}

class JsonUtil {
  //带有首行缩进的Json格式
  static JsonEncoder encoder = JsonEncoder.withIndent('  ');

  /// 单纯的Json格式输出打印
  static void printJson(Object object) {
    try {
      var encoderString = encoder.convert(object);
      // debugPrint(encoderString);
      // 不使用print()方法是因为这是单条输出，如果过长无法显示全
      // 所以使用debugPrint()
      debugPrint(encoderString.toString());
      // 下面这语句的效果与debugPrint 相同
      //prettyString.split('\n').forEach((element) => debugPrint(element));
    } catch (e) {
      debugPrint(e.toString());
    }
  }

  /// 接收Dio请求库返回的Response对象
  static void printRespond(Response response) {
    Map httpLogMap = Map();
    httpLogMap.putIfAbsent("requestUrl", () => "${response.request.uri}");
//    httpLogMap.putIfAbsent("requestHeaders", () => response.request.headers);
    httpLogMap.putIfAbsent("requestQueryParameters", () => response.request.queryParameters);
    httpLogMap.putIfAbsent("requestPostdata", () => response.request.data);
    printJson(httpLogMap);
    debugPrint("*** Response ***");
    debugPrint(response.data.toString());
  }
}

/*
 * dio网络请求失败的回调错误码
 */
class ResultCode {
  //正常返回是1
  static const SUCCESS = 0;

  //异常返回是0
  static const ERROR = 1;

  /// When opening  url timeout, it occurs.
  static const CONNECT_TIMEOUT = 401;

  ///It occurs when receiving timeout.
  static const RECEIVE_TIMEOUT = -2;

  /// When the server response, but with a incorrect status, such as 404, 503...
  static const RESPONSE = -3;

  /// When the request is cancelled, dio will throw a error with this type.
  static const CANCEL = -4;

  /// read the DioError.error if it is not null.
  static const DEFAULT = -5;
}
