package wjj.js;

/**
 * 功能：该activity通过webview加载的html与java的交互来设置日期
 * 
 * 描述：webview加载的html通过js实现与java接口的交互，这个测试调试成功的模拟器采用的是android4.2(API-17)，
 *     而在mainfest文件中的android:targetSdkVersion可以设置小于17的版本号
 */

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.JavascriptInterface;
import android.webkit.URLUtil;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;

@SuppressLint({ "JavascriptInterface", "SetJavaScriptEnabled" })

public class MainActivity extends Activity {
	WebView wv;
	private Button mButton;
	private EditText mEditText;
	private static final String TAG = "MainActivity";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		mButton = (Button) findViewById(R.id.button01);
		mEditText = (EditText) findViewById(R.id.edittext01);
		wv = ((WebView) findViewById(R.id.webView1));
		WebSettings ws = wv.getSettings();
		ws.setSupportZoom(true);
		ws.setBuiltInZoomControls(true);
		// ws.setLoadWithOverviewMode(true);
		// ws.setUseWideViewPort(true);
		ws.setDefaultTextEncodingName("utf-8");
		ws.setJavaScriptEnabled(true);
		// 自定义的客户端js处理对象"myjs"
		wv.addJavascriptInterface(new MyJavaScriptInterface(), "myjs");
		// ws.setJavaScriptCanOpenWindowsAutomatically(true);// 允许弹出窗口
		// 映射到javascript的window.open
		wv.setWebViewClient(new WebViewClient() {
			@Override
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				view.loadUrl(url);
				return true;
			}

			@Override
			public void onPageFinished(WebView view, String url) {
				super.onPageFinished(view, url);
			}

			@Override
			public void onPageStarted(WebView view, String url, Bitmap favicon) {
				super.onPageStarted(view, url, favicon);
			}
		});

		wv.loadUrl("file:///android_asset/showtime.html");// 测试页1
	
		//wv.loadUrl("file:///android_asset/dialog.html");// 测试页2
		//设置js处理的客户端，让客户端实现对应的js功能
		wv.setWebChromeClient(new MyWebChromeClient(MainActivity.this));
		
		mButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				try {
					// 取得编辑框中输入的内容
					String url = mEditText.getText().toString();
					// 判断输入的内容是不是网址
					if (URLUtil.isNetworkUrl(url)) {
						// 装载网址
						wv.loadUrl(url);
					} else {
						mEditText.setText("输入网址错误");
					}
				} catch (Exception e) {
					Log.e(TAG, e.toString());
				}
			}
		});
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if ((keyCode == KeyEvent.KEYCODE_BACK) && (wv.canGoBack())) {
			// 返回前一个页面
			wv.goBack();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			Bundle bundle = msg.getData();
			//在java中选取好日期后，再通过js中的setDate方法实现对html的重新加载
			wv.loadUrl("javascript:setDate('" + bundle.getString("dateString")
					+ "','" + bundle.getString("eleId") + "');");
		};

	};


	/**
	 * 在java中通过datePickerDialog来设置新的日期，再通过handler的sendMessage方法去调用js中的方法
	 * @author huanggui
	 *
	 */
	class MyJavaScriptInterface {
		@JavascriptInterface
		public void chooseDate(String date, final String eleId) {
			String[] dateParts = date.split("-");
			new DatePickerDialog(
					MainActivity.this,
					new OnDateSetListener() {
						@Override
						public void onDateSet(DatePicker view, int year,
								int monthOfYear, int dayOfMonth) {
							String dateString = year
									+ "-"
									+ ((monthOfYear + 1) < 10 ? "0"
											+ (monthOfYear + 1)
											: (monthOfYear + 1))
									+ "-"
									+ (dayOfMonth < 10 ? "0" + dayOfMonth
											: dayOfMonth);
							Message msg = new Message();

							Bundle bundle = new Bundle();
							bundle.putString("dateString", dateString);
							bundle.putString("eleId", eleId);

							msg.setData(bundle);
							handler.sendMessage(msg);

						}
					}, Integer.parseInt(dateParts[0]),
					Integer.parseInt(dateParts[1]) - 1, Integer
							.parseInt(dateParts[2])).show();

		}
	}
}
