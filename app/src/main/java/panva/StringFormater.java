package panva;
import java.text.MessageFormat;
import android.content.*;

public class StringFormater
{
	
	public static String format(String p,Object... args){
		return MessageFormat.format(p,args);
	}
	
	public static String format(Context x,int id,Object... args){
		return format(x.getString(id),args);
	}
}
