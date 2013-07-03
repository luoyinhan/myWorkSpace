package com.aidufei.protocol.core;

import org.json.JSONObject;

import com.aidufei.protocol.common.GSensor;
import com.aidufei.protocol.common.Mouse;


public abstract interface RemoteAction {
	public static final int NONE = 0;
	public static final int ANNOUNCE = 1;
	public static final int PLAY_VOB = 2;
	public static final int PLAY_VOD = 3;
	public static final int PLAY_STATUS_SYNC = 4;
	public static final int GET_VOLUME = 5;
	public static final int SET_VOLUME = 6;
	public static final int PLAY_SYNC = 7;
	public static final int PLAY_CONTROL = 8;
	
//	public abstract void announce(Device remote);
	public abstract void playVOB(Device remote, String user, String name, String resource, String product, long delay);
	public abstract void playVOB(Device remote, String user, String name, String resource, String product, long start, long end,int offset);
	public abstract void playVOD(Device remote, String user, String name, String resource, String product, String asset, String provider,int offset,int duration);
	public abstract void playStatusSync(Device remote);
	public abstract void getVolume(Device remote);
	public abstract void setVolume(Device remote, int volume);
	public abstract void playSync(Device remote);
	public abstract void playControl(Device remote,int control,Object data);
	public abstract void key(Device remote, int key);
	public abstract void mouse(Device remote,Mouse mouse);
	public abstract void sensor(Device remote,GSensor sensor);
	public abstract void text(Device remote, byte[] text);
	public abstract void sendURL(Device remote, String url,int command, JSONObject param);
	public abstract void mirrion(Device remote,boolean start);
}
