package main;

import io.reactivex.Observable;
import io.reactivex.subjects.PublishSubject;
import jlibudev.UdevDevice;
import jlibudev.monitor.DeviceListener;

public class McuDeviceListener implements DeviceListener {
  public static final String SUBSYSTEM_NAME = "tty";
  public static final String PORT_PREFIX = "ttyACM";

  private final PublishSubject<UdevDevice> subject = PublishSubject.create();

  @Override
  public void addDevice(UdevDevice dev) {
    if (isMcuDevice(dev)) {
      subject.onNext(dev);
    }
  }

  @Override
  public void enumerateDevice(UdevDevice dev) {
    //Ignore
  }

  @Override
  public void removeDevice(UdevDevice dev) {
    if (isMcuDevice(dev)) {
      subject.onNext(dev);
    }
  }

  /**
   * Returns the observable part of the subject
   *
   * NOTE: Observable is hot because DeviceListener is hot.
   *
   * @return observable
   */
  public Observable<UdevDevice> getObservable() {
    return subject;
  }

  //************************************************************************************************
  //                                      Helper methods
  //************************************************************************************************

  private static boolean isMcuDevice(UdevDevice dev) {
    return isMcuSubsystem(dev) && isMcuPort(dev);
  }

  private static boolean isMcuSubsystem(UdevDevice dev) {
    return dev.getSubsystem()
              .equalsIgnoreCase(SUBSYSTEM_NAME);
  }

  private static boolean isMcuPort(UdevDevice dev) {
    return dev.getSysName()
              .contains(PORT_PREFIX);
  }
}
