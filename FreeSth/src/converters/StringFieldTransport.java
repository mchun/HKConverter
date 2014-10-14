package converters;

import android.os.Parcel;

import com.parse.ParseObject;

public class StringFieldTransport 
implements IFieldTransport
{
	ParseObject po;
	Parcel p;
	int d = IFieldTransport.DIRECTION_FORWARD;
	
	public StringFieldTransport(ParseObject inpo, Parcel inp)
	{
		this(inpo,inp,DIRECTION_FORWARD);
	}
	
	public StringFieldTransport(ParseObject inpo, Parcel inp, int direction)
	{
		po = inpo;
		p = inp;
		d = direction;
	}
	@Override
	public void transfer(ValueField f) 
	{
		//1
		if (d == DIRECTION_BACKWARD)
		{
			//parcel to parseobject
			String s = p.readString();
			po.put(f.name, s);
		}
		else
		{
			//forward
			//parseobject to parcel
			String s = po.getString(f.name);
			p.writeString(s);
		}
	}
	
}
