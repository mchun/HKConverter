package converters;

import android.os.Parcel;
import android.os.Parcelable;

import com.parse.ParseUser;

public class User
implements Parcelable
{
	//Add more field types later
	public String userid;
	public String username;
	public String userfbid;
	
	public User(String inuserid, String inusername, String infbid)
	{
		userid = inuserid;
		username = inusername;
		userfbid = infbid;
	}
    public static final Parcelable.Creator<User> CREATOR
	    = new Parcelable.Creator<User>() {
			public User createFromParcel(Parcel in) {
			    return new User(in);
			}
			
			public User[] newArray(int size) {
			    return new User[size];
			}
    }; //end of creator

	//
	@Override
	public int describeContents() {
		return 0;
	}
	
	public User(Parcel in)
	{
		userid = in.readString();
		username = in.readString();
		userfbid = in.readString();
	}
	@Override
	public void writeToParcel(Parcel dest, int flags) 
	{
		dest.writeString(userid);
		dest.writeString(username);
		dest.writeString(userfbid);
	}

	public static User getAnnonymousUser()
	{
		return new User("0","Annonynous","0");
	}
	public static User fromParseUser(ParseUser pu)
	{
		if (pu == null) return getAnnonymousUser();
		//pu is available
		String userid = pu.getObjectId();
		String username = pu.getString("name");
		String userfbid = pu.getString("fbid");
		return new User(userid,username,userfbid);
	}
}//eof-class
