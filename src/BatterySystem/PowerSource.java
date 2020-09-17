package BatterySystem;

import com.sun.jna.NativeLong;

import BatterySystem.PowerProfile.SystemBatteryState;

public class PowerSource {

	private String name;
	private double remainingCapacity;
	private double timeRemaining;

	public PowerSource(String name, double remainingCapacity, double timeRemaining) {
		this.name = name;
		this.remainingCapacity = remainingCapacity;
		this.timeRemaining = timeRemaining;
		
	}

	public String getName() {
		return this.name;
		
	}

	public double getRemainingCapacity() {
		return this.remainingCapacity;
		
	}

	public double getTimeRemaining() {
		return this.timeRemaining;
		
	}

	public static PowerSource[] getPowerSources() {
		String name = "System Battery";
		PowerSource[] psArray = new PowerSource[1];
		// Get structure
		SystemBatteryState batteryState = new SystemBatteryState();
		if (0 != PowerProfile.INSTANCE.CallNtPowerInformation(PowerProfile.SYSTEM_BATTERY_STATE, null,
				new NativeLong(0), batteryState, new NativeLong(batteryState.size()))
				|| batteryState.batteryPresent == 0) {
			psArray[0] = new PowerSource("Unknown", 0d, -1d);
			
		} else {
			int estimatedTime = -2; // -1 = unknown, -2 = unlimited
			if (batteryState.acOnLine == 0 && batteryState.charging == 0 && batteryState.discharging > 0) {
				estimatedTime = batteryState.estimatedTime;
			}
			long maxCapacity = getUnsignedInt(batteryState.maxCapacity);
			long remainingCapacity = getUnsignedInt(batteryState.remainingCapacity);

			psArray[0] = new PowerSource(name, (double) remainingCapacity / maxCapacity, estimatedTime);
		}

		return psArray;
	}

	public static long getUnsignedInt(int x) {
		return x & 0x00000000ffffffffL;
		
	}

}
