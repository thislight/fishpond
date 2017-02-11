package fish.pondof.s;

import panva.Func;
import fish.pondof.s.utils.*;
import fish.pondof.s.StaticValue;
import android.app.*;
import android.os.*;
import android.webkit.*;
import android.view.View.*;
import android.view.*;
import android.content.*;
import android.net.*;
import java.net.*;
import android.util.Log;
import android.widget.*;
import android.graphics.*;
import android.graphics.drawable.*;
import fish.pondof.s.*;

public class MainActivity extends BaseActivity 
{
	
	final MainActivity self = this;
	
	WebView webview;
	ProgressBar pb;
	MenuItem backItem;
	MenuItem reloadItem;
	MenuItem openBroItem;
	
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
		
		webview = (WebView)this.findViewById(R.id.mainWebView);
		this.initWebView(webview);
		
		pb = (ProgressBar)this.findViewById(R.id.mainProgressBar);
		pb.setMax(100);
		
		
		this.goFishMain();
    }

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		backItem = menu.add(R.string.m_back);
		reloadItem = menu.add(R.string.m_reload);
		SubMenu debug = menu.addSubMenu(R.string.m_sub_debug);
		openBroItem = debug.add(R.string.m_debug_open_bro);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		
		new MenuItemMatcher(item)
			.match(reloadItem,new Func(){
				public void call(){
					webview.reload();
				}
			})
			.match(openBroItem,new Func(){
				public void call(){
					askJumpOut(getString(R.string.main_url));
				}
			})
			.match(backItem,new Func(){
				public void call(){
					if(webview.canGoBack()){
						webview.goBack();
					}
				}
			})
			.finish();
		return super.onOptionsItemSelected(item);
	}

	@Override
	public boolean onPrepareOptionsMenu(Menu menu)
	{
		backItem.setEnabled(webview.canGoBack());
		
		return super.onPrepareOptionsMenu(menu);
	}
	
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if ((keyCode == event.KEYCODE_BACK) && webview.canGoBack()) { 
			webview.goBack();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}
	
	private void goFishMain(){
		webview.loadUrl((getString(R.string.main_url)));
	}
	
	final class MainWebViewClient extends WebViewClient
	{
		
		@Override
		public boolean shouldOverrideUrlLoading(WebView view, String url)
		{
			if(!url.startsWith(getString(R.string.main_url))){
				askJumpOut(url);
				Log.i(StaticValue.LOG_TAG,"Jump out ask: "+url);
			}else{
				view.loadUrl(url);
				Log.i(StaticValue.LOG_TAG,"Go "+url);
			}
			
			return false; // Why? Remove webview-self action
		}

		@Override
		public void onReceivedError(WebView view, int errorCode, String description, String failingUrl)
		{
			// TODO: ADD custom error page
			super.onReceivedError(view,errorCode,description,failingUrl);
		}
		
		
	}
	
	private void initWebView(WebView v){
		v.setWebViewClient(new MainWebViewClient());
		v.setNetworkAvailable(true);
		v.setWebChromeClient(new WebChromeClient(){
			@Override
			public void onProgressChanged(WebView view,int p){
				if(p < 100){
					if(pb.getVisibility() == View.GONE){
						pb.setVisibility(View.VISIBLE);
					}
					pb.setProgress(p);
				}else{
					pb.setVisibility(View.GONE);
					pb.setProgress(0);
				}
			}
			
			@Override
			public void onReceivedTitle(WebView view,String title){
				setTitle(title);
			}
			
			@Override
			public void onReceivedIcon(WebView view,Bitmap icon){
				getActionBar().setIcon(new BitmapDrawable(icon));
			}
			
			@Override
			public boolean onConsoleMessage(ConsoleMessage m){
				
				if(m.messageLevel() == ConsoleMessage.MessageLevel.ERROR){
					Log.e(StaticValue.LOG_TAG,"[JavaScript Console] "+m.lineNumber()+" : "+m.message());
				}else{
					Log.d(StaticValue.LOG_TAG,"[JavaScript Console] "+m.lineNumber()+" : "+m.message());
				}
				
				return true;
			}
			
		});
		WebSettings s = v.getSettings();
		s.setJavaScriptEnabled(true);
		s.setCacheMode(WebSettings.LOAD_NORMAL);
		s.setAppCacheEnabled(true);
		s.setEnableSmoothTransition(true);
		s.setDomStorageEnabled(true);
		s.setSupportMultipleWindows(true);
		s.setDatabaseEnabled(true);
		
	}
	
	private void askJumpOut(final String url){
		Log.i(StaticValue.LOG_TAG,"ask jump out: "+url);
		AlertDialog dlg = new AlertDialog.Builder(this)
			.setTitle(R.string.go_to)
			.setCancelable(false)
			.setMessage(format(R.string.jump_out_message,url))
			.setNegativeButton(R.string.b_yes, new android.content.DialogInterface.OnClickListener(){

				@Override
				public void onClick(DialogInterface p1, int p2)
				{
					Intent i = new Intent(Intent.ACTION_VIEW);
					i.setData(Uri.parse(url));
					startActivity(i);
					Log.d(StaticValue.LOG_TAG,"Jump out: "+url);
				}
				
			})
			.setNeutralButton(R.string.b_no, new android.content.DialogInterface.OnClickListener(){

				@Override
				public void onClick(DialogInterface p1, int p2)
				{
					p1.cancel();
					Log.d(StaticValue.LOG_TAG,"Jump out cancel: "+url);
				}

				
			})
			.create();
			
			dlg.show();
	}

	@Override
	protected void onDestroy()
	{
		//webview.destroy();
		super.onDestroy();
	}
	
}
