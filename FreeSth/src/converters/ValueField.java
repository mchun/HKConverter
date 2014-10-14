package converters;

import android.os.Parcel;
import android.os.Parcelable;

public class ValueField
implements Parcelable
{
	public static String FT_int = "Integer";
	public static String FT_string = "String";
	public static String FT_Object = "Object";
	public static String FT_unknown = "Unknown";
	
	//Add more field types later
	public String name;
	public String type;
	
	public ValueField(String inName, String inFieldType)
	{
		name = inName;
		type = inFieldType;
	}
    public static final Parcelable.Creator<ValueField> CREATOR
	    = new Parcelable.Creator<ValueField>() {
			public ValueField createFromParcel(Parcel in) {
			    return new ValueField(in);
			}
			
			public ValueField[] newArray(int size) {
			    return new ValueField[size];
			}
    }; //end of creator

	//
	@Override
	public int describeContents() {
		return 0;
	}
	
	public ValueField(Parcel in)
	{
		name = in.readString();
		type = in.readString();
	}
	@Override
	public void writeToParcel(Parcel dest, int flags) 
	{
		dest.writeString(name);
		dest.writeString(type);
	}
	public String toString()
	{
		return name + "/" + type;
	}
	
	public static ValueField getStringField(String fieldName)
	{
		return new ValueField(fieldName, ValueField.FT_string);
	}
}//eof-class
