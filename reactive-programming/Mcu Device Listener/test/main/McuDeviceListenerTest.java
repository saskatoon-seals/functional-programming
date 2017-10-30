package main;

import static main.McuDeviceListener.PORT_PREFIX;
import static main.McuDeviceListener.SUBSYSTEM_NAME;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import io.reactivex.observers.TestObserver;
import jlibudev.UdevDevice;
import main.McuDeviceListener;

public class McuDeviceListenerTest {
  UdevDevice device;
  TestObserver<UdevDevice> testObserver;
  McuDeviceListener deviceListener;

  @Before
  public void setUp() throws Exception {
    testObserver = new TestObserver<>();
    deviceListener = new McuDeviceListener();

    device = Mockito.mock(UdevDevice.class);
    Mockito.when(device.getSubsystem())
           .thenReturn(SUBSYSTEM_NAME);
    Mockito.when(device.getSysName())
           .thenReturn(PORT_PREFIX + "10");
  }

  @Test
  public void addedDeviceIsPushedToSubscriber() {
    deviceListener.getObservable()
                  .subscribe(testObserver);

    deviceListener.addDevice(device);

    testObserver.assertNoErrors();
    testObserver.assertValues(device);
  }

  @Test
  public void removedDeviceIsPushedToSubscriber() {
    deviceListener.getObservable()
                  .subscribe(testObserver);

    deviceListener.removeDevice(device);

    testObserver.assertNoErrors();
    testObserver.assertValues(device);
  }

  @Test
  public void enumeratedDeviceIsIgnored() {
    deviceListener.getObservable()
                  .subscribe(testObserver);

    deviceListener.enumerateDevice(device);

    testObserver.assertNoErrors();
    testObserver.assertNoValues();
  }
}
