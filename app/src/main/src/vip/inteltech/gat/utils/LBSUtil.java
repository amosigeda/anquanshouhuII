package vip.inteltech.gat.utils;

import java.util.List;

import android.content.Context;
import android.telephony.NeighboringCellInfo;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.telephony.cdma.CdmaCellLocation;
import android.telephony.gsm.GsmCellLocation;

public class LBSUtil {
	private TelephonyManager manager;
	private List<NeighboringCellInfo> infoLists;
	// mcc 国家号码 mnc 移动网络号码
	private String MCC, MNC;
	private int lac, cellid, sid, nid, bid;
	public LBSUtil(Context context) {
		manager = (TelephonyManager) context
				.getSystemService(Context.TELEPHONY_SERVICE);
		
	}
	public void setSignalStrengthsListener(PhoneStateListener listener){
		manager.listen(listener, PhoneStateListener.LISTEN_SIGNAL_STRENGTHS);
	}
	public void clearSignalStrengthsListener(){
		manager.listen(new PhoneStateListener(), PhoneStateListener.LISTEN_NONE);
	}
	public String getResult(){
		String Result=null;
		try{
			
			String operator = manager.getNetworkOperator();
			/** 通过operator获取 MCC 和MNC */
			MCC = operator.substring(0, 3);
			MNC = operator.substring(3);
			if(MCC.equals("460"))
			{
				if(Integer.valueOf(MNC) != 3){

					GsmCellLocation location = (GsmCellLocation) manager.getCellLocation();
					/** 通过GsmCellLocation获取中国移动和联通 LAC 和cellID */
					lac = location.getLac();
					cellid = location.getCid();
					infoLists = manager.getNeighboringCellInfo();
					Result = MCC + "," + MNC + "," + lac +"," + cellid + ",";
				} else {
					CdmaCellLocation location = (CdmaCellLocation) manager.getCellLocation();
					/*lac = location.getNetworkId();  
					cellid = location.getBaseStationId();*/  
					sid = location.getSystemId(); 
					nid = location.getNetworkId();
					bid = location.getBaseStationId();
					infoLists = manager.getNeighboringCellInfo();
					Result = sid + "," + nid + "," + bid + ",,,";
				}
			}
			}catch(Exception ex)
			{
				ex.printStackTrace();
			}
		return Result;
	}
	public List<NeighboringCellInfo> getLBSList() {
		return infoLists;
	}
}
