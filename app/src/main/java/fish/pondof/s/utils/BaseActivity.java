package fish.pondof.s.utils;
import android.app.*;
import panva.StringFormater;

public class BaseActivity extends Activity
{
	public String format(String s,Object... args){
		return StringFormater.format(s,args);
	}
	
	public String format(int id,Object... args){
		return StringFormater.format(this,id,args);
	}
}
