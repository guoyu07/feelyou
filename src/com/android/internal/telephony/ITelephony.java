package com.android.internal.telephony;

import android.os.Bundle;
import android.telephony.NeighboringCellInfo;
import java.util.List;

public interface ITelephony
{
  public  void answerRingingCall();

  public  void call(String paramString);

  public  void cancelMissedCallsNotification();

  public  void dial(String paramString);

  public  int disableApnType(String paramString);

  public  boolean disableDataConnectivity();

  public  void disableLocationUpdates();

  public  int enableApnType(String paramString);

  public  boolean enableDataConnectivity();

  public  void enableLocationUpdates();

  public  boolean endCall();

  public  int getCallState();

  public  Bundle getCellLocation();

  public  int getDataActivity();

  public  int getDataState();

  public  List<NeighboringCellInfo> getNeighboringCellInfo();

  public  boolean handlePinMmi(String paramString);

  public  boolean isDataConnectivityPossible();

  public  boolean isIdle();

  public  boolean isOffhook();

  public  boolean isRadioOn();

  public  boolean isRinging();

  public  boolean isSimPinEnabled();

  public  boolean setRadio(boolean paramBoolean);

  public  boolean showCallScreen();

  public  boolean showCallScreenWithDialpad(boolean paramBoolean);

  public  void silenceRinger();

  public  boolean supplyPin(String paramString);

  public  void toggleRadioOnOff();

  public  void updateServiceLocation();
}