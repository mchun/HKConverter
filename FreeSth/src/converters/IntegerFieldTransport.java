package converters;

import android.os.Parcel;

import com.parse.ParseObject;

public class IntegerFieldTransport 
implements IFieldTransport
{
	ParseObject po;
	Parcel p;
	int d = IFieldTransport.DIRECTION_FORWARD;
	
	public IntegerFieldTransport(ParseObject inpo, Parcel inp)
	{
		this(inpo,inp,DIRECTION_FORWARD);
	}
	
	public IntegerFieldTransport(ParseObject inpo, Parcel inp, int direction)
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
			int i = p.readInt();
			po.put(f.name, i);
		}
		else
		{
			//forward
			//parseobject to parcel
			int i = po.getInt(f.name);
			p.writeInt(i);
		}
	}
	
}
