package cn.hhjjj.alipay;

import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CallbackContext;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.alipay.sdk.app.PayTask;
import com.alipay.sdk.app.AuthTask;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.util.Map;

import android.widget.Toast;
import android.text.TextUtils;
import android.annotation.SuppressLint;

import static android.provider.UserDictionary.Words.APP_ID;

/**
 * This class echoes a string called from JavaScript.
 */
public class alipay extends CordovaPlugin {

    private static final int SDK_PAY_FLAG = 1;
    private static final int SDK_AUTH_FLAG = 2;
    @Override
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
      Log.d("123123", "execute: "+action);
        if (action.equals("payment")) {
            String orderInfo = args.getString(0);
            this.payment(orderInfo, callbackContext);
            return true;
        }else if (action.equals("authLogin")) {
            String orderInfo = args.getString(0);
            this.authLogin(orderInfo, callbackContext);
            return true;
        }
        return false;
    }

    private void payment(String orderInfo, final CallbackContext callbackContext) {

        final String payInfo = orderInfo;
//        cordova.getThreadPool().execute(new Runnable() {
//            @Override
//            public void run() {
//                PayTask alipay = new PayTask(cordova.getActivity());
//                Map<String, String> result = alipay.payV2(payInfo, true);
//                Log.i("msp", result.toString());
//
//                Message msg = new Message();
//                msg.what = SDK_PAY_FLAG;
//                msg.obj = result;
//                mHandler.sendMessage(msg);
//
//                PayResult payResult = new PayResult(result);
//                String resultInfo = payResult.getResult();// 同步返回需要验证的信息
//                String resultStatus = payResult.getResultStatus();
//                // 判断resultStatus 为9000则代表支付成功
//                if (TextUtils.equals(resultStatus, "9000")) {
//                    // 该笔订单是否真实支付成功，需要依赖服务端的异步通知。
//                    callbackContext.success(new JSONObject(result));
//                } else {
//                    // 该笔订单真实的支付结果，需要依赖服务端的异步通知。
//                    callbackContext.error(new JSONObject(result));
//                }
//            }
//        });

      Runnable payRunnable = new Runnable() {

        @Override
        public void run() {
          PayTask alipay = new PayTask(cordova.getActivity());
          Map <String,String> result = alipay.payV2(payInfo,true);

          Message msg = new Message();
          msg.what = SDK_PAY_FLAG;
          msg.obj = result;
          mHandler.sendMessage(msg);

          PayResult payResult = new PayResult(result);
          String resultInfo = payResult.getResult();// 同步返回需要验证的信息
          String resultStatus = payResult.getResultStatus();
          // 判断resultStatus 为9000则代表支付成功
          if (TextUtils.equals(resultStatus, "9000")) {
            // 该笔订单是否真实支付成功，需要依赖服务端的异步通知。
            callbackContext.success(new JSONObject(result));
          } else {
            // 该笔订单真实的支付结果，需要依赖服务端的异步通知。
            callbackContext.error(new JSONObject(result));
          }
        }
      };
      // 必须异步调用
      Thread payThread = new Thread(payRunnable);
      payThread.start();
    }

    private void authLogin(String orderInfo, final CallbackContext callbackContext) {
      final String authInfo = orderInfo;

      Runnable authRunnable = new Runnable() {
        @Override
        public void run() {
          AuthTask authTask = new AuthTask(cordova.getActivity());
          // 调用授权接口
          // AuthTask#authV2(String info, boolean isShowLoading)，
          // 获取授权结果
          Map<String, String> result = authTask.authV2(authInfo, true);
          Log.i("msp", result.toString());
          // 将授权结果以 Message 的形式传递给 App 的其它部分处理。
          // 对授权结果的处理逻辑可以参考支付宝 SDK Demo 中的实现。
          Message msg = new Message();
          msg.what = SDK_AUTH_FLAG;
          msg.obj = result;
          mHandler1.sendMessage(msg);

          PayResult loginResult = new PayResult(result);
          String resultStatus = loginResult.getResultStatus();
          if (TextUtils.equals(resultStatus, "9000")) {
            // 该授权是否成功，需要依赖服务端的异步通知。
            callbackContext.success(loginResult.getResult());
          } else {
            // 该授权结果，需要依赖服务端的异步通知。
            callbackContext.error(loginResult.getResult());
          }
        }
      };
      Thread authThread = new Thread(authRunnable);
      authThread.start();
    }

    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {
        @SuppressWarnings("unused")
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case SDK_PAY_FLAG: {
                    @SuppressWarnings("unchecked")
                    PayResult payResult = new PayResult((Map<String, String>) msg.obj);
                    /**
                     对于支付结果，请商户依赖服务端的异步通知结果。同步通知结果，仅作为支付结束的通知。
                     */
                    String resultInfo = payResult.getResult();// 同步返回需要验证的信息
                    String resultStatus = payResult.getResultStatus();
                    // 判断resultStatus 为9000则代表支付成功
                    // 判断resultStatus 为9000则代表支付成功
                    if (TextUtils.equals(resultStatus, "9000")) {
                        // 该笔订单是否真实支付成功，需要依赖服务端的异步通知。
                        Toast.makeText(cordova.getActivity(), "支付成功" + resultStatus, Toast.LENGTH_SHORT);
                    } else {
                        // 该笔订单真实的支付结果，需要依赖服务端的异步通知。
                        Toast.makeText(cordova.getActivity(), "支付失败" + resultStatus, Toast.LENGTH_SHORT);
                    }
                    break;
                }
                default:
                    break;
            }
        }        ;
    };

  @SuppressLint("HandlerLeak")
  private Handler mHandler1 = new Handler() {
    @SuppressWarnings("unused")
    public void handleMessage(Message msg) {
      switch (msg.what) {
        case SDK_PAY_FLAG: {
          @SuppressWarnings("unchecked")
          PayResult payResult = new PayResult((Map<String, String>) msg.obj);
          /**
           对于授权结果，请商户依赖服务端的异步通知结果。同步通知结果，仅作为授权结束的通知。
           */
          String resultInfo = payResult.getResult();// 同步返回需要验证的信息
          String resultStatus = payResult.getResultStatus();
          // 判断resultStatus 为9000则代表授权成功
          // 判断resultStatus 为9000则代表授权成功
          if (TextUtils.equals(resultStatus, "9000")) {
            // 授权成功，需要依赖服务端的异步通知。
            Toast.makeText(cordova.getActivity(), "授权成功" + resultStatus, Toast.LENGTH_SHORT);
          } else {
            // 授权结果，需要依赖服务端的异步通知。
            Toast.makeText(cordova.getActivity(), "授权失败" + resultStatus, Toast.LENGTH_SHORT);
          }
          break;
        }
        default:
          break;
      }
    }        ;
  };

}
