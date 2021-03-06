package fish.pondof.s;

import android.app.*;
import android.content.*;
import android.net.*;
import android.os.*;
import android.support.v4.widget.*;
import android.util.*;
import android.view.*;
import android.webkit.*;
import android.widget.*;
import fish.pondof.s.utils.*;

public class MainActivity extends BaseActivity 
{
	
	WebView webview;
	Toolbar tb;
	SwipeRefreshLayout swipe;
	TextView titleView;
	TextView progressView;
	
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
		
		tb = (Toolbar)this.findViewById(R.id.toolbar);
		setActionBar(tb);
		tb.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener(){

				@Override
				public boolean onMenuItemClick(MenuItem p1){
					switch(p1.getItemId()){
						case R.id.action_back:
							if(webview.canGoBack()){
								webview.goBack();
							}
							break;
						case R.id.action_reload:
							webview.reload();
							break;
						case R.id.action_share:
							shareStory();
							break;
						case R.id.action_exit:
							finishAndRemoveTask();
							break;
						case R.id.action_open:
							askJumpOut(Uri.parse(webview.getUrl()));
					}
					return true;
				}

		});
		
		titleView = (TextView)tb.findViewById(R.id.mainTitle);

		progressView = (TextView)tb.findViewById(R.id.mainProgress);
		
		swipe = (SwipeRefreshLayout)findViewById(R.id.swipe_ly);
		swipe.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener(){

				@Override
				public void onRefresh(){
					webview.reload();
				}

			
		});

		webview = new WebView(this);
        webview.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT));
        swipe.addView(webview);
        this.initWebView(webview);
		
		this.goFishMain();
    }

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		getMenuInflater().inflate(R.menu.main,menu);
		return true;
	}
	

	@Override
	public boolean onPrepareOptionsMenu(Menu menu)
	{
		menu.findItem(R.id.action_back).setEnabled(webview.canGoBack());
		
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
		public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest r){
			Uri uri = r.getUrl();
			Uri homeUri = Uri.parse(getString(R.string.main_url));
			Log.d(StaticValue.LOG_TAG,format("Got uri host: {0}, compare with {1}",uri.getHost(),homeUri.getHost()));
			if(homeUri.getHost().intern() != uri.getHost().intern()){
				askJumpOut(uri);
				return true;
			}
			return false; 
		}


        @Override
        public void onReceivedError(WebView view, WebResourceRequest r, WebResourceError err){
            super.onReceivedError(view,r,err);
        }
		
	}
	
	private void setTextProgress(int p){
		if(progressView.getVisibility() != View.VISIBLE){
			progressView.setVisibility(View.VISIBLE);
		}
		
		progressView.setText(format(R.string.loading_message,p));
	}
	
	private void removeTextProgress(){
		progressView.setVisibility(View.GONE);
	}
	
	private void initWebView(WebView v){
		v.setWebViewClient(new MainWebViewClient());
		v.setNetworkAvailable(true);
		v.setWebChromeClient(new WebChromeClient(){
			@Override
			public void onProgressChanged(WebView view,int p){
				if(p < 100){
					setTextProgress(p);
				}else{
					removeTextProgress();
					if(swipe.isRefreshing()){
						swipe.setRefreshing(false);
					}
				}
			}
			
			@Override
			public void onReceivedTitle(WebView view,String title){
				titleView.setText(title);
			}
			
			@Override
			public boolean onConsoleMessage(ConsoleMessage m){
				final String TEMPLATE = "[JavaScript Console]:{0}:{1}";
				if(m.messageLevel() == ConsoleMessage.MessageLevel.ERROR){
					Log.e(StaticValue.LOG_TAG,format(TEMPLATE,m.lineNumber(),m.message()));
				}else{
					Log.d(StaticValue.LOG_TAG,format(TEMPLATE,m.lineNumber(),m.message()));
				}
				
				return true;
			}
			
		});
		
		WebSettings s = v.getSettings();
		s.setJavaScriptEnabled(true);
		s.setAppCacheEnabled(true);
		s.setDomStorageEnabled(true);
		s.setSupportMultipleWindows(false);
		s.setDatabaseEnabled(true);
		
	}
	
	public void askJumpOut(Uri uri2){
        // value used in local class must be final
		final Uri uri = uri2;
		Log.i(StaticValue.LOG_TAG,"ask jump out: "+uri.toString());
		AlertDialog dlg = new AlertDialog.Builder(this)
			.setTitle(R.string.go_to)
			.setCancelable(false)
			.setMessage(format(R.string.jump_out_message,uri.toString()))
			.setNegativeButton(R.string.b_yes, new android.content.DialogInterface.OnClickListener(){

				@Override
				public void onClick(DialogInterface p1, int p2)
				{
					Intent i = new Intent(Intent.ACTION_VIEW);
					i.setData(uri);
					startActivity(Intent.createChooser(i,getString(R.string.bro_chooser_title)));
					Log.d(StaticValue.LOG_TAG,"Jump out: "+uri.toString());
				}
				
			})
			.setNeutralButton(R.string.b_no, new android.content.DialogInterface.OnClickListener(){

				@Override
				public void onClick(DialogInterface p1, int p2)
				{
					p1.cancel();
					Log.d(StaticValue.LOG_TAG,"Jump out cancel: "+uri.toString());
				}

				
			})
			.create();
			
			dlg.show();
	}
	
	public void shareStory(){
		final AlertDialog dlg = new AlertDialog.Builder(this)
			.setCancelable(true)
			.setView(R.layout.share_dialog)
			.setNegativeButton(R.string.b_yes, new DialogInterface.OnClickListener(){

				@Override
				public void onClick(DialogInterface p1,int p2){
					AlertDialog dlg = (AlertDialog)p1;
					EditText t = (EditText)dlg.findViewById(R.id.shareDialogEditText);
					Intent in = new Intent(Intent.ACTION_SEND);
					in.setType("text/plain");
					in.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					in.putExtra(Intent.EXTRA_SUBJECT,getString(R.string.share_from));
					in.putExtra(Intent.EXTRA_TEXT,format(R.string.share_template,webview.getTitle(),webview.getUrl(),t.getText()));
					startActivity(Intent.createChooser(in,getString(R.string.share_title)));
				}

			})
			.setNeutralButton(R.string.b_no, new DialogInterface.OnClickListener(){

				@Override
				public void onClick(DialogInterface p1,int p2){
					p1.cancel();
				}

			})
			.setTitle(R.string.share_title)
			.create();
			
			dlg.show();
	}

    /**
     * Description: release the memory of web view, otherwise it's resource will not be recycle.
     * Created by Michael Lee on 7/18/16 20:38
     */
    public void clearWebViewResource() {
        if (webview!= null) {
            Log.d(StaticValue.LOG_TAG,"Clear webview's resources");
            webview.removeAllViews();
            // in android 5.1(sdk:21) we should invoke this to avoid memory leak
            // see (https://coolpers.github.io/webview/memory/leak/2015/07/16/
            // android-5.1-webview-memory-leak.html)
            ((ViewGroup) webview.getParent()).removeView(webview);
            webview.setTag(null);
            webview.clearHistory();
            webview.destroy();
            webview = null;
        }
    }

	@Override
	protected void onDestroy()
	{
		clearWebViewResource();
		super.onDestroy();
	}
	
	
}
