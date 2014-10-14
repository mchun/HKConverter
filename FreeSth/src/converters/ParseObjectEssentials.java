package converters;

import java.util.Date;

import android.os.Parcel;
import android.os.Parcelable;

public class ParseObjectEssentials
implements Parcelable
{
	//Add more field types later
	public Date createdAt;
	public User createdBy;
	public Date lastUpdatedAt;
	public User lastUpdatedBy;
	
	public ParseObjectEssentials(Date createdAt, User createdBy,
			Date lastUpdatedAt, User lastUpdatedBy) {
		super();
		this.createdAt = createdAt;
		this.createdBy = createdBy;
		this.lastUpdatedAt = lastUpdatedAt;
		this.lastUpdatedBy = lastUpdatedBy;
	}
    public static final Parcelable.Creator<ParseObjectEssentials> CREATOR
	    = new Parcelable.Creator<ParseObjectEssentials>() {
			public ParseObjectEssentials createFromParcel(Parcel in) {
			    return new ParseObjectEssentials(in);
			}
			
			public ParseObjectEssentials[] newArray(int size) {
			    return new ParseObjectEssentials[size];
			}
    }; //end of creator

	//
	@Override
	public int describeContents() {
		return 0;
	}
	
	public ParseObjectEssentials(Parcel in)
	{
		createdAt = new Date(in.readLong());
		createdBy = (User)in.readParcelable(User.class.getClassLoader());
		lastUpdatedAt = new Date(in.readLong());
		lastUpdatedBy = (User)in.readParcelable(User.class.getClassLoader());
	}
	@Override
	public void writeToParcel(Parcel dest, int flags) 
	{
		dest.writeLong(this.createdAt.getTime());
		dest.writeParcelable(createdBy, flags);
		dest.writeLong(lastUpdatedAt.getTime());
		dest.writeParcelable(lastUpdatedBy, flags);
		
	}
	public static ParseObjectEssentials getDefault()
	{
		Date cat = new Date(0);
		User auser = User.getAnnonymousUser();
		Date luat = new Date(0);
		return new ParseObjectEssentials(cat,auser,luat,auser);
	}
}//eof-class
