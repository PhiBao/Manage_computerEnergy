package application;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;

import java.io.IOException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.ResourceBundle;

import BatterySystem.PowerSource;

public class Controller implements Initializable {

	@FXML
	private Label dateTimeLabel;

	@FXML
	private Label powerLabel;

	@FXML
	private Label batteryLabel;

	@FXML
	private Slider sliderBrightness;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		setDateAndTime();
		setBatterySystem();
		sliderBrightness.setValue(60);
		setBrightness();

	}

	private void setBatterySystem() {
		new Thread(new Runnable() {
			@Override
			public void run() {
				while (true) {
					StringBuilder sb = new StringBuilder("Power: ");

					if (PowerSource.getPowerSources().length == 0) {
						sb.append("Unknown");
					} else {
						double timeRemaining = PowerSource.getPowerSources()[0].getTimeRemaining();

						if (timeRemaining < -1d)
							sb.append("Charging");
						else if (timeRemaining < 0d)
							sb.append("Calculating time remaining");
						else
							sb.append(String.format("%d:%02d' remaining", (int) (timeRemaining / 3600),
									(int) (timeRemaining / 60) % 60));
					}

					for (PowerSource pSource : PowerSource.getPowerSources()) {
						sb.append(String.format("%n %s @ %.1f%%", pSource.getName(),
								pSource.getRemainingCapacity() * 100d));
					}
					final String tmp[] = sb.toString().split("\r");

					Platform.runLater(new Runnable() {
						@Override
						public void run() {
							powerLabel.setText(tmp[0].trim());
							batteryLabel.setText(tmp[1].trim());
						}
					});

					try {
						Thread.sleep(30000); // 30s

					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		}).start();
	}

	private void setDateAndTime() {
		new Thread(new Runnable() {
			@Override
			public void run() {
				SimpleDateFormat simpleDateFormat = new SimpleDateFormat("EEEE | dd/MM/yyyy HH:mm:ss");

				while (true) {
					final String time = simpleDateFormat.format(new Date());

					Platform.runLater(new Runnable() {
						@Override
						public void run() {
							dateTimeLabel.setText(time);
						}
					});
					try {
						Thread.sleep(1000); // 1 second
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		}).start();
	}

	private void setBrightness() {
		sliderBrightness.valueProperty().addListener(new ChangeListener<Number>() {
			@Override
			public void changed(ObservableValue<? extends Number> observableValue, Number oldValue, Number newValue) {
				try {
					BrightnessManager.setBrightness(Math.round(newValue.intValue()));
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}
		});
	}

}